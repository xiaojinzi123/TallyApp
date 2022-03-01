package com.xiaojinzi.tally.statistical.module.home.view

import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl
import com.xiaojinzi.tally.base.view.MonthStatisticalViewUseCase
import com.xiaojinzi.tally.base.view.MonthStatisticalViewUseCaseImpl
import com.xiaojinzi.tally.statistical.module.home.domain.HomeUseCase
import com.xiaojinzi.tally.statistical.module.home.domain.HomeUseCaseImpl

@ViewLayer
class HomeViewModel(
    private val useCase: HomeUseCase = HomeUseCaseImpl(),
    private val monthStatisticalViewUseCase: MonthStatisticalViewUseCase = MonthStatisticalViewUseCaseImpl(
        timeObservableDTO = useCase.currentMonthTimeObservableDTO,
    ),
    private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.currentMonthlyStatisticalObservableDTO
    ),
) : BaseViewModel(), HomeUseCase by useCase,
    @ViewLayer
    MonthStatisticalViewUseCase by monthStatisticalViewUseCase,
    @ViewLayer
    BillListViewUseCase by billListViewUseCase {

    override fun destroy() {
        useCase.destroy()
        monthStatisticalViewUseCase.destroy()
        billListViewUseCase.destroy()
    }

}