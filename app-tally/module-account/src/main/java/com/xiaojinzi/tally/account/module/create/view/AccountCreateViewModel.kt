package com.xiaojinzi.tally.account.module.create.view

import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.account.module.create.domain.AccountCreateUseCase
import com.xiaojinzi.tally.account.module.create.domain.AccountCreateUseCaseImpl

@ViewLayer
class AccountCreateViewModel(
    private val useCase: AccountCreateUseCase = AccountCreateUseCaseImpl(),
): BaseViewModel(), AccountCreateUseCase by useCase {
}