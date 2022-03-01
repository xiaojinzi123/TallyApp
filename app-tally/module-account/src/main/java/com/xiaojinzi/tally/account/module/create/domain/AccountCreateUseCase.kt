package com.xiaojinzi.tally.account.module.create.domain

import android.content.Context
import androidx.annotation.UiContext
import com.xiaojinzi.module.base.domain.IconSelectUseCase
import com.xiaojinzi.module.base.domain.IconSelectUseCaseImpl
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.commonRouteResultService
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.account.R
import com.xiaojinzi.tally.base.service.datasource.TallyAccountDTO
import com.xiaojinzi.tally.base.service.datasource.TallyAccountInsertDTO
import com.xiaojinzi.tally.base.support.tallyAccountService
import com.xiaojinzi.tally.base.support.tallyAccountTypeService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyCostToLong
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

interface AccountCreateUseCase : BaseUseCase {

    /**
     * 图标选择的逻辑
     */
    val iconSelectUseCase: IconSelectUseCase

    /**
     * 初始化的账户 ID
     */
    val initAccountIdData: MutableInitOnceData<String?>

    /**
     * 初始化的账户对象
     */
    val initAccountObservableDTO: Flow<TallyAccountDTO?>

    /**
     * 名称
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val nameObservableDTO: MutableSharedStateFlow<String>

    /**
     * 是否名称的格式正确了
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isNameFormatCorrectObservableDTO: Flow<Boolean>

    /**
     * 是否余额的格式正确了
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isBalanceFormatCorrectObservableDTO: Flow<Boolean>

    /**
     * 初始余额
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val initialBalanceObservableDTO: Flow<Float>

    /**
     * 是否是更新
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isUpdateObservableDTO: Flow<Boolean>

    /**
     * 是否可继续
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val canNextObservableDTO: Flow<Boolean>

    /**
     * 创建账户
     */
    fun createOrUpdateAccount(@UiContext context: Context)

    /**
     * 去编辑余额
     */
    fun toEditBalance(@UiContext context: Context)

}

class AccountCreateUseCaseImpl(
    override val iconSelectUseCase: IconSelectUseCase = IconSelectUseCaseImpl()
) : BaseUseCaseImpl(), AccountCreateUseCase {

    override val initAccountIdData = MutableInitOnceData<String?>()

    override val initAccountObservableDTO = initAccountIdData
        .valueStateFlow
        .map {
            it?.run {
                tallyAccountService.getByUid(uid = this)
            }
        }
        .onEach { targetAccount ->
            nameObservableDTO.value = targetAccount?.getStringItemVO()?.nameOfApp ?: ""
            initialBalanceObservableDTO.value = targetAccount?.initialBalance?.tallyCostAdapter()?: 0f
            iconSelectUseCase.iconRsdNullableObservableDTO.value =
                targetAccount?.iconRsd ?: R.drawable.res_money6
        }
        .sharedStateIn(scope = scope)

    override val nameObservableDTO = MutableSharedStateFlow(initValue = "")

    override val isNameFormatCorrectObservableDTO = nameObservableDTO
        .map {
            it.trim().isNotEmpty()
        }

    override val initialBalanceObservableDTO = MutableSharedStateFlow(initValue = 0f)

    override val isUpdateObservableDTO: Flow<Boolean> = initAccountObservableDTO
        .map { it != null }

    override val isBalanceFormatCorrectObservableDTO = initialBalanceObservableDTO
        .map { true }

    override val canNextObservableDTO = combine(
        isNameFormatCorrectObservableDTO,
        isBalanceFormatCorrectObservableDTO
    ) { b1, b2 ->
        b1 && b2
    }

    override fun createOrUpdateAccount(context: Context) {
        scope.launch {
            val iconRsd = iconSelectUseCase.iconRsdNullableObservableDTO.value ?: return@launch
            val initialBalance = initialBalanceObservableDTO.value.tallyCostToLong()
            val needUpdateAccount = initAccountObservableDTO.value
            if (needUpdateAccount == null) {
                tallyAccountService.insert(
                    target = TallyAccountInsertDTO(
                        typeId = tallyAccountTypeService.getAll().first().uid,
                        iconRsd = iconRsd,
                        name = nameObservableDTO.value,
                        initialBalance = initialBalance,
                    )
                )
            } else {
                tallyAccountService.update(
                    target = needUpdateAccount.copy(
                        iconRsd = iconRsd,
                        name = nameObservableDTO.value,
                        initialBalance = initialBalance,
                    )
                )
                // 发起一个异步的余额计算
                tallyAccountService.calculateBalanceAsync()
            }
            context.tryFinishActivity()
        }
    }

    override fun toEditBalance(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            initialBalanceObservableDTO.value = commonRouteResultService.fillInNumber(
                context = context,
                value = initialBalanceObservableDTO.value,
            )
        }
    }

}
