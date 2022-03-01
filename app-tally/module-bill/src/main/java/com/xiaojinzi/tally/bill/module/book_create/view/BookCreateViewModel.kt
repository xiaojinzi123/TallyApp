package com.xiaojinzi.tally.bill.module.book_create.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.bill.module.book_create.domain.BookCreateUseCase
import com.xiaojinzi.tally.bill.module.book_create.domain.BookCreateUseCaseImpl

class BookCreateViewModel(
    private val useCase: BookCreateUseCase = BookCreateUseCaseImpl(),
): BaseViewModel(), BookCreateUseCase by useCase {
}