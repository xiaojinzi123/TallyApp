package com.xiaojinzi.tally.bill.module.bill_auto.domain

import android.content.Context
import android.content.Intent
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.support.autoBillService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@ViewModelLayer
interface BillAutoUseCase : BaseUseCase {

    /**
     * 是否开启了自动记账
     */
    val isOpenAutoBillFeatureObservableDTO: Flow<Boolean>

    /**
     * 检查是否开启了自动记账的功能
     */
    fun checkAutoBillFeature(context: Context)

    /**
     * 开启自动记账功能
     */
    fun openAutoBillFeature(context: Context)

}

@ViewModelLayer
class BillAutoUseCaseImpl : BaseUseCaseImpl(), BillAutoUseCase {

    override val isOpenAutoBillFeatureObservableDTO = MutableSharedStateFlow(initValue = false)

    override fun checkAutoBillFeature(context: Context) {
        isOpenAutoBillFeatureObservableDTO.value = autoBillService?.isOpenAutoBill?: false
    }

    override fun openAutoBillFeature(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            if (autoBillService?.canDrawOverlays == true) {
                Router.with(context)
                    .hostAndPath(RouterConfig.SYSTEM_ACTION_ACCESSIBILITY_SETTINGS)
                    .addIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .forward()
            } else {
                Router.with(context)
                    .hostAndPath(RouterConfig.SYSTEM_ACTION_MANAGE_OVERLAY_PERMISSION)
                    .addIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .forward()
            }
        }
    }

}