package com.xiaojinzi.tally.bill.module.cate_group_bill_list.view

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
 * 类别组的账单列表数据
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_BILL_CATEGORY_GROUP
)
class CateGroupBillAct: BaseTallyActivity<CateGroupBillViewModel>() {

    @AttrValueAutowiredAnno("monthTimestamp")
    var targetMonthTimestamp: Long? = null

    @AttrValueAutowiredAnno("yearTimestamp")
    var targetYearTimestamp: Long? = null

    @AttrValueAutowiredAnno("cateGroupId")
    var cateGroupId: String? = null

    override fun getViewModelClass(): Class<CateGroupBillViewModel> {
        return CateGroupBillViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().targetMonthTimeData.value = targetMonthTimestamp
            requiredViewModel().targetYearTimeData.value = targetYearTimestamp
            requiredViewModel().targetCategoryGroupIdData.value = cateGroupId!!
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