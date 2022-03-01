package com.xiaojinzi.module.system.module.datetime.view

import com.xiaojinzi.module.system.module.datetime.domain.YearSelectUseCase
import com.xiaojinzi.module.system.module.datetime.domain.YearSelectUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel

class YearSelectViewModel(
    private val useCase: YearSelectUseCase = YearSelectUseCaseImpl(),
    ) : BaseViewModel(), YearSelectUseCase by useCase