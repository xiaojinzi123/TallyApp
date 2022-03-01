package com.xiaojinzi.tally.home.module.main.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.R
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.theme.Yellow500
import com.xiaojinzi.module.base.theme.Yellow700
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.LogSupport
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.TabType
import com.xiaojinzi.tally.base.support.TallyLogKeyword
import com.xiaojinzi.tally.base.support.mainService
import com.xiaojinzi.tally.base.support.tallyAppService
import com.xiaojinzi.tally.base.theme.h7
import com.xiaojinzi.tally.home.module.main.data.tabList
import kotlinx.coroutines.InternalCoroutinesApi

private val TAG = "MainView"

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun MainView(pageIndex: Int, totalSize: Int) {
    val context = LocalContext.current
    val isCongratulation by mainService.isCongratulationObservableDTO.collectAsState(initial = false)
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
            ) {
                MainViewPagerView(pageIndex = pageIndex)
            }
            MainTabView(pageIndex = pageIndex, tabList = tabList)
        }
        // 是否祝贺一下
        if (isCongratulation) {
            Dialog(onDismissRequest = {
                // empty
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nothing(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.res_str_desc10),
                        style = MaterialTheme.typography.h4.copy(
                            color = Yellow500,
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.res_lottie_congratulation1)
                    )
                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.8f)
                            .aspectRatio(ratio = 1f),
                        iterations = LottieConstants.IterateForever,
                    )
                    Row(
                        modifier = Modifier
                            .nothing(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .clickable {
                                    mainService.flagCongratulation()
                                    Router
                                        .with(context)
                                        .hostAndPath(RouterConfig.SYSTEM_APP_MARKET)
                                        .putString("packageName", tallyAppService.appChannel.packageName)
                                        .forward()
                                }
                                .background(color = Yellow700)
                                .padding(horizontal = 28.dp, vertical = 10.dp)
                                .nothing(),
                            text = stringResource(id = R.string.res_str_give_a_high_praise),
                            style = MaterialTheme.typography.h7.copy(
                                color = Color.White,
                            ),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.width(width = 32.dp))
                        Text(
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .clickable {
                                    mainService.flagCongratulation()
                                }
                                .background(color = Yellow700)
                                .padding(horizontal = 28.dp, vertical = 10.dp)
                                .nothing(),
                            text = stringResource(id = R.string.res_str_i_know),
                            style = MaterialTheme.typography.h7.copy(
                                color = Color.White,
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@InternalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun MainViewWrap() {
    val selectTabType by mainService.tabObservable.collectAsState(initial = TabType.Home)
    LogSupport.d(
        tag = TAG,
        content = "selectTabType = ${selectTabType.tabIndex}", keywords = arrayOf(
            TallyLogKeyword.MAIN_VIEW, TallyLogKeyword.MAIN_TAB_INDEX
        )
    )
    val totalSize = tabList.size
    MainView(pageIndex = selectTabType.tabIndex, totalSize = totalSize)
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
@Preview(locale = "en")
@Preview(locale = "zh")
fun HomeViewPreview() {
    val totalSize = tabList.size
    Surface {
        MainView(
            pageIndex = 1,
            totalSize = totalSize
        )
    }
}