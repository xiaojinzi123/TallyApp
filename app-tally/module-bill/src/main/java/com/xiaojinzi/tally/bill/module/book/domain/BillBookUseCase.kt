package com.xiaojinzi.tally.bill.module.book.domain

import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl

@ViewModelLayer
interface BillBookUseCase : BaseUseCase {
}

@ViewModelLayer
class BillBookUseCaseImpl : BaseUseCaseImpl(), BillBookUseCase {
}
