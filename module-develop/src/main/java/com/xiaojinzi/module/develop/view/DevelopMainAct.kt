package com.xiaojinzi.module.develop.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.anno.DevelopToolsVisible
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.theme.CommonTheme
import com.xiaojinzi.module.base.view.BaseActivity
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@DevelopToolsVisible(false)
@RouterAnno(hostAndPath = RouterConfig.COMMON_DEVELOP_MAIN)
class DevelopMainAct : BaseActivity<BaseViewModel>() {

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CommonTheme {
                StateBar {
                    DevelopViewWarp()
                }
            }
        }

        test1()

    }

    private fun test1() {
        lifecycleScope.launch(context = Dispatchers.IO) {
            println("123123")
        }
    }

}