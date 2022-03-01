package com.xiaojinzi.tally.bill.module.search.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

/**
 * 账单搜索界面
 */
@RouterAnno(hostAndPath = TallyRouterConfig.TALLY_BILL_SEARCH)
class BillSearchAct: BaseTallyActivity<BillSearchViewModel>() {

    override fun getViewModelClass(): Class<BillSearchViewModel> {
        return BillSearchViewModel::class.java
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
                    BillSearchViewWrap()
                }
            }
        }

    }

}