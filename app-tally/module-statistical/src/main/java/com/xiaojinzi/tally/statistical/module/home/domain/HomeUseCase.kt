package com.xiaojinzi.tally.statistical.module.home.domain


import android.content.Context
import androidx.paging.PagingData
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.component.withHostAndPath
import com.xiaojinzi.module.base.support.commonRouteResultService
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.util.LogSupport
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface HomeUseCase : BaseUseCase {

    companion object {
        const val TAG = "HomeUseCase"
    }

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val currentMonthTimeObservableDTO: Flow<Long>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectBookIdListObservableDTO: Flow<List<String>>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val filteredSelectBookIdListObservableVO: Flow<List<String>>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthlyStatisticalObservableDTO: Flow<PagingData<BillDetailDayDTO>>

    /**
     * 当月的结余预算
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthlyBudgetBalanceObservableDTO: Flow<Long?>

    /**
     * 当月的剩余预算
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthlyRemainBudgetObservableDTO: Flow<Long?>

    fun setSelectBookIdList(list: List<String>)

    /**
     * 去选择时间
     */
    fun toChooseTime(context: Context)

    /**
     * 去选择账本
     */
    fun toChooseBook(context: Context)

}

class HomeUseCaseImpl : BaseUseCaseImpl(), HomeUseCase {

    override val currentMonthTimeObservableDTO =
        MutableSharedStateFlow(initValue = System.currentTimeMillis())

    override val selectBookIdListObservableDTO = tallyBookService
        .allBillBookListObservable
        .map { list ->
            list.map {
                it.uid
            }
        }
        .sharedStateIn(
            initValue = emptyList(),
            scope = scope,
        )

    override val filteredSelectBookIdListObservableVO =
        combine(
            tallyBookService.allBillBookListObservable,
            selectBookIdListObservableDTO
        ) { bookList, selectIdList ->
            bookList
                .asSequence()
                .filter {
                    selectIdList.contains(it.uid)
                }
                .map {
                    it.uid
                }
                .toList()
        }

    @ExperimentalCoroutinesApi
    override val currentMonthlyStatisticalObservableDTO = combine(
        filteredSelectBookIdListObservableVO,
        currentMonthTimeObservableDTO,
        tallyService.dataBaseChangedObservable
    ) { bookIdList, monthTime, _ ->
        val (startTime, endTime) = getMonthInterval(timeStamp = monthTime)
        LogSupport.d(tag = HomeUseCase.TAG, content = "startTime = $startTime, endTime = $endTime")
        tallyBillDetailQueryPagingService
            .subscribeCommonPageBillDetailObservable(
                billQueryCondition = BillQueryConditionDTO(
                    businessLogKey = HomeUseCase.TAG,
                    bookIdList = bookIdList,
                    startTime = startTime,
                    endTime = endTime,
                )
            )
    }.flatMapLatest { it }

    override val currentMonthlyBudgetBalanceObservableDTO = combine(
        tallyBudgetService.isCumulativeBudgetObservableDTO,
        currentMonthTimeObservableDTO,
        tallyService.dataBaseChangedObservable,
    ) { isCumulativeBudget, monthTime, _ ->
        if (isCumulativeBudget) {
            tallyBudgetService.getMonthBudgetBalanceValue(monthTime = monthTime)
        } else {
            tallyBudgetService.getMonthBudgetValue(monthTime = monthTime)
        }
    }

    override val currentMonthlyRemainBudgetObservableDTO = combine(
        currentMonthlyBudgetBalanceObservableDTO,
        currentMonthTimeObservableDTO
            .flatMapLatest { timeStamp ->
                tallyDataStatisticalService.subscribeMonthCostObservableDTO(timeStamp = timeStamp)
                    .map { it.realSpending }
            }
    ) { currentMonthlyBudgetBalance, currentMonthlySpending ->
        if (currentMonthlyBudgetBalance == null) {
            null
        } else {
            currentMonthlyBudgetBalance - currentMonthlySpending
        }
    }

    override fun setSelectBookIdList(list: List<String>) {
        selectBookIdListObservableDTO.value = list
    }

    override fun toChooseTime(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            currentMonthTimeObservableDTO.value = commonRouteResultService.selectDateTime(
                context = context,
                dateTime = currentMonthTimeObservableDTO.value,
            )
        }
    }

    override fun toChooseBook(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val selectIdList = context
                .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_BILL_BOOK_SELECT)
                .putStringArray("selectIdList", selectBookIdListObservableDTO.value.toTypedArray())
                .requestCodeRandom()
                .intentAwait()
                .getStringArrayExtra("data")
                ?.toList() ?: emptyList()
            selectBookIdListObservableDTO.value = selectIdList
        }
    }

}
