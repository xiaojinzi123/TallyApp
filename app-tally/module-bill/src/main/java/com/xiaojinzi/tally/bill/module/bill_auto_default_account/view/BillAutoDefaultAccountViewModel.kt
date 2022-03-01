package com.xiaojinzi.tally.bill.module.bill_auto_default_account.view

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.bill.module.bill_auto_default_account.domain.BillAutoDefaultAccountUseCase
import com.xiaojinzi.tally.bill.module.bill_auto_default_account.domain.BillAutoDefaultAccountUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ViewLayer
class BillAutoDefaultAccountViewModel(
    private val useCase: BillAutoDefaultAccountUseCase = BillAutoDefaultAccountUseCaseImpl(),
) : BaseViewModel(), BillAutoDefaultAccountUseCase by useCase {

    /**
     * 显示的列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dataListObservableVO: Flow<List<BillAutoDefaultAccountItemVO>> = dataListObservableDTO
        .map { list ->
            list.map {
                BillAutoDefaultAccountItemVO(
                    uid = it.core.uid,
                    name = it.core.name,
                    accountIcon = it.account?.iconRsd,
                    accountName  = it.account?.getStringItemVO()
                )
            }
        }

}