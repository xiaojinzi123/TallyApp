package com.xiaojinzi.module.support.module.fill_in_number.view

import com.xiaojinzi.module.support.module.fill_in_number.domain.FillInNumberUseCase
import com.xiaojinzi.module.support.module.fill_in_number.domain.FillInNumberUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class FillInNumberViewModel(
    private val useCase: FillInNumberUseCase = FillInNumberUseCaseImpl(),
) : BaseViewModel(), FillInNumberUseCase by useCase {
    // TODO
}