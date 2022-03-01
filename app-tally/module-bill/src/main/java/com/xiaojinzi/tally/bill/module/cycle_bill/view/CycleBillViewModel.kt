package com.xiaojinzi.tally.bill.module.cycle_bill.view

import com.xiaojinzi.tally.bill.module.cycle_bill.domain.CycleBillUseCase
import com.xiaojinzi.tally.bill.module.cycle_bill.domain.CycleBillUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class CycleBillViewModel(
    private val useCase: CycleBillUseCase = CycleBillUseCaseImpl(),
) : BaseViewModel(), CycleBillUseCase by useCase {
    // TODO
}