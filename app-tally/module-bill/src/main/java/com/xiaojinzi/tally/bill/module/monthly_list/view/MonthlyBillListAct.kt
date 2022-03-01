package com.xiaojinzi.tally.bill.module.monthly_list.view

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
 *
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_BILL_MONTHLY
)
class MonthlyBillListAct: BaseTallyActivity<MonthlyBillListViewModel>() {

    @AttrValueAutowiredAnno("timestamp")
    var targetTimestamp: Long = System.currentTimeMillis()

    override fun getViewModelClass(): Class<MonthlyBillListViewModel> {
        return MonthlyBillListViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().dateTime.value = targetTimestamp
        }
        setContent {
            TallyTheme {
                StateBar {
                    MonthlyBillListViewWrap()
                }
            }
        }
    }

}