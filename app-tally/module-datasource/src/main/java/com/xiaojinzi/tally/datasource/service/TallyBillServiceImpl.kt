package com.xiaojinzi.tally.datasource.service

import androidx.room.withTransaction
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.getDayInterval
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.support.tallyBillLabelService
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyImageService
import com.xiaojinzi.tally.base.support.tallyService
import com.xiaojinzi.tally.datasource.data.toTallBillDO
import com.xiaojinzi.tally.datasource.data.toTallyBillDO
import com.xiaojinzi.tally.datasource.data.toTallyBillDTO
import com.xiaojinzi.tally.datasource.data.toTallyBillDetailDTO
import com.xiaojinzi.tally.datasource.db.BillDetailPageQueryType
import com.xiaojinzi.tally.datasource.db.SupportBillDetailPageQueryImpl
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext


@ServiceAnno(TallyBillService::class)
class TallyBillServiceImpl : TallyBillService {

    private val currentMonthIntervalPair = getMonthInterval(timeStamp = System.currentTimeMillis())

    override val billCountObservable = MutableSharedFlow<Long>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    ).apply {
        dbTally
            .tallyBillDao().subscribeCount()
            .onEach {
                this.emit(value = it)
            }
            .flowOn(Dispatchers.IO)
            .launchIn(scope = AppScope)
    }

    override suspend fun bookBillTransfer(oldBookId: String, newBookId: String) {
        val dbDao = dbTally.tallyBillDao()
        tallyService.withTransaction {
            dbDao.bookIdBillTransfer(
                oldBookId = oldBookId,
                newBookId = newBookId,
            )
        }
    }

    override suspend fun accountBillTransfer(oldAccountId: String, newAccountId: String) {
        val dbDao = dbTally.tallyBillDao()
        tallyService.withTransaction {
            dbDao.accountIdBillTransfer(
                oldAccountId = oldAccountId,
                newAccountId = newAccountId,
            )
            dbDao.transferTargetAccountIdBillTransfer(
                oldAccountId = oldAccountId,
                newAccountId = newAccountId,
            )
        }
    }

    override suspend fun categoryBillTransfer(oldCategoryId: String, newCategoryId: String) {
        val dbDao = dbTally.tallyBillDao()
        dbDao.categoryIdBillTransfer(
            oldCategoryId = oldCategoryId,
            newCategoryId = newCategoryId,
        )
    }

    override suspend fun getById(id: String): TallyBillDTO? {
        return dbTally.tallyBillDao().getById(uid = id)?.toTallyBillDTO()
    }

    override suspend fun getDetailById(id: String): TallyBillDetailDTO? {
        return dbTally.tallyBillDao().getDetailById(uid = id)?.toTallyBillDetailDTO()
    }

    override suspend fun updateCostAdjust(billId: String) {
        val targetBill = getById(id = billId) ?: return
        if (targetBill.type == TallyBillTypeDTO.Reimbursement) {
            updateCostAdjust(billId = targetBill.reimburseBillId!!)
        } else {
            // ????????????????????????????????????
            val reimbursedCost = tallyBillService
                .getBillCostByCondition(
                    condition = BillQueryConditionDTO(
                        billTypes = listOf(
                            TallyBillTypeDTO.Reimbursement
                        ),
                        reimburseBillIdList = listOf(
                            targetBill.uid,
                        )
                    )
                )
            val targetCostAdjust = targetBill.cost + reimbursedCost
            if (targetBill.costAdjust != targetCostAdjust) {
                dbTally
                    .tallyBillDao()
                    .update(
                        target = targetBill.copy(
                            costAdjust = targetCostAdjust,
                        ).toTallyBillDO()
                    )
            }
        }
    }

    override suspend fun update(
        target: TallyBillDTO,
        labelIdList: List<String>?,
        imageUrlList: List<String>?,
    ) {
        target.check()
        val dao = dbTally.tallyBillDao()
        // ???????????????????????????, ???????????????????????????, ??????????????????
        if (target.type == TallyBillTypeDTO.Transfer) {
            val reimburseBillIds =
                dao.getIdListByReimburseBillIds(reimburseBillIds = listOf(target.uid))
            if (reimburseBillIds.isNotEmpty()) {
                notSupportError()
            }
        }

        dbTally.withTransaction {
            // ????????????
            dao.update(
                target = target
                    // ?????????????????? costAdjust = cost, ?????????????????????
                    .copy(
                        costAdjust = target.cost,
                    )
                    .toTallyBillDO()
            )
            // ?????? costAdjust ??????
            updateCostAdjust(billId = target.uid)
            // ??????????????????????????????????????????
            val reimburseBillIds = dbTally.tallyBillDao()
                .getIdListByReimburseBillIds(reimburseBillIds = listOf(target.uid))
            reimburseBillIds
                .mapNotNull {
                    dao.getById(uid = it)
                }
                .forEach {
                    dao.update(
                        target = it.copy(
                            categoryId = target.categoryId,
                            bookId = target.bookId,
                        )
                    )
                }
            labelIdList?.let {
                // ????????????
                tallyBillLabelService.updateLabelList(
                    billId = target.uid,
                    labelIdList = labelIdList
                )
            }
            imageUrlList?.let {
                // ????????????
                tallyImageService.deleteByKey1(key1 = target.uid)
                tallyImageService.insertList(
                    targetList = imageUrlList
                        .map { url ->
                            TallyImageInsertDTO(
                                key1 = target.uid,
                                url = url
                            )
                        }
                )
            }
        }
    }

    override suspend fun insertBill(
        targetBill: TallyBillInsertDTO,
        labelIdList: List<String>,
        imageUrlList: List<String>,
    ): TallyBillDTO {
        targetBill.check()
        val targetBillId = dbTally.withTransaction {
            // ????????????
            val billId = insertBills(
                targetList = listOf(targetBill)
            ).first().uid
            // ????????????
            tallyBillLabelService.insertList(
                labelIdList.map { labelId ->
                    TallyBillLabelInsertDTO(
                        billId = billId,
                        labelId = labelId,
                    )
                }
            )
            // ????????????
            tallyImageService.insertList(
                targetList = imageUrlList
                    .map { url ->
                        TallyImageInsertDTO(
                            key1 = billId,
                            url = url
                        )
                    }
            )
            billId
        }
        return getById(id = targetBillId)!!
    }

    override suspend fun insertBills(targetList: List<TallyBillInsertDTO>): List<TallyBillDTO> {
        return withContext(context = Dispatchers.IO) {
            dbTally.withTransaction {
                targetList.forEach { it.check() }
                val resultList = targetList
                    .map { it.toTallBillDO() }
                    .apply {
                        dbTally
                            .tallyBillDao()
                            .insertList(
                                value = this
                            )
                    }
                    .map { it.uid }
                    .map {
                        dbTally
                            .tallyBillDao()
                            .getById(uid = it)!!
                            .toTallyBillDTO()
                    }
                resultList.forEach {
                    updateCostAdjust(billId = it.uid)
                }
                resultList
            }
        }
    }

    override suspend fun getAll(): List<TallyBillDTO> {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyBillDao().getAll()
                .map {
                    it.toTallyBillDTO()
                }
        }
    }

    override suspend fun deleteById(id: String) {
        return deleteByIds(ids = listOf(id))
    }

    override suspend fun deleteByIds(ids: List<String>) {
        withContext(context = Dispatchers.IO) {
            dbTally.withTransaction {
                val needUpdateCostAdjustIds = dbTally
                    .tallyBillDao()
                    .getReimburseBillIdListByIds(ids = ids)
                // ?????????????????????
                tallyBillLabelService.deleteByBillIds(ids = ids)
                // ????????????
                dbTally.tallyBillDao().deleteByIds(targetList = ids)
                // ???????????? id ????????????????????????????????????
                val newIds = dbTally
                    .tallyBillDao()
                    .getIdListByReimburseBillIds(reimburseBillIds = ids)
                // ?????????????????????
                dbTally.tallyBillDao().deleteByIds(targetList = newIds)
                // ?????? costAdjust
                needUpdateCostAdjustIds.forEach { billId ->
                    updateCostAdjust(billId = billId)
                }
            }
        }
    }

    override suspend fun deleteByCategoryId(categoryId: String) {
        val dbDao = dbTally.tallyBillDao()
        val ids = dbDao.getIdListByCategoryId(categoryId = categoryId)
        deleteByIds(ids = ids)
    }

    override suspend fun deleteByBookId(bookId: String) {
        val dbDao = dbTally.tallyBillDao()
        val ids = dbDao.getIdListByBookId(bookId = bookId)
        deleteByIds(ids = ids)
    }

    override suspend fun deleteAboutAccountId(accountId: String) {
        val ids = dbTally
            .tallyBillDao()
            .getIdListAboutAccountId(accountId = accountId)
        deleteByIds(ids = ids)
    }

    override suspend fun getCountAboutAccountId(accountId: String): Int {
        return dbTally
            .tallyBillDao()
            .getCountAboutAccountId(accountId = accountId)
    }

    override suspend fun getMonthlyBillDetailList(timeStamp: Long): List<TallyBillDetailDTO> {
        return withContext(context = Dispatchers.IO) {
            val (startTime, endTime) = getMonthInterval(timeStamp = timeStamp)
            getBillListByCondition(
                condition = BillQueryConditionDTO(
                    startTime = startTime,
                    endTime = endTime,
                )
            )
        }
    }

    override suspend fun getBillDetailListByDay(timeStamp: Long): List<TallyBillDetailDTO> {
        val (startTime, endTime) = getDayInterval(timeStamp = timeStamp)
        return withContext(context = Dispatchers.IO) {
            getBillListByCondition(
                condition = BillQueryConditionDTO(
                    startTime = startTime,
                    endTime = endTime,
                )
            )
        }
    }

    override suspend fun getAccountSpendingCost(accountId: String): Long {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyBillDao()
                .getAllCostByAccountId(accountId = accountId)
                .reduceOrNull { acc, item -> acc + item } ?: 0
        }
    }

    override suspend fun getAccountIncomeCost(accountId: String): Long {
        return withContext(context = Dispatchers.IO) {
            // ???????????? * -1
            val result = dbTally.tallyBillDao()
                .getAllCostByTransferTargetAccountId(accountId = accountId)
                .reduceOrNull { acc, item -> acc + item } ?: 0
            -result
        }
    }

    override suspend fun getCountByCondition(conditionBill: BillQueryConditionDTO): Int {
        return withContext(context = Dispatchers.IO) {
            // ???????????? * -1
            dbTally.tallyBillDao()
                .getCount(
                    condition = SupportBillDetailPageQueryImpl(
                        queryType = BillDetailPageQueryType.Count,
                        billQueryCondition = conditionBill,
                    )
                )
        }
    }

    override suspend fun getBillIdListByCondition(condition: BillQueryConditionDTO): List<String> {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyBillDao()
                .queryBillIdList(
                    condition = SupportBillDetailPageQueryImpl(
                        queryType = BillDetailPageQueryType.Id,
                        billQueryCondition = condition,
                    )
                )
        }
    }

    override suspend fun getBillListByCondition(condition: BillQueryConditionDTO): List<TallyBillDetailDTO> {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyBillDao()
                .queryPageBillDetail(
                    condition = SupportBillDetailPageQueryImpl(
                        queryType = BillDetailPageQueryType.Detail,
                        billQueryCondition = condition,
                    )
                )
                .map { it.toTallyBillDetailDTO() }
        }
    }

    override suspend fun getBillCostByCondition(condition: BillQueryConditionDTO): Long {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyBillDao()
                .getCostList(
                    condition = SupportBillDetailPageQueryImpl(
                        queryType = BillDetailPageQueryType.Cost,
                        billQueryCondition = condition,
                    )
                )
                .reduceOrNull { acc, item -> acc + item } ?: 0
        }
    }

    override suspend fun getBillCostAdjustByCondition(condition: BillQueryConditionDTO): Long {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyBillDao()
                .getCostList(
                    condition = SupportBillDetailPageQueryImpl(
                        queryType = BillDetailPageQueryType.CostAdjust,
                        billQueryCondition = condition,
                    )
                )
                .reduceOrNull { acc, item -> acc + item } ?: 0
        }
    }

    override suspend fun getCommonBillCostByCondition(condition: BillQueryConditionDTO): Long {
        return getBillCostByCondition(
            condition = condition.copy(
                billTypes = listOf(
                    TallyBillTypeDTO.Normal,
                    TallyBillTypeDTO.Reimbursement,
                ),
            )
        )
    }

    override suspend fun getNormalBillCostAdjustByCondition(condition: BillQueryConditionDTO): Long {
        return getBillCostAdjustByCondition(
            condition = condition.copy(
                billTypes = listOf(
                    TallyBillTypeDTO.Normal,
                ),
            )
        )
    }

    override suspend fun getCountByBookId(billBookId: String): Int {
        return dbTally
            .tallyBillDao()
            .getCountByBookId(bookId = billBookId)
    }

}