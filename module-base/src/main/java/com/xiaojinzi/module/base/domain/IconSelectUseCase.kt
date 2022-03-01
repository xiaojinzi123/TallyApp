package com.xiaojinzi.module.base.domain

import android.content.Context
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.module.base.support.ResData
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

interface IconSelectUseCase : BaseUseCase {

    /**
     * 初始化的资源
     */
    val initIconData: MutableInitOnceData<Int?>

    /**
     * 选中的图标, 是一个图标的资源 ID
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val iconRsdNullableObservableDTO: MutableSharedStateFlow<Int?>

    /**
     * 选中的图标, 是一个图标的资源 ID
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val iconRsdNonNullObservableDTO: SharedStateFlow<Int>

    /**
     * 去选择 Icon
     */
    fun toChooseIcon(context: Context)

}

class IconSelectUseCaseImpl : BaseUseCaseImpl(), IconSelectUseCase {

    override val initIconData = MutableInitOnceData<Int?>()

    override val iconRsdNullableObservableDTO = initIconData
        .valueStateFlow
        .sharedStateIn(scope = scope)

    override val iconRsdNonNullObservableDTO = iconRsdNullableObservableDTO
        .filterNotNull()
        .sharedStateIn(scope = scope)

    override fun toChooseIcon(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val rsdIndex = Router.with(context)
                .hostAndPath(RouterConfig.SUPPORT_ICON_SELECT)
                .requestCodeRandom()
                .intentAwait()
                .getIntExtra("rsdIndex", -1)
            // 如果在合理的区间
            if (rsdIndex in ResData.resIconIndexList.indices) {
                iconRsdNullableObservableDTO.value = ResData.resIconIndexList[rsdIndex]
            }
        }
    }

}
