package com.xiaojinzi.tally.home.module.note.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.interceptor.AlphaInAnimInterceptor
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity
import com.xiaojinzi.tally.home.R

/**
 * 备注的界面
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_NOTE,
    interceptors = [AlphaInAnimInterceptor::class],
)
@ViewLayer
class NoteAct: BaseTallyActivity<NoteViewModel>() {

    @AttrValueAutowiredAnno("data")
    var data: String = ""

    override fun getViewModelClass(): Class<NoteViewModel> {
        return NoteViewModel::class.java
    }

    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TallyTheme {
                StateBar {
                    NoteViewWrap()
                }
            }
        }

        requiredViewModel().noteObservableDTO.value = data

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out)
    }

}