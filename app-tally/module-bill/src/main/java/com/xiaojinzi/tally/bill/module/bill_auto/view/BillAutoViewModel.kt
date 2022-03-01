package com.xiaojinzi.tally.bill.module.bill_auto.view

import com.xiaojinzi.tally.bill.module.bill_auto.domain.BillAutoUseCase
import com.xiaojinzi.tally.bill.module.bill_auto.domain.BillAutoUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class BillAutoViewModel(
    private val useCase: BillAutoUseCase = BillAutoUseCaseImpl(),
) : BaseViewModel(), BillAutoUseCase by useCase {
}