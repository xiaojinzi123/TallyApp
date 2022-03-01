package com.xiaojinzi.tally.base.service.statistical

import androidx.annotation.Keep
import com.xiaojinzi.module.base.support.getDayInterval
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.module.base.support.getYearInterval
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.CostTypeDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Cost 统计的对象
 */
@Keep
data class DataStatisticalCostDTO(
    val startTime: Long,
    val endTime: Long,
    // 收入类别的收入, 正负是不一定的
    val income: Long,
    // 支出类别对的支出, 正负是不一定的
    val spending: Long,
    // 真实的收入, 不可能 < 0
    val realIncome: Long,
    // 真实的收入, 不可能 < 0
    val realSpending: Long,
    // 结余
    val balance: Long = realIncome - realSpending
)


interface TallyCostDataStatisticalService {

    /**
     * 订阅某时间段的消费
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    fun subscribeTimeFrameCostObservableDTO(
        startTime: Long,
        endTime: Long,
    ): Flow<DataStatisticalCostDTO>

    /**
     * 订阅某日的消费
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    fun subscribeDayCostObservableDTO(timeStamp: Long): Flow<DataStatisticalCostDTO> {
        val (startTime, endTime) = getDayInterval(timeStamp = timeStamp)
        return subscribeTimeFrameCostObservableDTO(
            startTime = startTime,
            endTime = endTime,
        )
    }

    /**
     * 订阅某月的消费
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    fun subscribeMonthCostObservableDTO(timeStamp: Long): Flow<DataStatisticalCostDTO> {
        val (startTime, endTime) = getMonthInterval(timeStamp = timeStamp)
        return subscribeTimeFrameCostObservableDTO(
            startTime = startTime,
            endTime = endTime,
        )
    }

    /**
     * 订阅某年的消费
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    fun subscribeYearCostObservableDTO(timeStamp: Long): Flow<DataStatisticalCostDTO> {
        val (startTime, endTime) = getYearInterval(timeStamp = timeStamp)
        return subscribeTimeFrameCostObservableDTO(
            startTime = startTime,
            endTime = endTime,
        )
    }


}

class TallyCostDataStatisticalServiceImpl : TallyCostDataStatisticalService {

    override fun subscribeTimeFrameCostObservableDTO(
        startTime: Long,
        endTime: Long
    ): Flow<DataStatisticalCostDTO> {

        val baseCondition = BillQueryConditionDTO(
            startTime = startTime,
            endTime = endTime,
        )

        return tallyService
            .dataBaseChangedObservable
            .map {
                DataStatisticalCostDTO(
                    startTime = startTime,
                    endTime = endTime,
                    income = tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = baseCondition.copy(
                            cateGroupType = TallyCategoryGroupTypeDTO.Income
                        )
                    ),
                    spending = -tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = baseCondition.copy(
                            cateGroupType = TallyCategoryGroupTypeDTO.Spending
                        )
                    ),
                    realIncome = tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = baseCondition.copy(
                            costType = CostTypeDTO.Income,
                        )
                    ),
                    realSpending = -tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = baseCondition.copy(
                            costType = CostTypeDTO.Spending,
                        )
                    ),
                )
            }
    }

}