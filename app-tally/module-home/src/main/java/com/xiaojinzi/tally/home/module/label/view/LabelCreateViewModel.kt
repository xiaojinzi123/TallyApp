package com.xiaojinzi.tally.home.module.label.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.home.module.label.domain.LabelCreateUseCase
import com.xiaojinzi.tally.home.module.label.domain.LabelCreateUseCaseImpl

class LabelCreateViewModel(
    private val useCase: LabelCreateUseCase = LabelCreateUseCaseImpl(),
) : BaseViewModel(),
    LabelCreateUseCase by useCase {
}