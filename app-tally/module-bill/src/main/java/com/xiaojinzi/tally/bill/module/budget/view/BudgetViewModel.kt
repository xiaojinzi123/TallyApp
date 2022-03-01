package com.xiaojinzi.tally.bill.module.budget.view

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.bill.module.budget.domain.BudgetUseCase
import com.xiaojinzi.tally.bill.module.budget.domain.BudgetUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@ViewLayer
class BudgetViewModel(
    private val useCase: BudgetUseCase = BudgetUseCaseImpl(),
) : BaseViewModel(), BudgetUseCase by useCase {

    /**
     * 当月预算
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthBudgetObservableVO: Flow<MonthBudgetBoardVO?> = combine(
        currentMonthBudgetBalanceValueAdapterObservableDTO,
        currentMonthSpendingObservableDTO
    ) { currentMonthBudgetBalanceValueAdapter, monthSpending ->
        currentMonthBudgetBalanceValueAdapter?.let {
            MonthBudgetBoardVO(
                value = it.tallyCostAdapter(),
                valueRemain = (it - monthSpending).tallyCostAdapter(),
            )
        }
    }

}