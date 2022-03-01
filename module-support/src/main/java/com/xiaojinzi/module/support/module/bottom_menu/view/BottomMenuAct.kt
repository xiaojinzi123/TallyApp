package com.xiaojinzi.module.support.module.bottom_menu.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.interceptor.AlphaInAnimInterceptor
import com.xiaojinzi.module.base.support.translateStatusBar
import com.xiaojinzi.module.base.theme.CommonTheme
import com.xiaojinzi.module.base.view.BaseActivity
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.module.support.R
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.initOnceUseViewModel

@RouterAnno(
    hostAndPath = RouterConfig.SYSTEM_MENU_SELECT,
    interceptors = [AlphaInAnimInterceptor::class],
)
@ViewLayer
class BottomMenuAct : BaseActivity<BottomMenuViewModel>() {

    @AttrValueAutowiredAnno("data")
    var dataList: ArrayList<StringItemDTO>? = null

    override fun getViewModelClass(): Class<BottomMenuViewModel> {
        return BottomMenuViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.translateStatusBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().dataListObservableDTO.value = dataList?: emptyList()
        }

        setContent {
            CommonTheme() {
                StateBar {
                    BottomMenuViewWrap()
                }
            }
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out)
    }

}