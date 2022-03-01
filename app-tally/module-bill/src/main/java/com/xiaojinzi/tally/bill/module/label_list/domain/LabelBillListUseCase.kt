package com.xiaojinzi.tally.bill.module.label_list.domain

import androidx.paging.PagingData
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.tallyBillDetailQueryPagingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

interface LabelBillListUseCase : BaseUseCase {

    val targetMonthTimeData: MutableInitOnceData<Long?>

    val targetLabelIdData: MutableInitOnceData<String>

    /**
     * 当前标签下面的所有账单
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val labelBillListObservableDTO: Flow<PagingData<BillDetailDayDTO>>

}

class LabelBillListUseCaseImpl : BaseUseCaseImpl(), LabelBillListUseCase {

    override val targetMonthTimeData = MutableInitOnceData<Long?>()

    override val targetLabelIdData = MutableInitOnceData<String>()

    @ExperimentalCoroutinesApi
    @FlowPreview
    override val labelBillListObservableDTO =
        combine(
            targetMonthTimeData.valueStateFlow,
            targetLabelIdData.valueStateFlow,
        ) { monthTimeStamp, _ ->
            val (startTime, endTime) = monthTimeStamp?.let { timeStamp ->
                getMonthInterval(timeStamp = timeStamp)
            } ?: Pair(
                first = null,
                second = null,
            )
            tallyBillDetailQueryPagingService.subscribeCommonPageBillDetailObservable(
                billQueryCondition = BillQueryConditionDTO(
                    startTime = startTime,
                    endTime = endTime,
                )
            )
        }.flatMapLatest { it }

}
