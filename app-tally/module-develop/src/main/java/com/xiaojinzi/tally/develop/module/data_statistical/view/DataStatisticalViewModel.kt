package com.xiaojinzi.tally.develop.module.data_statistical.view

import com.xiaojinzi.tally.develop.module.data_statistical.domain.DataStatisticalUseCase
import com.xiaojinzi.tally.develop.module.data_statistical.domain.DataStatisticalUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class DataStatisticalViewModel(
    private val useCase: DataStatisticalUseCase = DataStatisticalUseCaseImpl(),
) : BaseViewModel(), DataStatisticalUseCase by useCase {
    // TODO
}