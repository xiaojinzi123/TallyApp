package com.xiaojinzi.tally.home.module.label.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.base.view.compose.EmptyView
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.home.R

@ExperimentalFoundationApi
@Composable
fun LabelListItemView(vo: LabelItemVO) {
    val context = LocalContext.current
    val vm: LabelListViewModel = viewModel()
    Surface(
        color = Color(vo.color),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(all = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .combinedClickable(
                    onLongClick = {
                        vm.toEdit(context = context, id = vo.labelId)
                    }
                ) {
                    vm.toggleLabelSelect(id = vo.labelId)
                }
                .padding(horizontal = 8.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(all = 6.dp)
                    .wrapContentSize(),
                text = vo.content.nameAdapter(),
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.width(width = 4.dp))
            Checkbox(
                checked = vo.isSelect,
                onCheckedChange = null,
                enabled = false
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun LabelListView(vos: List<LabelItemVO>) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        if (vos.isEmpty()) {
            EmptyView(
                emptyText = stringResource(id = R.string.res_str_list_of_label_is_empty)
            )
        } else
            FlowRow {
                vos.forEachIndexed { index, item ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.width(width = 0.dp))
                    }
                    LabelListItemView(vo = item)
                }
            }
    }
}

@ExperimentalFoundationApi
@Composable
fun LabelListViewWrap() {
    val vm: LabelListViewModel = viewModel()
    val context = LocalContext.current
    val vos by vm.labelListVO.collectAsState(initial = emptyList())
    val isReturnData by vm.isReturnDataInitData.valueStateFlow.collectAsState(initial = false)
    Scaffold(
        topBar = {
            if (isReturnData) {
                AppbarNormal(
                    titleRsd = R.string.res_str_label_list,
                    menu3IconRsd = R.drawable.res_delete3,
                    menu3ClickListener = {
                        vm.deleteSelectLabel()
                    },
                    menu2IconRsd = R.drawable.res_add4,
                    menu2ClickListener = {
                        Router.with(context)
                            .hostAndPath(TallyRouterConfig.TALLY_LABEL_CREATE)
                            .forward()
                    },
                    menu1IconRsd = R.drawable.res_right1,
                    menu1ClickListener = {
                        vm.completeSelect(context = context)
                    },
                )
            } else {
                AppbarNormal(
                    titleRsd = R.string.res_str_label_list,
                    menu2IconRsd = R.drawable.res_delete3,
                    menu2ClickListener = {
                        vm.deleteSelectLabel()
                    },
                    menu1IconRsd = R.drawable.res_add4,
                    menu1ClickListener = {
                        Router.with(context)
                            .hostAndPath(TallyRouterConfig.TALLY_LABEL_CREATE)
                            .forward()
                    },
                )
            }
        }
    ) {
        LabelListView(vos = vos)
    }
}

@ExperimentalFoundationApi
@Preview(locale = "zh")
@Composable
fun LabelListViewPreview() {
    LabelListView(
        vos = listOf(
            LabelItemVO(
                labelId = INVALID_STRING,
                content = "测试".toStringItemDTO(),
                color = Color.DarkGray.toArgb(),
            )
        )
    )
}