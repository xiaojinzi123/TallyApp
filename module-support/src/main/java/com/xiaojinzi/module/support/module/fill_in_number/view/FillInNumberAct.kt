package com.xiaojinzi.module.support.module.fill_in_number.view

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
import com.xiaojinzi.module.base.support.translateStatusBar
import com.xiaojinzi.module.base.theme.CommonTheme
import com.xiaojinzi.module.base.view.BaseActivity
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.initOnceUseViewModel

@RouterAnno(
    hostAndPath = RouterConfig.SYSTEM_FILL_IN_NUMBER
)
@ViewLayer
class FillInNumberAct : BaseActivity<FillInNumberViewModel>() {

    @AttrValueAutowiredAnno("data")
    var data: Float? = null

    override fun getViewModelClass(): Class<FillInNumberViewModel> {
        return FillInNumberViewModel::class.java
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
            data?.let {
                requiredViewModel().costUseCase.costAppend(
                    target = it.toString(),
                    isRemoveDefaultInput = true,
                )
            }
        }

        setContent {
            CommonTheme {
                StateBar {
                    FillInNumberViewWrap()
                }
            }
        }

    }

}