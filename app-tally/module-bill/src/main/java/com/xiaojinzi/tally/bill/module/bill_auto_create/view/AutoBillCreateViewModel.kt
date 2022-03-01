package com.xiaojinzi.tally.bill.module.bill_auto_create.view

import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.bill.module.bill_auto_create.domain.AutoBillCreateUseCase
import com.xiaojinzi.tally.bill.module.bill_auto_create.domain.AutoBillCreateUseCaseImpl
import com.xiaojinzi.tally.bill.module.bill_create.domain.BillCreateUseCase
import com.xiaojinzi.tally.bill.module.bill_create.domain.BillCreateUseCaseImpl

@ViewLayer
class AutoBillCreateViewModel(
    private val useCase: AutoBillCreateUseCase = AutoBillCreateUseCaseImpl(),
) : BaseViewModel(), AutoBillCreateUseCase by useCase {
    // TODO
}