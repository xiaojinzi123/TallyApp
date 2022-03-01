package com.xiaojinzi.tally.bill.module.book_select.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.interceptor.AlphaInAnimInterceptor
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity
import com.xiaojinzi.tally.bill.R
import com.xiaojinzi.tally.bill.module.book_select.domain.SelectType

/**
 * 账本的列表, 功能：
 * 1. 单选一个账本
 * 2. 多选账本
 * 默认情况下是多选的.
 * 不提供删除和编辑功能
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_BILL_BOOK_SELECT,
    interceptors = [AlphaInAnimInterceptor::class],
)
class BillBookSelectAct : BaseTallyActivity<BillBookSelectViewModel>() {

    @AttrValueAutowiredAnno("isSingleSelect")
    var isSingleSelect: Boolean = false

    @AttrValueAutowiredAnno("selectIdList")
    var selectIdList: Array<String> = Array(0) { "" }

    override fun getViewModelClass(): Class<BillBookSelectViewModel> {
        return BillBookSelectViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            val targetSelectType = if (isSingleSelect) {
                SelectType.SingleSelect
            } else {
                SelectType.MultiSelect
            }
            requiredViewModel().selectTypeInitData.value = targetSelectType
            requiredViewModel().selectIdListInitData.value = selectIdList.toList()
        }
        setContent {
            TallyTheme {
                StateBar {
                    BillBookSelectViewWrap()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out)
    }

}