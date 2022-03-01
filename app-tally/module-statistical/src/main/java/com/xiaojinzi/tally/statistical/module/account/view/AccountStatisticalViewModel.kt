package com.xiaojinzi.tally.statistical.module.account.view

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.support.tallyAccountService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountStatisticalViewModel : BaseViewModel() {

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val accountStatisticalOverviewObservableVO: Flow<AccountStatisticalOverviewVO> =
        tallyAccountService.allAccount
            .map { accountList ->
                val totalAsset = accountList.filter { it.balance >= 0 }
                    .map { it.balance }
                    .reduceOrNull { acc, item -> acc + item } ?: 0
                val debtAsset = accountList.filter { it.balance <= 0 }
                    .map { it.balance }
                    .reduceOrNull { acc, item -> acc + item } ?: 0

                AccountStatisticalOverviewVO(
                    totalAsset = totalAsset,
                    debtAsset = -debtAsset,
                )
            }

    /**
     * 所有账户的统计
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val accountGroupListObservableVO: Flow<List<AccountStatisticalGroupVO>> = tallyAccountService
        .allAccountWithType
        .map { accountList ->
            accountList
                .groupBy { it.accountType }
                .toList()
                .sortedByDescending { it.first.order }
                .map { pairItem ->
                    val accountTypeDTO = pairItem.first
                    AccountStatisticalGroupVO(
                        nameRsd = accountTypeDTO.nameRsd,
                        name = accountTypeDTO.name,
                        balance = pairItem.second.map { it.account.balance }
                            .reduceOrNull { acc, item -> acc + item } ?: 0,
                        items = pairItem.second.map { accountWithTypeItem ->
                            val accountDTO = accountWithTypeItem.account
                            AccountStatisticalItemVO(
                                isDefault = accountDTO.isDefault,
                                accountId = accountDTO.uid,
                                iconRsd = accountDTO.iconRsd,
                                nameRsd = accountDTO.nameRsd,
                                name = accountDTO.name,
                                balance = accountDTO.balance
                            )
                        }
                    )
                }
        }

    init {


    }

}