package com.xiaojinzi.tally.account.module.create.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

/**
 * 账户创建的界面
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_ACCOUNT_CREATE
)
@ViewLayer
class AccountCreateAct: BaseTallyActivity<AccountCreateViewModel>() {

    @AttrValueAutowiredAnno("accountId")
    var accountId: String? = null

    override fun getViewModelClass(): Class<AccountCreateViewModel> {
        return AccountCreateViewModel::class.java
    }

    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().initAccountIdData.value = accountId
        }
        setContent {
            TallyTheme {
                StateBar {
                    AccountCreateViewWrap()
                }
            }
        }
    }

}