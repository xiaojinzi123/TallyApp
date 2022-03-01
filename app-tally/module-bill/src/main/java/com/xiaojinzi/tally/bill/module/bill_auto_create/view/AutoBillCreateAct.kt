package com.xiaojinzi.tally.bill.module.bill_auto_create.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.interceptor.AlphaInAnimInterceptor
import com.xiaojinzi.module.base.support.translateStatusBar
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.support.tallyBillAutoSourceAppService
import com.xiaojinzi.tally.base.support.tallyBillAutoSourceViewService
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity
import com.xiaojinzi.tally.bill.module.bill_create.domain.CostType
import kotlinx.coroutines.launch

@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_BILL_AUTO_CREATE,
    interceptors = [AlphaInAnimInterceptor::class],
)
@ViewLayer
class AutoBillCreateAct : BaseTallyActivity<AutoBillCreateViewModel>() {

    @AttrValueAutowiredAnno("data")
    var billInfoData: BillParseResultDTO? = null

    override fun getViewModelClass(): Class<AutoBillCreateViewModel> {
        return AutoBillCreateViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.translateStatusBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        initOnceUseViewModel {
            lifecycleScope.launch {
                billInfoData?.let { info ->
                    val vm = requiredViewModel()
                    vm.initData(
                        costType = CostType.Absolute,
                        time = info.time,
                        cost = info.cost,
                        isMustSelectAccount = true,
                        accountId = tallyBillAutoSourceAppService
                            .getDetailByType(sourceType = info.sourceType.sourceAppType)
                            ?.account
                            ?.uid,
                        note = info.note,
                    )
                    vm.categorySelectObservableDTO.value = tallyBillAutoSourceViewService
                        .getDetailByType(type = info.sourceType)
                        ?.category
                }
            }
        }

        setContent {
            TallyTheme {
                StateBar {
                    AutoBillCreateViewWrap()
                }
            }
        }

    }

}