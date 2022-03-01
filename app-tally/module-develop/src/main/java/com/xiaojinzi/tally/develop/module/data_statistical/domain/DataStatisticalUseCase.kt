package com.xiaojinzi.tally.develop.module.data_statistical.domain

import android.content.Context
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.service.statistical.DataStatisticalCostDTO
import com.xiaojinzi.tally.base.support.tallyDataStatisticalService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ViewModelLayer
interface DataStatisticalUseCase : BaseUseCase {

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dayTimeObservableDTO: Flow<Long>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val monthTimeObservableDTO: Flow<Long>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val yearTimeObservableDTO: Flow<Long>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dayCostObservableDTO: Flow<DataStatisticalCostDTO>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val monthCostObservableDTO: Flow<DataStatisticalCostDTO>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val yearCostObservableDTO: Flow<DataStatisticalCostDTO>

    fun selectDayTime(context: Context)

    fun selectMonthTime(context: Context)

    fun selectYearTime(context: Context)

}

@ViewModelLayer
class DataStatisticalUseCaseImpl : BaseUseCaseImpl(), DataStatisticalUseCase {

    override val dayTimeObservableDTO =
        MutableSharedStateFlow(initValue = System.currentTimeMillis())

    override val monthTimeObservableDTO =
        MutableSharedStateFlow(initValue = System.currentTimeMillis())

    override val yearTimeObservableDTO =
        MutableSharedStateFlow(initValue = System.currentTimeMillis())

    override val dayCostObservableDTO = dayTimeObservableDTO
        .flatMapLatest { timeStamp ->
            tallyDataStatisticalService.subscribeDayCostObservableDTO(timeStamp = timeStamp)
        }

    override val monthCostObservableDTO = monthTimeObservableDTO
        .flatMapLatest { timeStamp ->
            tallyDataStatisticalService.subscribeMonthCostObservableDTO(timeStamp = timeStamp)
        }

    override val yearCostObservableDTO = monthTimeObservableDTO
        .flatMapLatest { timeStamp ->
            tallyDataStatisticalService.subscribeMonthCostObservableDTO(timeStamp = timeStamp)
        }

    private suspend fun selectTime(
        context: Context,
        defTime: Long = System.currentTimeMillis()
    ): Long {
        return Router.with(context)
            .hostAndPath(RouterConfig.SYSTEM_DATE_TIME_PICKER)
            .requestCodeRandom()
            .intentAwait()
            .getLongExtra("dateTime", defTime)
    }

    override fun selectDayTime(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            dayTimeObservableDTO.value = selectTime(
                context = context
            )
        }
    }

    override fun selectMonthTime(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            monthTimeObservableDTO.value = selectTime(
                context = context
            )
        }
    }

    override fun selectYearTime(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            yearTimeObservableDTO.value = selectTime(
                context = context
            )
        }
    }

}