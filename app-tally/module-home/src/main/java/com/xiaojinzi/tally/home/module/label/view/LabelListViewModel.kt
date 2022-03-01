package com.xiaojinzi.tally.home.module.label.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.home.module.label.domain.LabelListUseCase
import com.xiaojinzi.tally.home.module.label.domain.LabelListUseCaseImpl

class LabelListViewModel(
    private val labelListUseCase: LabelListUseCase = LabelListUseCaseImpl()
) : BaseViewModel(), LabelListUseCase by labelListUseCase {
}