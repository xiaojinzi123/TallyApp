package com.xiaojinzi.tally.bill.module.category.view

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.theme.body3
import com.xiaojinzi.tally.bill.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

val tabSpace = 12.dp

@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun TallyCategoryViewWrap() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val vm: BillCategoryViewModel = viewModel()
    val selectTabType by vm.tabSelectIndexTypeObservableDTO.collectAsState(initial = BillCategoryTabType.Spending)

    val dataVOList by vm
        .dataObservableVO
        .collectAsState(initial = emptyList())

    val isChinaLang = Locale.getDefault() == Locale.CHINA
    val textStyle = if (isChinaLang) {
        MaterialTheme.typography.subtitle2
    } else {
        MaterialTheme.typography.body3
    }
    val textSelectStyle = if (isChinaLang) {
        MaterialTheme.typography.subtitle1
    } else {
        MaterialTheme.typography.body2
    }

    Column {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colors.primary)
                .statusBarsPadding()
                .fillMaxWidth()
                .nothing(),
        ) {
            Row(
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .nothing(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    context.tryFinishActivity()
                }) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.res_back1),
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    listOf(
                        R.string.res_str_spending,
                        R.string.res_str_income,
                    ).forEachIndexed { index, titleRsd ->

                        if (index > 0) {

                            Spacer(modifier = Modifier.width(width = tabSpace))
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .background(color = MaterialTheme.colors.background)
                                    .width(width = 1.dp)
                                    .fillMaxHeight(),
                            )
                            Spacer(modifier = Modifier.width(width = tabSpace))

                        }

                        Text(
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                                .clickable {
                                    vm.tabSelectIndexTypeObservableDTO.value =
                                        BillCategoryTabType.fromIndex(
                                            index = index
                                        )
                                }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                .nothing(),
                            text = stringResource(id = titleRsd),
                            style = if (index == selectTabType.index) {
                                textSelectStyle
                            } else {
                                textStyle
                            }.copy(color = Color.White),
                            textAlign = TextAlign.Center,
                        )

                    }

                }
                IconButton(
                    onClick = {
                        Router.with(context)
                            .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_GROUP_CREATE)
                            .putSerializable(
                                "cateGroupType",
                                selectTabType.toTallyCategoryGroupType()
                            )
                            .forward()
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .size(size = 24.dp)
                            .nothing(),
                        painter = painterResource(id = R.drawable.res_add4),
                        contentDescription = null
                    )
                }
            }
        }
        if (dataVOList.isNotEmpty()) {
            val pagerState = rememberPagerState(
                initialPage = vm.initTabTypeData.value.index
            )
            if (pagerState.currentPage != selectTabType.index) {
                scope.launch {
                    pagerState.animateScrollToPage(page = selectTabType.index)
                }
            }
            LaunchedEffect(key1 = pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .collect {
                        vm.tabSelectIndexTypeObservableDTO.value =
                            BillCategoryTabType.fromIndex(index = pagerState.currentPage)
                    }
            }
            Column {
                HorizontalPager(
                    count = dataVOList.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    state = pagerState
                ) { pageIndex ->
                    TallyCategoryListView(
                        categoryGroups = dataVOList[pageIndex].second,
                        cateGroupItemLongClick = { cateGroupId ->
                            Router.with(context)
                                .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_GROUP_CREATE)
                                .putString("cateGroupId", cateGroupId)
                                .forward()
                        },
                        cateItemClick = {
                            vm.onCateItemClick(
                                context = context,
                                categoryId = it.categoryId
                            )
                        },
                        cateAddClick = { cateGroupId ->
                            vm.onAddMoreCategory(
                                context = context,
                                cateGroupId = cateGroupId
                            )
                        }
                    )
                }
            }
        }
    }
}

