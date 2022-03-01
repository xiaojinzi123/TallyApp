package com.xiaojinzi.tally.account.module.detail.domain

import android.content.Context
import androidx.annotation.UiContext
import androidx.paging.PagingData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.account.R
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyAccountDTO
import com.xiaojinzi.tally.base.support.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface AccountDetailUseCase : BaseUseCase {

    /**
     * 账户 Id
     */
    val accountIdData: MutableInitOnceData<String>

    /**
     * 账户的对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val accountObservableDTO: SharedStateFlow<TallyAccountDTO?>

    /**
     * 这个账户有关的所有账单
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val accountBillListObservableDTO: Flow<PagingData<BillDetailDayDTO>>

    /**
     * 去创建账单
     */
    fun toCreateBill(@UiContext context: Context, isTransfer: Boolean)

    /**
     * 删除账户
     */
    fun deleteAccount(@UiContext context: Context)

    /**
     * 设置为默认账户
     */
    fun setAsDefault()

}

class AccountDetailUseCaseImpl : BaseUseCaseImpl(), AccountDetailUseCase {

    override val accountIdData: MutableInitOnceData<String> = MutableInitOnceData()

    override val accountObservableDTO = combine(
        accountIdData.valueStateFlow,
        tallyService.dataBaseChangedObservable,
    ) { accountId, _ ->
        tallyAccountService.getByUid(uid = accountId)
    }
        .sharedStateIn(scope = scope)

    @ExperimentalCoroutinesApi
    override val accountBillListObservableDTO = combine(
        accountIdData.valueStateFlow,
        tallyService.dataBaseChangedObservable
    ) { accountId, _ ->
        tallyBillDetailQueryPagingService.subscribeCommonPageBillDetailObservable(
            billQueryCondition = BillQueryConditionDTO(
                accountAboutIdList = listOf(accountId)
            )
        )
    }.flatMapLatest { it }

    override fun toCreateBill(context: Context, isTransfer: Boolean) {
        Router.with(context)
            .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
            .putBoolean("isTransfer", isTransfer)
            .run {
                if (isTransfer) {
                    this.putString("outAccountId", accountIdData.value)
                } else {
                    this.putString("accountId", accountIdData.value)
                }
            }
            .forward()
    }

    override fun deleteAccount(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val currentAccountDTO = accountObservableDTO.value ?: return@launch
            if (currentAccountDTO.isDefault) {
                tallyAppToast(content = context.getString(R.string.res_str_the_default_account_can_not_be_deleted))
                return@launch
            }
            // 当前的账单 ID
            val currentAccountId = currentAccountDTO.uid
            val billCountAboutThisAccount =
                tallyBillService.getCountAboutAccountId(accountId = currentAccountId)
            if (billCountAboutThisAccount == 0) {
                // 删除这个账户
                tallyAccountService.deleteByUid(uid = currentAccountId)
                context.tryFinishActivity()
            } else {
                val action = suspendCoroutine<Int> { cot ->
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.res_str_tip4)
                        .setOnCancelListener {
                            cot.resume(value = -1)
                        }
                        .setNegativeButton(R.string.res_str_delete_account_and_bill) { dialog, _ ->
                            cot.resume(value = 0)
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.res_str_transfer_and_delete) { dialog, _ ->
                            cot.resume(value = 1)
                            dialog.dismiss()
                        }
                        .show()
                }
                if (action == -1) {
                    return@launch
                }
                if (action == 0) {
                    // 删除相关账单
                    tallyBillService.deleteAboutAccountId(accountId = currentAccountId)
                    // 删除这个账户
                    tallyAccountService.deleteByUid(uid = currentAccountId)
                } else if (action == 1) {
                    // 进行转移
                    val selectAccountId = Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_ACCOUNT_SELECT)
                        .putStringArray("disableIds", listOf(currentAccountId).toTypedArray())
                        .requestCodeRandom()
                        .intentAwait()
                        .getStringArrayExtra("data")!!
                        .first()
                    Assert.assertNotEquals(
                        value1 = selectAccountId,
                        value2 = currentAccountId,
                    )
                    tallyBillService.accountBillTransfer(
                        oldAccountId = currentAccountId,
                        newAccountId = selectAccountId
                    )
                    // 删除这个账户
                    tallyAccountService.deleteByUid(uid = currentAccountId)
                }
                if (action != -1) {
                    context.tryFinishActivity()
                }
            }
        }
    }

    override fun setAsDefault() {
        scope.launch(ErrorIgnoreContext) {
            val accountId = accountIdData.value
            tallyAccountService.setDefault(targetAccountId = accountId)
        }

    }

}
