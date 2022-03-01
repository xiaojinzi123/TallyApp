package com.xiaojinzi.tally.account.module.select.view

import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.account.module.select.domain.AccountSelectUseCase
import com.xiaojinzi.tally.account.module.select.domain.AccountSelectUseCaseImpl
import com.xiaojinzi.tally.base.support.tallyAccountService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AccountSelectViewModel(
    private val useCase: AccountSelectUseCase = AccountSelectUseCaseImpl()
) : BaseViewModel(), AccountSelectUseCase by useCase {

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val accountListObservableVO: Flow<List<AccountSelectItemVO>> = combine(
        tallyAccountService.allAccount,
        useCase.userSelectIdObservable,
        useCase.userDisableIdsObservable,
    ) { accountList, userSelectId, userDisableIdList ->
        accountList
            .map {
                AccountSelectItemVO(
                    uid = it.uid,
                    isSelect = it.uid == userSelectId,
                    isDisable = it.uid in userDisableIdList,
                    iconRsd = it.iconRsd,
                    name = StringItemDTO(
                        nameRsd = it.nameRsd,
                        name = it.name,
                    ),
                    balance = it.balance,
                )
            }
            .filter { !it.isDisable }
    }

}