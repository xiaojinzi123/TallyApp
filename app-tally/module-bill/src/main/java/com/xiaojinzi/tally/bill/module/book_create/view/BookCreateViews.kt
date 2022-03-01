package com.xiaojinzi.tally.bill.module.book_create.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.base.view.compose.CommonEditText
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.theme.h7
import com.xiaojinzi.tally.bill.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
private fun BookCreateView() {
    val vm: BookCreateViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val name by vm.nameObservableDTO.collectAsState(initial = "")
    val isNameFormatCorrect by vm.isNameFormatCorrectObservableDTO.collectAsState(initial = false)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .nothing(),
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .nothing(),
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .nothing(),
            ) {
                // 创建 id 引用
                val (
                    nameName, nameValue, nameError,
                ) = createRefs()
                val barrier = createEndBarrier(nameName, margin = 8.dp)
                Text(
                    modifier = Modifier
                        .constrainAs(nameName) {
                            start.linkTo(parent.start)
                            centerVerticallyTo(nameValue)
                        }
                        .wrapContentSize()
                        .padding(horizontal = 0.dp, vertical = 12.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_name),
                    style = MaterialTheme.typography.h7,
                    textAlign = TextAlign.End,
                )
                CommonEditText(
                    Modifier
                        .focusRequester(
                            focusRequester = focusRequester
                        )
                        .constrainAs(nameValue) {
                            top.linkTo(parent.top)
                            start.linkTo(barrier)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .nothing(),
                    text = name,
                    placeholderTextRsd = R.string.res_str_please_input_name,
                    onDelete = {
                        vm.nameObservableDTO.value = ""
                    },
                ) {
                    vm.nameObservableDTO.value = it
                }
                if (!isNameFormatCorrect) {
                    Text(
                        modifier = Modifier
                            .constrainAs(nameError) {
                                top.linkTo(anchor = nameValue.bottom)
                                start.linkTo(anchor = barrier, margin = 16.dp)
                            }
                            .wrapContentSize()
                            .nothing(),
                        text = stringResource(id = R.string.res_str_err_tip1),
                        style = MaterialTheme.typography.body2.copy(
                            color = Color.Red,
                            fontSize = 10.sp,
                        ),
                    )
                }
            }
        }
    }
    DisposableEffect(key1 = Unit) {
        scope.launch {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        onDispose { }
    }
}

@ExperimentalComposeUiApi
@Composable
fun BookCreateViewWrap() {
    val vm: BookCreateViewModel = viewModel()
    val context = LocalContext.current
    val isUpdate by vm.isUpdateBookObservableDTO.collectAsState(initial = false)
    val canNext by vm.canNextObservableDTO.collectAsState(initial = false)
    Scaffold(
        topBar = {
            AppbarNormal(
                title = stringResource(id = if (isUpdate) R.string.res_str_edit_ledger else R.string.res_str_new_ledger),
                titleAlign = TextAlign.Center,
                menu1IconRsd = if (canNext) R.drawable.res_right2 else null,
                menu1ClickListener = {
                    vm.addOrUpdateBook(context = context)
                }
            )
        }
    ) {
        BookCreateView()
    }
}

@ExperimentalComposeUiApi
@Composable
private fun BookCreateViewPreview() {
    BookCreateView()
}