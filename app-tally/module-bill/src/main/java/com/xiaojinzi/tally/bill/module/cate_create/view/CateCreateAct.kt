package com.xiaojinzi.tally.bill.module.cate_create.view

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
import com.xiaojinzi.module.base.support.translateStatusBar
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_CATEGORY_CREATE
)
@ViewLayer
class CateCreateAct : BaseTallyActivity<CateCreateViewModel>() {

    @AttrValueAutowiredAnno("cateGroupId")
    var cateGroupId: String? = null

    @AttrValueAutowiredAnno("cateId")
    var cateId: String? = null

    override fun getViewModelClass(): Class<CateCreateViewModel> {
        return CateCreateViewModel::class.java
    }

    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.translateStatusBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initOnceUseViewModel {
            requiredViewModel().let { vm ->
                if (cateId == null) {
                    vm.cateGroupIdInitData.value = cateGroupId!!
                } else {
                    vm.cateGroupIdInitData.value = null
                }
                vm.cateIdInitData.value = cateId
            }
        }

        setContent {
            TallyTheme {
                StateBar {
                    CateCreateViewWrap()
                    // TestView()
                }
            }
        }

    }

}