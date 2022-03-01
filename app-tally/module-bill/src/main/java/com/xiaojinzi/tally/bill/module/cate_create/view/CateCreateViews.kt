package com.xiaojinzi.tally.bill.module.cate_create.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
private fun CateCreateView() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vm: CateCreateViewModel = viewModel()
    val isUpdate by vm.isUpdateObservableDTO.collectAsState(initial = false)
    val name by vm.nameObservableDTO.collectAsState(initial = "")
    val isNameFormatCorrect by vm.isNameFormatCorrectObservableDTO.collectAsState(initial = false)

    @DrawableRes
    val iconResSelect: Int? by vm.iconSelectUseCase.iconRsdNonNullObservableDTO.collectAsState(
        initial = null
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .nothing(),
    ) {
        // 输入名称
        ConstraintLayout(
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .nothing(),
        ) {
            // 创建 id 引用
            val (
                nameName, nameValue, nameError,
            ) = createRefs()
            val barrier = createEndBarrier(nameName, margin = 10.dp)
            Text(
                modifier = Modifier
                    .constrainAs(nameName) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        centerVerticallyTo(nameValue)
                    }
                    .padding(horizontal = 0.dp, vertical = 12.dp)
                    .nothing(),
                text = stringResource(id = R.string.res_str_name),
                style = MaterialTheme.typography.h7,
                textAlign = TextAlign.End,
            )
            CommonEditText(
                modifier = Modifier
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
                            top.linkTo(nameValue.bottom)
                            start.linkTo(anchor = barrier, margin = 16.dp)
                        }
                        .nothing(),
                    text = stringResource(id = R.string.res_str_err_tip1),
                    style = MaterialTheme.typography.body2.copy(
                        color = Color.Red,
                        fontSize = 10.sp,
                    ),
                )
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
        Spacer(modifier = Modifier.height(height = 16.dp))
        // 选择 Icon
        Row(
            modifier = Modifier
                .clickable {
                    vm.iconSelectUseCase.toChooseIcon(context = context)
                }
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 0.dp, vertical = 12.dp)
                    .nothing(),
                text = stringResource(id = R.string.res_str_icon),
                style = MaterialTheme.typography.h7,
                textAlign = TextAlign.End,
            )
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
            if (iconResSelect == null) {
                Text(
                    text = stringResource(id = R.string.res_str_please_choose_the_Icon),
                    style = MaterialTheme.typography.body2.copy(
                        color = Color.Red
                    ),
                )
            } else {
                Image(
                    modifier = Modifier
                        .size(size = 24.dp)
                        .nothing(),
                    painter = painterResource(id = iconResSelect!!),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(width = 8.dp))
            Image(
                modifier = Modifier
                    .size(size = 20.dp)
                    .nothing(),
                painter = painterResource(id = R.drawable.res_arrow_right1),
                contentDescription = null
            )
        }
        if (isUpdate) {
            Spacer(modifier = Modifier.height(height = 12.dp))
            // 删除按钮
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .nothing(),
                onClick = { vm.deleteCate(context = context) },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White,
                )
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_delete),
                    style = MaterialTheme.typography.body2.copy(
                        color = Color.White,
                    ),
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun CateCreateViewWrap() {
    val context = LocalContext.current
    val vm: CateCreateViewModel = viewModel()
    val isUpdate by vm.isUpdateObservableDTO.collectAsState(initial = false)
    val canNext by vm.canNextObservableDTO.collectAsState(initial = false)
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = if (isUpdate) R.string.res_str_update_category else R.string.res_str_new_category,
                menu1IconRsd = if (canNext) R.drawable.res_right1 else null,
                menu1ClickListener = {
                    vm.newCate(context = context)
                },
            )
        }
    ) {
        CateCreateView()
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
private fun CateCreateViewPreview() {
    CateCreateView()
}