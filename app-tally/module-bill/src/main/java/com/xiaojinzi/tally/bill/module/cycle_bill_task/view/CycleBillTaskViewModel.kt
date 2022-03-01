package com.xiaojinzi.tally.bill.module.cycle_bill_task.view

import com.xiaojinzi.tally.bill.module.cycle_bill_task.domain.CycleBillTaskUseCase
import com.xiaojinzi.tally.bill.module.cycle_bill_task.domain.CycleBillTaskUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class CycleBillTaskViewModel(
    private val useCase: CycleBillTaskUseCase = CycleBillTaskUseCaseImpl(),
) : BaseViewModel(), CycleBillTaskUseCase by useCase {
    // TODO
}