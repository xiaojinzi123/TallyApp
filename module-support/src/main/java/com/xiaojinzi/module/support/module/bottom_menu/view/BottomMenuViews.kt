package com.xiaojinzi.module.support.module.bottom_menu.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.support.ktx.nothing

@ExperimentalMaterialApi
@Composable
private fun BottomMenuView() {
    val context = LocalContext.current
    val vm: BottomMenuViewModel = viewModel()
    val memuList by vm.dataListObservableDTO.collectAsState(initial = emptyList())
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
    LaunchedEffect(key1 = sheetState) {
        snapshotFlow { sheetState.isVisible }
            .collect {
                if (!it) { // 如果隐藏了
                    vm.returnData(
                        context = context,
                        index = null,
                    )
                }
            }
    }
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .nothing(),
            ) {
                memuList.forEachIndexed { index, item ->
                    if (index > 0) {
                        Divider()
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                vm.returnData(
                                    context = context,
                                    index = index,
                                )
                            }
                            .padding(horizontal = 0.dp, vertical = 18.dp)
                            .nothing(),
                        text = item.nameAdapter(),
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    ) {
        // empty
    }
}

@ExperimentalMaterialApi
@Composable
fun BottomMenuViewWrap() {
    BottomMenuView()
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun BottomMenuViewPreview() {
    BottomMenuView()
}