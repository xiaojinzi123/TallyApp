package com.xiaojinzi.tally.datasource.service

import androidx.room.withTransaction
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.datasource.TallyBillLabelDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillLabelInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillLabelService
import com.xiaojinzi.tally.datasource.data.toTallyBillLabelDO
import com.xiaojinzi.tally.datasource.data.toTallyBillLabelDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ServiceAnno(TallyBillLabelService::class)
class TallyBillLabelServiceImpl : TallyBillLabelService {

    override suspend fun insert(target: TallyBillLabelInsertDTO): TallyBillLabelDTO {
        return insertList(targetList = listOf(target)).first()
    }

    override suspend fun insertIfNotExist(target: TallyBillLabelInsertDTO): TallyBillLabelDTO? {
        val dbDao = dbTally.tallyBillLabelDao()
        val isExist = dbDao.getByBillIdAndLabelId(billId = target.billId, labelId = target.labelId).isNotEmpty()
        return if (!isExist) {
            insert(target = target)
        } else {
            null
        }
    }

    override suspend fun insertList(targetList: List<TallyBillLabelInsertDTO>): List<TallyBillLabelDTO> {
        return withContext(context = Dispatchers.IO) {
            val dbDao = dbTally.tallyBillLabelDao()
            targetList
                .map { it.toTallyBillLabelDO() }
                .apply {
                    dbDao
                        .insertList(
                            target = this
                        )
                }
                .map {
                    dbDao.getById(uid = it.uid).toTallyBillLabelDTO()
                }
        }
    }

    override suspend fun deleteByBillIds(ids: List<String>) {
        dbTally.tallyBillLabelDao()
            .deleteByBillIds(billIdList = ids)
    }

    override suspend fun deleteByLabelIds(ids: List<String>) {
        dbTally.tallyBillLabelDao()
            .deleteByLabelIds(labelIdList = ids)
    }

    override suspend fun updateLabelList(targetBillId: String, labelIdList: List<String>) {
        dbTally.withTransaction {
            // 先删除
            deleteByBillIds(ids = listOf(targetBillId))
            insertList(
                targetList = labelIdList.map {
                    TallyBillLabelInsertDTO(
                        billId = targetBillId,
                        labelId = it
                    )
                }
            )
        }
    }

    override suspend fun getBillIdListByLabelIds(labelIds: List<String>): List<String> {
        if (labelIds.isNullOrEmpty()) {
            return emptyList()
        }
        return dbTally.tallyBillLabelDao().getBillIdListByLabelIds(labelIdList = labelIds)
    }

}