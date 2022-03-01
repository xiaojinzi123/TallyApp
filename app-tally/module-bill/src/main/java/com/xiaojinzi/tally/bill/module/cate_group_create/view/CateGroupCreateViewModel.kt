package com.xiaojinzi.tally.bill.module.cate_group_create.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.bill.module.cate_group_create.domain.CateGroupCreateUseCase
import com.xiaojinzi.tally.bill.module.cate_group_create.domain.CateGroupCreateUseCaseImpl

class CateGroupCreateViewModel(
    private val useCase: CateGroupCreateUseCase = CateGroupCreateUseCaseImpl(),
): BaseViewModel(), CateGroupCreateUseCase by useCase {
}