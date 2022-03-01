package com.xiaojinzi.tally.bill.module.category.view

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
 * 类别的界面
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_CATEGORY
)
class BillCategoryAct: BaseTallyActivity<BillCategoryViewModel>() {

    @AttrValueAutowiredAnno("isReturnData")
    var isReturnData: Boolean = false

    override fun getViewModelClass(): Class<BillCategoryViewModel> {
        return BillCategoryViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().isReturnDataData.value = isReturnData
        }
        setContent {
            TallyTheme {
                StateBar {
                    TallyCategoryViewWrap()
                }
            }
        }

    }

}