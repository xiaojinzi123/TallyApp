package com.xiaojinzi.tally.account.module.detail.view

import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.account.module.detail.domain.AccountDetailUseCase
import com.xiaojinzi.tally.account.module.detail.domain.AccountDetailUseCaseImpl
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountDetailViewModel(
    private val useCase: AccountDetailUseCase = AccountDetailUseCaseImpl(),
    private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.accountBillListObservableDTO
    ),
) : BaseViewModel(), AccountDetailUseCase by useCase,
    BillListViewUseCase by billListViewUseCase {

    val isShowMenuVO = MutableSharedStateFlow(initValue = false)

    /**
     * 账户详情
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val accountDetailVO: Flow<AccountDetailVO?> = accountObservableDTO
        .map { accountDTO ->
            accountDTO?.let {
                AccountDetailVO(
                    isDefault = it.isDefault,
                    accountId = it.uid,
                    accountName = StringItemDTO(
                        nameRsd = it.nameRsd,
                        name = it.name,
                    ),
                    initialBalance = it.initialBalance.tallyCostAdapter(),
                    balance = it.balance.tallyCostAdapter(),
                )
            }
        }

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

}
