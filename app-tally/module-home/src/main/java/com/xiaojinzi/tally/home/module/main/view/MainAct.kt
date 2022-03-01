package com.xiaojinzi.tally.home.module.main.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.support.flow.toggle
import com.xiaojinzi.module.base.support.translateStatusBar
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.TabType
import com.xiaojinzi.tally.base.support.autoBillService
import com.xiaojinzi.tally.base.support.autoBillSettingService
import com.xiaojinzi.tally.base.support.mainService
import com.xiaojinzi.tally.base.support.tallyAppToast
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity
import com.xiaojinzi.tally.home.R
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlin.math.abs

@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_HOME_MAIN
)
class MainAct : BaseTallyActivity<MainViewModel>() {

    @AttrValueAutowiredAnno("routerUrl")
    var routerUrl: String? = null

    @InternalCoroutinesApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.translateStatusBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TallyTheme {
                StateBar {
                    MainViewWrap()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            if (autoBillSettingService.isTipToOpenAutoBillObservableDTO.first() && autoBillService?.isOpenAutoBill == false) {
                AlertDialog.Builder(mContext)
                    .setMessage(R.string.res_str_tip6)
                    .setNegativeButton(R.string.res_str_desc7) { _, _ ->
                        autoBillSettingService.isTipToOpenAutoBillObservableDTO.toggle()
                    }
                    .setPositiveButton(R.string.res_str_confirm) { _, _ ->
                        Router.with(mContext)
                            .hostAndPath(TallyRouterConfig.TALLY_BILL_AUTO)
                            .forward()
                    }
                    .show()
            }
        }

    }

    private var lastBackTimestamp: Long = 0
    override fun onBackPressed() {
        if (mainService.tabObservable.value != TabType.Home) {
            mainService.tabObservable.value = TabType.Home
            return
        }
        val currTimestamp = System.currentTimeMillis()
        if (abs(currTimestamp - lastBackTimestamp) > 1500) {
            tallyAppToast(context = this, content = mContext.getString(R.string.res_str_tip2))
            lastBackTimestamp = currTimestamp
            return
        }
        super.onBackPressed()
    }

}