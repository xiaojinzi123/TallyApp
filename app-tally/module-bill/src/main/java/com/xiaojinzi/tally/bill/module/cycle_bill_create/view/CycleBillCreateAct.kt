package com.xiaojinzi.tally.bill.module.cycle_bill_create.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.translateStatusBar
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_CYCLE_BILL_CREATE
)
@ViewLayer
class CycleBillCreateAct : BaseTallyActivity<CycleBillCreateViewModel>() {

    override fun getViewModelClass(): Class<CycleBillCreateViewModel> {
        return CycleBillCreateViewModel::class.java
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
            // TODO
        }

        setContent {
            TallyTheme {
                StateBar {
                    CycleBillCreateViewWrap()
                }
            }
        }

    }

}