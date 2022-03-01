package com.xiaojinzi.tally.bill.module.bill_create.domain

import android.content.Context
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.component.withHostAndPath
import com.xiaojinzi.module.base.support.InitOnceData
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyAccountDTO
import com.xiaojinzi.tally.base.support.tallyAccountService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface BillCreateTransferUseCase : BaseUseCase {

    /**
     * 转出账户的 id 初始化
     */
    val outAccountIdInitData: InitOnceData<String?>

    /**
     * 转入账户的 id 初始化
     */
    val inAccountIdInitData: InitOnceData<String?>

    /**
     * 转出的账户
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectedOutAccountObservableDTO: MutableSharedStateFlow<TallyAccountDTO?>

    /**
     * 转入的账户
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectedInAccountObservableDTO: MutableSharedStateFlow<TallyAccountDTO?>

    /**
     * 初始化数据
     */
    fun initData(
        outAccountId: String? = null,
        inAccountId: String? = null,
    )

    /**
     * 去选择转出账户
     */
    fun toChooseOutAccount(context: Context)

    /**
     * 去选择转入账户
     */
    fun toChooseInAccount(context: Context)

    /**
     * 反转转账的账户
     */
    fun toggleTransferAccount()

}

class BillCreateTransferUseCaseImpl : BaseUseCaseImpl(), BillCreateTransferUseCase {

    override val outAccountIdInitData = MutableInitOnceData<String?>()
    override val inAccountIdInitData = MutableInitOnceData<String?>()

    override val selectedOutAccountObservableDTO = outAccountIdInitData
        .valueStateFlow
        .map {
            it?.run {
                tallyAccountService.getByUid(uid = this)
            }
        }
        .sharedStateIn(scope = scope)

    override val selectedInAccountObservableDTO = inAccountIdInitData
        .valueStateFlow
        .map {
            it?.run {
                tallyAccountService.getByUid(uid = this)
            }
        }
        .sharedStateIn(scope = scope)

    override fun initData(outAccountId: String?, inAccountId: String?) {
        outAccountIdInitData.value = outAccountId
        inAccountIdInitData.value = inAccountId
    }

    override fun toChooseOutAccount(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            selectedOutAccountObservableDTO.value = doSelectAccount(context = context)
        }
    }

    override fun toChooseInAccount(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            selectedInAccountObservableDTO.value = doSelectAccount(context = context)
        }
    }

    override fun toggleTransferAccount() {
        val outAccountValue = selectedOutAccountObservableDTO.value
        selectedOutAccountObservableDTO.value = selectedInAccountObservableDTO.value
        selectedInAccountObservableDTO.value = outAccountValue
    }

    private suspend fun doSelectAccount(context: Context): TallyAccountDTO? {
        val selectIdList: List<String> = context
            .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_ACCOUNT_SELECT)
            .requestCodeRandom()
            .intentAwait()
            .getStringArrayExtra("data")
            ?.toList() ?: emptyList()
        // 选择好的账户
        return tallyAccountService.getByUid(uid = selectIdList.first())
    }

}
