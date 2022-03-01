package com.xiaojinzi.tally.home.module.label.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

/**
 * 标签创建界面
 */
@RouterAnno(hostAndPath = TallyRouterConfig.TALLY_LABEL_CREATE)
class LabelCreateAct : BaseTallyActivity<LabelCreateViewModel>() {

    @AttrValueAutowiredAnno("labelId")
    var labelId: String? = null

    override fun getViewModelClass(): Class<LabelCreateViewModel> {
        return LabelCreateViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().labelIdInitData.value = labelId
        }
        setContent {
            TallyTheme {
                StateBar {
                    LabelCreateViewWrap()
                }
            }
        }

    }

}