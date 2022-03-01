package com.xiaojinzi.module.system.module.datetime.view

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
import com.xiaojinzi.module.base.interceptor.AlphaInAnimInterceptor
import com.xiaojinzi.module.base.support.getYearByTimeStamp
import com.xiaojinzi.module.base.theme.CommonTheme
import com.xiaojinzi.module.base.view.BaseActivity
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.module.system.R
import com.xiaojinzi.support.ktx.initOnceUseViewModel

/**
 * 可以选择的年份是 2000-now
 */
@RouterAnno(
    hostAndPath = RouterConfig.SYSTEM_YEAR_SELECT,
    interceptors = [AlphaInAnimInterceptor::class],
)
class YearSelectAct : BaseActivity<YearSelectViewModel>() {

    @AttrValueAutowiredAnno("initYear")
    var initYear: Int = getYearByTimeStamp(timeStamp = System.currentTimeMillis())

    override fun getViewModelClass(): Class<YearSelectViewModel> {
        return YearSelectViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().yearInitData.value = initYear
        }
        setContent {
            CommonTheme {
                StateBar {
                    YearSelectViewWrap()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out)
    }

}