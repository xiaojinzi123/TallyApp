package com.xiaojinzi.module.system.module.datetime.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.getYearByTimeStamp
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getActivity
import kotlinx.coroutines.flow.Flow

interface YearSelectUseCase : BaseUseCase {

    /**
     * 初始化的年份
     */
    val yearInitData: MutableInitOnceData<Int>

    /**
     * 选择的年份
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectYearObservableDTO: MutableSharedStateFlow<Int>

    /**
     * 可选择的年份
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val yearListObservableDTO: Flow<List<Int>>

    /**
     * 返回数据
     */
    fun returnData(context: Context)

}

class YearSelectUseCaseImpl : BaseUseCaseImpl(), YearSelectUseCase {

    override val yearInitData = MutableInitOnceData<Int>()

    override val selectYearObservableDTO = yearInitData.valueStateFlow
        .sharedStateIn(scope = scope)

    override val yearListObservableDTO = MutableSharedStateFlow(
        initValue = (2000..getYearByTimeStamp(timeStamp = System.currentTimeMillis())).toList()
    )

    override fun returnData(context: Context) {
        context.getActivity()?.let { act ->
            act.setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    this.putExtra("data", selectYearObservableDTO.value)
                }
            )
            act.finish()
        }
    }

}
