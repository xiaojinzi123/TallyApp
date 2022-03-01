package com.xiaojinzi.tally.account.module.select.view

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
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.account.R
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

/**
 * - 支持单选
 * - 支持多选(暂时不做, 因为没需求)
 * - 支持禁用
 * Note: 禁用的和默认要选中是不可以一样的
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_ACCOUNT_SELECT,
    interceptors = [AlphaInAnimInterceptor::class]
)
class AccountSelectAct : BaseTallyActivity<AccountSelectViewModel>() {

    @AttrValueAutowiredAnno("selectId")
    var selectId: String? = null

    @AttrValueAutowiredAnno("disableIds")
    var disableIds: Array<String> = Array(size = 0) {""}

    override fun getViewModelClass(): Class<AccountSelectViewModel> {
        return AccountSelectViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            // 选中不可以在禁用列表里面
            Assert.assertNull(value = disableIds.find { it == selectId })
            requiredViewModel().userSelectIdObservable.value = selectId
            requiredViewModel().userDisableIdsObservable.value = disableIds.toList()
        }
        setContent {
            TallyTheme {
                StateBar {
                    AccountSelectViewWrap()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out)
    }

}