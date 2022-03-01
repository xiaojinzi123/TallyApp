package com.xiaojinzi.tally.base.view

import com.xiaojinzi.module.base.view.ViewUseCase
import com.xiaojinzi.module.base.view.ViewUseCaseImpl
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.tally.base.service.statistical.DataStatisticalCostDTO
import com.xiaojinzi.tally.base.support.tallyDataStatisticalService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@ViewLayer
interface MonthStatisticalViewUseCase : ViewUseCase {

    /**
     * 当月的统计数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val currMonthStatisticalObservableVO: Flow<DataStatisticalCostDTO>

}

class MonthStatisticalViewUseCaseImpl(
    timeObservableDTO: Flow<Long> = flowOf(System.currentTimeMillis()),
) : ViewUseCaseImpl(), MonthStatisticalViewUseCase {

    // 当月的统计
    override val currMonthStatisticalObservableVO = timeObservableDTO
        .flatMapLatest {
            tallyDataStatisticalService
                .subscribeMonthCostObservableDTO(timeStamp = it)
        }

}