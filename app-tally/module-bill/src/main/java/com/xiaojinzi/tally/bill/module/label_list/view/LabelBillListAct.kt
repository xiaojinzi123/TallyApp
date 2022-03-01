package com.xiaojinzi.tally.bill.module.label_list.view

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

@RouterAnno(hostAndPath = TallyRouterConfig.TALLY_BILL_LABEL_LIST)
class LabelBillListAct: BaseTallyActivity<LabelBillListViewModel>() {

    @AttrValueAutowiredAnno("monthTimestamp")
    var targetMonthTimestamp: Long = System.currentTimeMillis()

    @AttrValueAutowiredAnno("labelId")
    var targetLabelId: String? = null

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().targetMonthTimeData.value = targetMonthTimestamp
            requiredViewModel().targetLabelIdData.value = targetLabelId!!
        }
        setContent {
            TallyTheme {
                StateBar {
                    CateGroupBillViewWrap()
                }
            }
        }
    }

}