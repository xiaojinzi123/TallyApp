package com.xiaojinzi.tally.bill.module.monthly_list.domain

import androidx.paging.PagingData
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.tallyBillDetailQueryPagingService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.*

interface MonthlyBillListUseCase : BaseUseCase {

    /**
     * 时间戳
     */
    val dateTime: MutableInitOnceData<Long>

    /**
     * 当前月的数字
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currMonthNumber: Flow<Int>

    /**
     * 月账单的列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val monthBillDetailListObservableDTO: Flow<PagingData<BillDetailDayDTO>>

}

@FlowPreview
class MonthlyBillListUseCaseImpl : BaseUseCaseImpl(), MonthlyBillListUseCase {

    override val dateTime = MutableInitOnceData<Long>()

    override val currMonthNumber = dateTime.valueStateFlow.map { timeInMillis ->
        Calendar
            .getInstance()
            .apply {
                this.timeInMillis = timeInMillis
            }
            .get(Calendar.MONTH)
    }

    @ExperimentalCoroutinesApi
    override val monthBillDetailListObservableDTO = combine(
        dateTime.valueStateFlow,
        tallyService.dataBaseChangedObservable,
    ) { timeStamp, _ ->
        val (startTime, endTime) = getMonthInterval(timeStamp = timeStamp)
        tallyBillDetailQueryPagingService.subscribeCommonPageBillDetailObservable(
            billQueryCondition = BillQueryConditionDTO(
                startTime = startTime,
                endTime = endTime,
            )
        )
    }.flatMapLatest { it }

}
