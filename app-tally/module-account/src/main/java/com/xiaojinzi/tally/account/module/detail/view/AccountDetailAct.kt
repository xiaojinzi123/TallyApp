package com.xiaojinzi.tally.account.module.detail.view

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

@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_ACCOUNT_DETAIL,
)
class AccountDetailAct: BaseTallyActivity<AccountDetailViewModel>() {

    @AttrValueAutowiredAnno("accountId")
    var accountId: String? = null

    override fun getViewModelClass(): Class<AccountDetailViewModel> {
        return AccountDetailViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().accountIdData.value = accountId!!
        }
        setContent {
            TallyTheme {
                StateBar {
                    AccountDetailViewWrap()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (requiredViewModel().isShowMenuVO.value) {
            requiredViewModel().isShowMenuVO.value = false
        } else {
            super.onBackPressed()
        }
    }

}