package com.xiaojinzi.tally.statistical.module.calendar.domain

import androidx.paging.PagingData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.getDayInterval
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.tallyBillDetailQueryPagingService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*

interface CalendarStatisticalUseCase : BaseUseCase {

    val yearDTO: MutableSharedStateFlow<Int>
    val monthDTO: MutableSharedStateFlow<Int>
    val dayOfMonthDTO: MutableSharedStateFlow<Int>

    val dateTimeOfDaySelectDTO: Flow<Long>

    /**
     * 选择的那天的账单列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dayBillDetailListObservableDTO: Flow<PagingData<BillDetailDayDTO>>

}

class CalendarStatisticalUseCaseImpl : BaseUseCaseImpl(), CalendarStatisticalUseCase {

    private val calendar = Calendar.getInstance(Locale.getDefault())

    override val yearDTO = MutableSharedStateFlow(
        initValue = calendar.get(Calendar.YEAR)
    )

    override val monthDTO = MutableSharedStateFlow(
        initValue = calendar.get(Calendar.MONTH)
    )

    override val dayOfMonthDTO = MutableSharedStateFlow(
        initValue = calendar.get(Calendar.DAY_OF_MONTH)
    )

    override val dateTimeOfDaySelectDTO = combine(
        yearDTO.distinctUntilChanged(),
        monthDTO.distinctUntilChanged(),
        dayOfMonthDTO.distinctUntilChanged(),
    ) { year, month, dayOfMonth ->
        Calendar.getInstance(Locale.getDefault())
            .apply {
                this.set(Calendar.YEAR, year)
                this.set(Calendar.MONTH, month)
                this.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // 意思一下弄个 1
                // this.set(Calendar.HOUR_OF_DAY, 1)
            }
            .timeInMillis
    }

    @ExperimentalCoroutinesApi
    override val dayBillDetailListObservableDTO =
        combine(
            dateTimeOfDaySelectDTO,
            tallyService.dataBaseChangedObservable,
        ) { timeStamp, _ ->
            timeStamp
        }.map { timeStamp ->
            val (startTime, endTime) = getDayInterval(timeStamp = timeStamp)
            tallyBillDetailQueryPagingService.subscribeCommonPageBillDetailObservable(
                billQueryCondition = BillQueryConditionDTO(
                    startTime = startTime,
                    endTime = endTime,
                )
            )
        }.flatMapLatest { it }

}
