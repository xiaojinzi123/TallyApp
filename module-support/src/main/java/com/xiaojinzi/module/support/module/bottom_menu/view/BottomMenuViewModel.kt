package com.xiaojinzi.module.support.module.bottom_menu.view

import com.xiaojinzi.module.support.module.bottom_menu.domain.BottomMenuUseCase
import com.xiaojinzi.module.support.module.bottom_menu.domain.BottomMenuUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class BottomMenuViewModel(
    private val useCase: BottomMenuUseCase = BottomMenuUseCaseImpl(),
) : BaseViewModel(), BottomMenuUseCase by useCase {
    // TODO
}