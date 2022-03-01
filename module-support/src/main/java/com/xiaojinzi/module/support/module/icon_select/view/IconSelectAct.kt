package com.xiaojinzi.module.support.module.icon_select.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.theme.CommonTheme
import com.xiaojinzi.module.base.view.BaseActivity
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.initOnceUseViewModel

@RouterAnno(
    hostAndPath = RouterConfig.SUPPORT_ICON_SELECT
)
@ViewLayer
class IconSelectAct : BaseActivity<IconSelectViewModel>() {

    override fun getViewModelClass(): Class<IconSelectViewModel> {
        return IconSelectViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
        }
        setContent {
            CommonTheme {
                StateBar {
                    IconSelectViewWrap()
                }
            }
        }
    }

}