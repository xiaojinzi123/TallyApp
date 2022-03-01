package com.xiaojinzi.tally.statistical.module.core.domain

import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl

@ViewModelLayer
interface CoreStatisticalUseCase : BaseUseCase {

    /**
     * 月统计的逻辑处理类
     */
    @ViewModelLayer
    val monthlyStatisticalUseCase: MonthlyStatisticalUseCase

    /**
     * 年统计的逻辑处理类
     */
    @ViewModelLayer
    val yearlyStatisticalUseCase: YearlyStatisticalUseCase

}

class CoreStatisticalUseCaseImpl(
     override val monthlyStatisticalUseCase: MonthlyStatisticalUseCase = MonthlyStatisticalUseCaseImpl(),
     override val yearlyStatisticalUseCase: YearlyStatisticalUseCase = YearlyStatisticalUseCaseImpl(),
) : BaseUseCaseImpl(), CoreStatisticalUseCase{

    override fun destroy() {
        super.destroy()
        monthlyStatisticalUseCase.destroy()
        yearlyStatisticalUseCase.destroy()
    }

}