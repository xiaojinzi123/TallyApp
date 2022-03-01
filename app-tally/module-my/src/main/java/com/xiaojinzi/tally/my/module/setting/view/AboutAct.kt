package com.xiaojinzi.tally.my.module.setting.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.view.WindowCompat
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.support.translateStatusBar
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

/**
 * 关于界面
 */
@RouterAnno(hostAndPath = TallyRouterConfig.TALLY_ABOUT)
class AboutAct: BaseTallyActivity<SettingViewModel>() {

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.translateStatusBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TallyTheme {
                StateBar {
                    AboutViewWrap()
                }
            }
        }
    }

}