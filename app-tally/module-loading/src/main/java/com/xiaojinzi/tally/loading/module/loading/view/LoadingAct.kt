package com.xiaojinzi.tally.loading.module.loading.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.await
import com.xiaojinzi.component.forward
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.R
import com.xiaojinzi.module.base.support.appInfoService
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_LOADING_MAIN
)
class LoadingAct : BaseTallyActivity<LoadingViewModel>() {

    @AttrValueAutowiredAnno("routerUrl")
    var routerUrl: String? = null

    override fun getViewModelClass(): Class<LoadingViewModel> {
        return LoadingViewModel::class.java
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TallyTheme {
                StateBar {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colors.background)
                            .nothing(),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                                .fillMaxWidth()
                                .nothing(),
                            contentAlignment = Alignment.Center
                        ) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(R.raw.res_lottie_cat2)
                            )
                            LottieAnimation(
                                composition = composition,
                                modifier = Modifier
                                    .fillMaxWidth(fraction = 0.5f)
                                    .aspectRatio(ratio = 1f),
                                iterations = LottieConstants.IterateForever,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .nothing(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .nothing(),
                                text = stringResource(id = R.string.res_tally_app_name),
                                style = MaterialTheme.typography.h5,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(height = 6.dp))
                            Text(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .nothing(),
                                text = "v${appInfoService.appVersionName}",
                                style = MaterialTheme.typography.h6,
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(modifier = Modifier.height(height = 60.dp))
                    }

                }
            }
        }
        lifecycleScope.launch {
            // 给动画展示一点时间
            delay(1800)
            val b = try {
                routerUrl?.let { url ->
                    Router.with(mContext)
                        .url(url)
                        .await()
                    finish()
                    true
                }?: false
            } catch (e: Exception) {
                false
            }
            if (!b) {
                Router.with(mContext)
                    .hostAndPath(TallyRouterConfig.TALLY_HOME_MAIN)
                    .putAll(this@LoadingAct.intent.extras ?: Bundle())
                    .forward {
                        finish()
                    }
            }
        }
    }

}