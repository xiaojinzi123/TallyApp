package com.xiaojinzi.tally.bill.module.monthly_list.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.view.*
import com.xiaojinzi.tally.bill.module.monthly_list.domain.MonthlyBillListUseCase
import com.xiaojinzi.tally.bill.module.monthly_list.domain.MonthlyBillListUseCaseImpl
import kotlinx.coroutines.FlowPreview

@FlowPreview
class MonthlyBillListViewModel(
    private val useCase: MonthlyBillListUseCase = MonthlyBillListUseCaseImpl(),
    private val monthStatisticalViewUseCase: MonthStatisticalViewUseCase = MonthStatisticalViewUseCaseImpl(
        timeObservableDTO = useCase.dateTime.valueStateFlow,
    ),
    private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.monthBillDetailListObservableDTO
    ),
) : BaseViewModel(),
    MonthStatisticalViewUseCase by monthStatisticalViewUseCase,
    MonthlyBillListUseCase by useCase,
    BillListViewUseCase by billListViewUseCase {

    override fun destroy() {
        useCase.destroy()
        monthStatisticalViewUseCase.destroy()
        billListViewUseCase.destroy()
    }

}