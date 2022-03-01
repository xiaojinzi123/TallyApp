package com.xiaojinzi.tally.account.module.create.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.xiaojinzi.tally.account.R
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.theme.h7

@Composable
fun AccountCreateView() {
    val vm: AccountCreateViewModel = viewModel()
    val context = LocalContext.current
    val name by vm.nameObservableDTO.collectAsState(initial = "")
    val initialBalance by vm.initialBalanceObservableDTO.collectAsState(initial = 0f)
    val isNameFormatCorrect by vm.isNameFormatCorrectObservableDTO.collectAsState(initial = false)
    val isBalanceFormatCorrect by vm.isBalanceFormatCorrectObservableDTO.collectAsState(initial = false)

    @DrawableRes
    val iconRsd: Int? by vm.iconSelectUseCase.iconRsdNullableObservableDTO.collectAsState(initial = null)
    Column {

        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .fillMaxWidth()
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
                    nameName, nameValue, nameError, nameDivider,
                    balanceName, balanceValue, balanceError,
                ) = createRefs()
                val barrier = createEndBarrier(nameName, balanceName, margin = 8.dp)
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
                Divider(
                    modifier = Modifier
                        .constrainAs(nameDivider) {
                            if (isNameFormatCorrect) {
                                top.linkTo(anchor = nameValue.bottom, margin = 4.dp)
                            } else {
                                top.linkTo(anchor = nameError.bottom, margin = 4.dp)
                            }
                            start.linkTo(anchor = barrier, margin = 16.dp)
                        }
                        .nothing(),
                )
                Text(
                    modifier = Modifier
                        .constrainAs(balanceName) {
                            start.linkTo(parent.start)
                            centerVerticallyTo(balanceValue)
                        }
                        .wrapContentSize()
                        .padding(horizontal = 0.dp, vertical = 12.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_initial_balance),
                    style = MaterialTheme.typography.h7,
                    textAlign = TextAlign.End,
                )
                Text(
                    modifier = Modifier
                        .constrainAs(balanceValue) {
                            top.linkTo(anchor = nameDivider.bottom, margin = 4.dp)
                            start.linkTo(anchor = barrier, margin = 16.dp)
                            end.linkTo(anchor = parent.end)
                            width = Dimension.fillToConstraints
                        }
                        // .wrapContentSize()
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            vm.toEditBalance(context = context)
                        }
                        .padding(horizontal = 0.dp, vertical = 16.dp)
                        .nothing(),
                    text = initialBalance.tallyNumberFormat1(),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start,
                )
                if (!isBalanceFormatCorrect) {
                    Text(
                        modifier = Modifier
                            .constrainAs(balanceError) {
                                top.linkTo(balanceValue.bottom)
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

        Spacer(modifier = Modifier.height(height = 8.dp))

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
            iconRsd?.let {
                Image(
                    modifier = Modifier
                        .size(size = 24.dp)
                        .nothing(),
                    painter = painterResource(id = it),
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

    }
}

@ExperimentalComposeUiApi
@Composable
fun AccountCreateViewWrap() {
    val vm: AccountCreateViewModel = viewModel()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isUpdate by vm.isUpdateObservableDTO.collectAsState(initial = false)
    val canNext by vm.canNextObservableDTO.collectAsState(initial = false)
    Scaffold(
        topBar = {
            AppbarNormal(
                title = stringResource(
                    id = if (isUpdate) {
                        R.string.res_str_update_account
                    } else {
                        R.string.res_str_new_account
                    }
                ),
                titleAlign = TextAlign.Center,
                menu1IconRsd = if (canNext) R.drawable.res_right2 else null,
                menu1ClickListener = {
                    if (canNext) {
                        keyboardController?.hide()
                        vm.createOrUpdateAccount(context = context)
                    }
                }
            )
        }
    ) {
        AccountCreateView()
    }
}

@Preview
@Composable
private fun AccountCreateViewPreview() {
    AccountCreateView()
}