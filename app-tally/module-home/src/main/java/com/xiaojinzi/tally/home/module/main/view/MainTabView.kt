package com.xiaojinzi.tally.home.module.main.view

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.TabType
import com.xiaojinzi.tally.base.support.mainService
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.home.R
import com.xiaojinzi.tally.home.module.main.data.TabVO
import com.xiaojinzi.tally.home.module.main.data.tabList

@ExperimentalPagerApi
@Composable
fun MainTabView(pageIndex: Int = 0, tabList: List<TabVO>) {
    val context = LocalContext.current
    Surface(
        color = MaterialTheme.colors.background,
    ) {
        Surface(
            modifier = Modifier.padding(top = 20.dp),
            color = MaterialTheme.colors.background,
        ) {
            TabRow(
                selectedTabIndex = 0,
                indicator = {},
                backgroundColor = MaterialTheme.colors.surface,
            ) {
                tabList.forEachIndexed { index, tabVO ->
                    val isSelected = index == pageIndex
                    Tab(
                        selected = isSelected,
                        onClick = {
                            mainService.tabObservable.value = TabType.fromIndex(
                                index = index
                            )
                        },
                    ) {
                        Row {
                            if (index == 2) {
                                Spacer(modifier = Modifier.width(width = 30.dp))
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .padding(top = 5.dp, bottom = 5.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = if (isSelected) tabVO.iconSelectedRsd else tabVO.iconRsd),
                                    contentDescription = stringResource(id = tabVO.nameRsd),
                                    modifier = Modifier
                                        .size(size = 20.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = stringResource(id = tabVO.nameRsd),
                                    style = MaterialTheme.typography.body1
                                )
                            }
                            if (index == 1) {
                                Spacer(modifier = Modifier.width(width = 30.dp))
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                        .forward()
                }
            ) {
                Image(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(id = R.drawable.res_add3),
                    contentDescription = null
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun MainTabViewPreview() {
    TallyTheme {
        MainTabView(pageIndex = 0, tabList = tabList)
    }
}