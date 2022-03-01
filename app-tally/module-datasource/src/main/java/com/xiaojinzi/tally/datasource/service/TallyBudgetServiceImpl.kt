package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.module.base.support.spPersistence
import com.xiaojinzi.module.base.support.spPersistenceNonNull
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.datasource.data.toTallyBudgetDO
import com.xiaojinzi.tally.datasource.data.toTallyBudgetDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.flow.map

@ServiceAnno(TallyBudgetService::class)
class TallyBudgetServiceImpl : TallyBudgetService {

    override val isCumulativeBudgetObservableDTO = MutableSharedStateFlow<Boolean>(
        initValue = false
    ).spPersistenceNonNull(
        key = "isCumulativeBudget",
        def = false
    )

    override val monthlyDefaultBudgetObservableDTO = MutableSharedStateFlow<Long?>().spPersistence(
        // sp 存储
        key = "monthlyDefaultBudget",
        def = null,
    )

    override val isSetMonthlyDefaultBudgetObservableDTO =
        monthlyDefaultBudgetObservableDTO.map { it != null }

    override suspend fun insert(target: TallyBudgetInsertDTO) {
        dbTally
            .allyBudgetDao()
            .insert(
                target = target.toTallyBudgetDO()
            )
    }

    override suspend fun delete(target: TallyBudgetDTO) {
        dbTally
            .allyBudgetDao()
            .delete(target = target.toTallyBudgetDO())
    }

    override suspend fun insertOrUpdate(target: TallyBudgetInsertDTO) {
        val targetDTO = getByMonthTime(monthTime = target.monthTime)
        if (targetDTO == null) {
            insert(target = target)
        } else {
            dbTally
                .allyBudgetDao()
                .update(
                    target = targetDTO
                        .copy(
                            value = target.value
                        )
                        .toTallyBudgetDO()
                )
        }
    }

    override suspend fun getByMonthTime(monthTime: Long): TallyBudgetDTO? {
        val monthTimeStr = TallyBudgetService.MONTH_TIME_FORMAT.format(monthTime)
        return dbTally
            .allyBudgetDao()
            .getByMonth(month = monthTimeStr)
            ?.toTallyBudgetDTO()
    }

    override suspend fun getAllBeforeSpecialTime(monthTime: Long): List<TallyBudgetDTO> {
        val monthTimeStr = TallyBudgetService.MONTH_TIME_FORMAT.format(monthTime)
        return dbTally
            .allyBudgetDao()
            .getAllBeforeSpecialTime(month = monthTimeStr)
            .map { it.toTallyBudgetDTO() }
    }

    override suspend fun getBudgetBalanceBeforeSpecialTime(monthTime: Long): Long? {
        // 这个月时间之前设置的预算
        val monthBudgetList = getAllBeforeSpecialTime(monthTime = monthTime)
        val minMonthTime = monthBudgetList.map { it.monthTime }.minOrNull()?: return null
        val maxMonthTime = monthBudgetList.map { it.monthTime }.minOrNull()?: return null
        val (minMonthStartTime, minMonthEndTime) = getMonthInterval(timeStamp = minMonthTime)
        val (maxMonthStartTime, maxMonthEndTime) = getMonthInterval(timeStamp = maxMonthTime)
        // 支出的费用高,
        val costSpending = -1 * tallyBillService.getNormalBillCostAdjustByCondition(
            condition = BillQueryConditionDTO(
                costType = CostTypeDTO.Spending,
                startTime = minMonthStartTime,
                endTime = maxMonthEndTime,
            )
        )
        // 这个时间点之前的总预算
        val totalBudget: Long = monthBudgetList.map { it.value }.reduceOrNull { acc, item -> acc + item }?: return null
        return totalBudget - costSpending
    }

    override suspend fun getMonthBudgetValue(monthTime: Long): Long? {
        return getByMonthTime(monthTime = monthTime)?.value
    }

    override suspend fun getMonthBudgetBalanceValue(monthTime: Long): Long? {
        val monthBudget = getByMonthTime(monthTime = monthTime)?.value?: return null
        val beforeBudgetBalance = getBudgetBalanceBeforeSpecialTime(monthTime = monthTime)?: 0
        return monthBudget + beforeBudgetBalance
    }

}