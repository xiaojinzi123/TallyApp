package com.xiaojinzi.tally.loading.module.loading.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.loading.module.loading.domain.LoadingUseCase
import com.xiaojinzi.tally.loading.module.loading.domain.LoadingUseCaseImpl

class LoadingViewModel(
    private val useCase: LoadingUseCase = LoadingUseCaseImpl()
): BaseViewModel(), LoadingUseCase by useCase {
}