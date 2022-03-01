package com.xiaojinzi.tally.bill.module.cate_create.view

import com.xiaojinzi.tally.bill.module.cate_create.domain.CateCreateUseCase
import com.xiaojinzi.tally.bill.module.cate_create.domain.CateCreateUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class CateCreateViewModel(
    private val useCase: CateCreateUseCase = CateCreateUseCaseImpl(),
) : BaseViewModel(), CateCreateUseCase by useCase {
    // TODO
}