package com.xiaojinzi.tally.bill.module.cycle_bill_create.domain

import android.app.Activity
import android.content.Context
import androidx.annotation.UiContext
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.component.intentResultCodeMatchAwait
import com.xiaojinzi.module.base.support.commonRouteResultService
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ViewModelLayer
interface CycleBillCreateUseCase : BaseUseCase {

    /**
     * 周期记账任务
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val cycleBillTaskObservableDTO: Flow<TallyBillDetailDTO?>

    /**
     * 起始时间
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val startTimeObservableDTO: MutableSharedStateFlow<Long?>

    /**
     * 去选择周期任务
     */
    fun toChooseBillTask(@UiContext context: Context)

    /**
     * 选择开始的时间
     */
    fun toChooseStartTime(@UiContext context: Context)

}

@ViewModelLayer
class CycleBillCreateUseCaseImpl : BaseUseCaseImpl(), CycleBillCreateUseCase {

    override val cycleBillTaskObservableDTO = MutableSharedStateFlow<TallyBillDetailDTO?>(initValue = null)

    override val startTimeObservableDTO = MutableSharedStateFlow<Long?>(initValue = null)

    override fun toChooseBillTask(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val billId = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_CYCLE_BILL_TASK)
                .requestCodeRandom()
                .intentResultCodeMatchAwait(expectedResultCode = Activity.RESULT_OK)
                .getStringExtra("billId")?: return@launch
            cycleBillTaskObservableDTO.value = tallyBillService.getDetailById(id = billId)
        }
    }

    override fun toChooseStartTime(context: Context) {

        scope.launch (ErrorIgnoreContext){
            startTimeObservableDTO.value = commonRouteResultService.selectDateTime(
                context = context,
                dateTime = startTimeObservableDTO.value?: System.currentTimeMillis(),
            )
        }

    }

}