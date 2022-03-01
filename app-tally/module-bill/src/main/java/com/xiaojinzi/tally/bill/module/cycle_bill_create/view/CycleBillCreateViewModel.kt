package com.xiaojinzi.tally.bill.module.cycle_bill_create.view

import com.xiaojinzi.tally.bill.module.cycle_bill_create.domain.CycleBillCreateUseCase
import com.xiaojinzi.tally.bill.module.cycle_bill_create.domain.CycleBillCreateUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class CycleBillCreateViewModel(
    private val useCase: CycleBillCreateUseCase = CycleBillCreateUseCaseImpl(),
) : BaseViewModel(), CycleBillCreateUseCase by useCase {
    // TODO
}