package com.xiaojinzi.module.support.module.bottom_menu.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getActivity
import kotlinx.coroutines.flow.Flow

@ViewModelLayer
interface BottomMenuUseCase: BaseUseCase {

    /**
     * 显示的数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dataListObservableDTO: MutableSharedStateFlow<List<StringItemDTO>>

    /**
     * 返回数据
     */
    fun returnData(context: Context, index: Int?)

}

@ViewModelLayer
class BottomMenuUseCaseImpl: BaseUseCaseImpl(), BottomMenuUseCase {

    override val dataListObservableDTO = MutableSharedStateFlow<List<StringItemDTO>>(initValue = emptyList())

    override fun returnData(context: Context, index: Int?) {
        if (index != null) {
            context.getActivity()?.let { act ->
                act.setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        this.putExtra("data", index)
                    }
                )
            }
        }
        context.tryFinishActivity()
    }

}