package com.xiaojinzi.tally.bill.module.cate_group_create.view

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
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity

/**
 * 类别组的创建界面
 */
@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_CATEGORY_GROUP_CREATE,
    /*interceptors = [
        CateGroupCreateInterceptor::class
    ]*/
)
class CateGroupCreateAct: BaseTallyActivity<CateGroupCreateViewModel>() {

    @AttrValueAutowiredAnno("cateGroupType")
    var cateGroupType: TallyCategoryGroupTypeDTO = TallyCategoryGroupTypeDTO.Spending

    @AttrValueAutowiredAnno("cateGroupId")
    var cateGroupId: String? = null

    override fun getViewModelClass(): Class<CateGroupCreateViewModel> {
        return CateGroupCreateViewModel::class.java
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
            if (cateGroupId == null) {
                requiredViewModel().cateGroupTypeInitDate.value = cateGroupType
                requiredViewModel().cateGroupIdInitData.value = null
            } else {
                requiredViewModel().cateGroupIdInitData.value = cateGroupId
            }
        }
        setContent {
            TallyTheme {
                StateBar {
                    CateGroupCreateViewWrap()
                }
            }
        }

    }

}