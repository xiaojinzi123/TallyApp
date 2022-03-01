package com.xiaojinzi.tally.bill.module.budget.domain

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.module.base.support.commonRouteResultService
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.service.datasource.CostTypeDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBudgetDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBudgetInsertDTO
import com.xiaojinzi.tally.base.support.*
import com.xiaojinzi.tally.bill.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ViewModelLayer
interface BudgetUseCase : BaseUseCase {

    /**
     * 月份的时间
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val monthDateTimeObservableDTO: MutableSharedStateFlow<Long>

    /**
     * 当月预算的对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthBudgetObservableDTO: Flow<TallyBudgetDTO?>

    /**
     * 当月预算的值
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthBudgetValueObservableDTO: Flow<Long?>

    /**
     * 这个月之前的预算结余
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val beforeCurrentMonthBudgetBalanceValueObservableDTO: Flow<Long?>

    /**
     * 当月结余预算的值
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthBudgetBalanceValueObservableDTO: Flow<Long?>

    /**
     * 当月预算的值, 根据开关适配
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthBudgetBalanceValueAdapterObservableDTO: Flow<Long?>

    /**
     * 当月的支出
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val currentMonthSpendingObservableDTO: Flow<Long>

    /**
     * 是否设置了当月预算
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isSetMonthBudgetObservableDTO: Flow<Boolean>

    /**
     * 选择月份的时间
     */
    fun selectMonthTime(context: Context)

    /**
     * 去填写数字
     */
    fun toFillMonthBudget(context: Context)

    /**
     * 填写默认的预算
     */
    fun toFillDefaultMonthBudget(context: Context)

    /**
     * 删除月预算
     */
    fun deleteMonthBudget()

    /**
     * 删除默认预算
     */
    fun deleteDefaultBudget()

    /**
     * 设置是否预算累计
     */
    fun setCumulativeBudget(context: Context, value: Boolean)

}

@ViewModelLayer
class BudgetUseCaseImpl : BaseUseCaseImpl(), BudgetUseCase {

    override val monthDateTimeObservableDTO =
        MutableSharedStateFlow(initValue = System.currentTimeMillis())

    override val currentMonthBudgetObservableDTO = combine(
        monthDateTimeObservableDTO, tallyService.dataBaseChangedObservable
    ) { monthTime, _ ->
        tallyBudgetService.getByMonthTime(monthTime = monthTime)
    }
        .sharedStateIn(scope = scope)

    override val currentMonthBudgetValueObservableDTO = currentMonthBudgetObservableDTO
        .map {
            it?.value
        }

    override val beforeCurrentMonthBudgetBalanceValueObservableDTO = combine(
        monthDateTimeObservableDTO, tallyService.dataBaseChangedObservable
    ) { monthTime, _ ->
        tallyBudgetService.getBudgetBalanceBeforeSpecialTime(monthTime = monthTime)
    }

    override val currentMonthBudgetBalanceValueObservableDTO = monthDateTimeObservableDTO
        .flatMapLatest { monthTime ->
            tallyService.subscribeWithDataBaseChanged {
                tallyBudgetService.getMonthBudgetBalanceValue(monthTime = monthTime)
            }
        }

    override val currentMonthBudgetBalanceValueAdapterObservableDTO = combine(
        tallyBudgetService.isCumulativeBudgetObservableDTO,
        currentMonthBudgetBalanceValueObservableDTO,
        currentMonthBudgetValueObservableDTO
    ) { isCumulativeBudget, currentMonthBudgetBalanceValue, currentMonthBudgetValue ->
        if (isCumulativeBudget) {
            currentMonthBudgetBalanceValue
        } else {
            currentMonthBudgetValue
        }
    }

    override val currentMonthSpendingObservableDTO = combine(
        monthDateTimeObservableDTO, tallyService.dataBaseChangedObservable
    ) { timeStamp, _ ->
        val (startTime, endTime) = getMonthInterval(timeStamp = timeStamp)
        // 查询出来的是负数, 所以需要 * -1
        -1 * tallyBillService.getNormalBillCostAdjustByCondition(
            condition = BillQueryConditionDTO(
                costType = CostTypeDTO.Spending,
                startTime = startTime,
                endTime = endTime,
            )
        )
    }

    override val isSetMonthBudgetObservableDTO = currentMonthBudgetObservableDTO.map { it != null }

    override fun selectMonthTime(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            monthDateTimeObservableDTO.value = commonRouteResultService.selectDateTime(
                context = context,
                dateTime = monthDateTimeObservableDTO.value,
            )
        }
    }

    override fun toFillMonthBudget(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val value = commonRouteResultService.fillInNumber(
                context = context,
                value = currentMonthBudgetObservableDTO.value?.value?.tallyCostAdapter(),
            )
            tallyBudgetService.insertOrUpdate(
                target = TallyBudgetInsertDTO(
                    monthTime = monthDateTimeObservableDTO.value,
                    value = value.tallyCostToLong()
                )
            )
        }
    }

    override fun toFillDefaultMonthBudget(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val value = commonRouteResultService.fillInNumber(
                context = context,
                value = tallyBudgetService.monthlyDefaultBudgetObservableDTO.value?.tallyCostAdapter(),
            )
            tallyBudgetService.monthlyDefaultBudgetObservableDTO.value = value.tallyCostToLong()
        }
    }

    override fun deleteMonthBudget() {
        scope.launch(ErrorIgnoreContext) {
            currentMonthBudgetObservableDTO.value?.let { target ->
                tallyBudgetService.delete(target = target)
            }
        }
    }

    override fun deleteDefaultBudget() {
        scope.launch(ErrorIgnoreContext) {
            // 删除默认预算
            tallyBudgetService.monthlyDefaultBudgetObservableDTO.value = null
        }
    }

    override fun setCumulativeBudget(context: Context, value: Boolean) {
        scope.launch(ErrorIgnoreContext) {
            tallyBudgetService.isCumulativeBudgetObservableDTO.value = value
            if (value) {
                if (tallyBudgetService.getByMonthTime(monthTime = monthDateTimeObservableDTO.value) == null) {
                    val action = suspendCoroutine<Int> { cot ->
                        MaterialAlertDialogBuilder(context)
                            .setMessage(R.string.res_str_tip5)
                            .setOnCancelListener {
                                cot.resume(value = -1)
                            }
                            .setNegativeButton(R.string.res_str_cancel) { dialog, _ ->
                                cot.resume(value = 0)
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.res_str_confirm) { dialog, _ ->
                                cot.resume(value = 1)
                                dialog.dismiss()
                            }
                            .show()
                    }
                    if (action == 1) {
                        toFillMonthBudget(context = context)
                    }
                }
            }
        }
    }

}