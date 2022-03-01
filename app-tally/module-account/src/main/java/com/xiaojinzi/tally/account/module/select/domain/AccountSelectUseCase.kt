package com.xiaojinzi.tally.account.module.select.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.UiContext
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getActivity
import kotlinx.coroutines.flow.Flow

interface AccountSelectUseCase : BaseUseCase {

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val userSelectIdObservable: MutableSharedStateFlow<String?>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val userDisableIdsObservable: MutableSharedStateFlow<List<String>>

    /**
     * 尝试返回数据
     */
    fun returnData(@UiContext context: Context)

}

class AccountSelectUseCaseImpl : BaseUseCaseImpl(), AccountSelectUseCase {

    override val userSelectIdObservable = MutableSharedStateFlow<String?>(initValue = null)

    override val userDisableIdsObservable =
        MutableSharedStateFlow<List<String>>(initValue = emptyList())

    override fun returnData(context: Context) {
        context.getActivity()?.let { act ->
            val targetSelectId = userSelectIdObservable.value
            if (targetSelectId == null) {
                act.setResult(Activity.RESULT_CANCELED)
            } else {
                act.setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        this.putExtra("data", arrayOf(targetSelectId))
                    }
                )
            }
            act.finish()
        }
    }

}
