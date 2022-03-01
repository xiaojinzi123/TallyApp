package com.xiaojinzi.tally.bill.module.bill_auto_default_account.domain

import android.content.Context
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceAppDetailDTO
import com.xiaojinzi.tally.base.support.tallyAccountService
import com.xiaojinzi.tally.base.support.tallyBillAutoSourceAppService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ViewModelLayer
interface BillAutoDefaultAccountUseCase : BaseUseCase {

    /**
     * 显示的列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dataListObservableDTO: Flow<List<TallyBillAutoSourceAppDetailDTO>>

    /**
     * 选择账户
     */
    fun selectAccount(context: Context, uid: String)

}

@ViewModelLayer
class BillAutoDefaultAccountUseCaseImpl : BaseUseCaseImpl(), BillAutoDefaultAccountUseCase {

    override val dataListObservableDTO = tallyService
        .dataBaseChangedObservable
        .map { tallyBillAutoSourceAppService.getAllDetail() }
        .sharedStateIn(scope = scope)

    override fun selectAccount(context: Context, uid: String) {
        scope.launch(ErrorIgnoreContext) {
            val selectIdList = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_SELECT)
                .requestCodeRandom()
                .intentAwait()
                .getStringArrayExtra("data")
                ?.toList() ?: emptyList()
            // 选择好的账户
            val targetAccount =
                tallyAccountService.getByUid(uid = selectIdList.first()) ?: return@launch
            val targetItem =
                dataListObservableDTO.value.find { it.core.uid == uid } ?: return@launch
            tallyBillAutoSourceAppService.update(
                target = targetItem.core.copy(
                    accountId = targetAccount.uid
                )
            )
        }
    }

}