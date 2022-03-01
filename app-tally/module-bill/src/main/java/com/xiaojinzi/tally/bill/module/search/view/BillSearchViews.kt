package com.xiaojinzi.tally.bill.module.search.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.statusBarsPadding
import com.xiaojinzi.module.base.support.flow.toggle
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.module.base.theme.Yellow900
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.view.BillPageListView
import com.xiaojinzi.tally.bill.R
import com.xiaojinzi.tally.bill.module.search.domain.HavePictureType

private val BoaderWidth = 1.dp
private val BoaderSelectColor = Yellow900
private val ConditionCompleteColor = Yellow900
private val ConditionItemHorizontalSpace = 10.dp
private val ConditionItemGroupVerticalSpace = 10.dp
private val ConditionItemVerticalSpace = 10.dp

@ExperimentalComposeUiApi
@Composable
private fun BillSearchConditionTextItem(
    text: String,
    isSelect: Boolean,
    onClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .wrapContentSize()
            .nothing(),
    ) {
        val (content, isSelectFlag) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(ref = content) {
                    this.top.linkTo(parent.top)
                    this.bottom.linkTo(parent.bottom)
                    this.start.linkTo(parent.start)
                    this.end.linkTo(parent.end)
                }
                .clip(shape = MaterialTheme.shapes.small)
                .clickable {
                    onClick.invoke()
                }
                .background(color = MaterialTheme.colors.surface)
                .run {
                    if (isSelect) {
                        this.border(
                            width = BoaderWidth,
                            color = BoaderSelectColor,
                            shape = MaterialTheme.shapes.small
                        )
                    } else {
                        this
                    }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .nothing(),
            text = text,
            style = MaterialTheme.typography.body1,
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun BillSearchSearchbar() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vm: BillSearchViewModel = viewModel()
    val searchContent by vm.searchContentObservableDTO.collectAsState(initial = "")
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .nothing(),
    ) {
        Row(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = CircleShape,
                )
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .clickable {
                        if (vm.isShowConditionObservableDTO.value) {
                            vm.isShowConditionObservableDTO.toggle()
                        } else {
                            context.tryFinishActivity()
                        }
                    }
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .nothing(),
            ) {
                Icon(
                    modifier = Modifier
                        .size(size = 24.dp)
                        .nothing(),
                    painter = painterResource(id = R.drawable.res_back1),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                )
            }
            Box(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    .nothing(),
                contentAlignment = Alignment.Center,
            ) {
                BasicTextField(
                    value = TextFieldValue(
                        text = searchContent,
                        selection = TextRange(index = searchContent.length),
                    ),
                    onValueChange = {
                        vm.searchContentObservableDTO.value = it.text.trim()
                    },
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    modifier = Modifier
                        .focusRequester(
                            focusRequester = focusRequester
                        )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .nothing(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            vm.doSearch()
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    ),
                )
                if (searchContent.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier
                                .clickable {
                                    context.tryFinishActivity()
                                }
                                .size(12.dp),
                            painter = painterResource(id = R.drawable.res_search1),
                            contentDescription = null,
                            // tint = MaterialTheme.colors.primary,
                        )
                        Spacer(modifier = Modifier.width(width = 4.dp))
                        Text(
                            text = stringResource(id = R.string.res_str_please_input_content),
                            style = MaterialTheme.typography.subtitle2,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .clickable {
                        if (!vm.isShowConditionObservableDTO.value) {
                            focusManager.clearFocus()
                        }
                        vm.isShowConditionObservableDTO.toggle()
                    }
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .nothing(),
            ) {
                Image(
                    modifier = Modifier
                        .size(size = 24.dp)
                        .nothing(),
                    painter = painterResource(id = R.drawable.res_filter1),
                    contentDescription = null
                )
            }
        }
        // 显示键盘
        /*DisposableEffect(key1 = Unit) {
            scope.launch {
                delay(200)
                focusRequester.requestFocus()
                keyboardController?.show()
            }
            onDispose { }
        }*/
        Spacer(modifier = Modifier.width(width = 6.dp))
        Text(
            text = stringResource(id = R.string.res_str_search),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .clickable {
                    vm.doSearch()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
                .padding(horizontal = 6.dp, vertical = 8.dp)
                .nothing(),
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun BillSearchViewWrap() {
    val vm: BillSearchViewModel = viewModel()
    val isShowCondition by vm.isShowConditionObservableDTO.collectAsState(initial = false)
    val labelListVO by vm.labelListObservableVO.collectAsState(initial = emptyList())
    val bookListVO by vm.bookListObservableVO.collectAsState(initial = emptyList())
    val accountListVO by vm.accountListObservableVO.collectAsState(initial = emptyList())
    val categoryGroupSpendingListVO by vm.categoryGroupSpendingListObservableVO.collectAsState(
        initial = emptyList()
    )
    val categoryGroupIncomeListVO by vm.categoryGroupIncomeListObservableVO.collectAsState(initial = emptyList())
    val billGroupVOList = vm.currBillPageListObservableVO.collectAsLazyPagingItems()
    val searchResultBillListCount by vm.searchResultBillListCountObservableDTO.collectAsState(initial = 0)
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nothing(),
        ) {
            BillSearchSearchbar()
            Box(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .nothing(),
            ) {
                Column {
                    if (billGroupVOList.itemCount != 0) {
                        Text(
                            text = "总共找到 $searchResultBillListCount 条数据",
                            style = MaterialTheme.typography.subtitle1.copy(
                                color = Color.Gray
                            ),
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .nothing(),
                        )
                    }
                    BillPageListView(
                        modifier = Modifier
                            .fillMaxSize()
                            .nothing(),
                        vos = billGroupVOList
                    ) {
                        // empty
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .nothing(),
                ) {
                    AnimatedVisibility(visible = isShowCondition) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .combinedClickable(enabled = false) {
                                    // empty
                                }
                                .nothing(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(weight = 8f, fill = true)
                                    .background(color = MaterialTheme.colors.background)
                                    .padding(horizontal = 0.dp, vertical = 0.dp)
                                    .nothing(),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(weight = 1f, fill = true)
                                        .verticalScroll(state = rememberScrollState())
                                        .padding(horizontal = 16.dp, vertical = 0.dp)
                                        .padding(bottom = 20.dp)
                                        .nothing(),
                                ) {
                                    if (labelListVO.isNotEmpty()) {
                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = 0.dp, vertical = 8.dp)
                                                .nothing(),
                                            text = stringResource(id = R.string.res_str_label),
                                            style = MaterialTheme.typography.subtitle1,
                                        )
                                        FlowRow(
                                            mainAxisSpacing = ConditionItemHorizontalSpace,
                                            crossAxisSpacing = ConditionItemVerticalSpace,
                                        ) {
                                            labelListVO.forEach { item ->
                                                BillSearchConditionTextItem(
                                                    text = item.core.name.nameAdapter(),
                                                    isSelect = item.isSelected
                                                ) {
                                                    vm.toggleLabelIdSelect(id = item.core.uid)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(height = ConditionItemGroupVerticalSpace))
                                    }
                                    if (bookListVO.isNotEmpty()) {
                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = 0.dp, vertical = 8.dp)
                                                .nothing(),
                                            text = stringResource(id = R.string.res_str_ledger),
                                            style = MaterialTheme.typography.subtitle1,
                                        )
                                        FlowRow(
                                            mainAxisSpacing = ConditionItemHorizontalSpace,
                                            crossAxisSpacing = ConditionItemVerticalSpace,
                                        ) {
                                            bookListVO.forEach { item ->
                                                BillSearchConditionTextItem(
                                                    text = item.core.nameItem.nameAdapter(),
                                                    isSelect = item.isSelected
                                                ) {
                                                    vm.toggleBookIdSelect(id = item.core.uid)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(height = ConditionItemGroupVerticalSpace))
                                    }
                                    if (accountListVO.isNotEmpty()) {
                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = 0.dp, vertical = 8.dp)
                                                .nothing(),
                                            text = stringResource(id = R.string.res_str_account),
                                            style = MaterialTheme.typography.subtitle1,
                                        )
                                        FlowRow(
                                            mainAxisSpacing = ConditionItemHorizontalSpace,
                                            crossAxisSpacing = ConditionItemVerticalSpace,
                                        ) {
                                            accountListVO.forEach { item ->
                                                BillSearchConditionTextItem(
                                                    text = item.core.getStringItemVO()
                                                        .nameAdapter(),
                                                    isSelect = item.isSelected
                                                ) {
                                                    vm.toggleAccountIdSelect(id = item.core.uid)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(height = ConditionItemGroupVerticalSpace))
                                    }
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 0.dp, vertical = 8.dp)
                                            .nothing(),
                                        text = stringResource(id = R.string.res_str_category),
                                        style = MaterialTheme.typography.subtitle1,
                                    )
                                    if (categoryGroupSpendingListVO.isNotEmpty() && categoryGroupIncomeListVO.isNotEmpty()) {
                                        FlowRow(
                                            mainAxisSpacing = ConditionItemHorizontalSpace,
                                            crossAxisSpacing = ConditionItemVerticalSpace,
                                        ) {
                                            categoryGroupSpendingListVO.forEach { item ->
                                                BillSearchConditionTextItem(
                                                    text = item.core.getStringItemVO()
                                                        .nameAdapter(),
                                                    isSelect = item.isSelected
                                                ) {
                                                    vm.toggleCategoryGroupIdSelect(id = item.core.uid)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(height = ConditionItemVerticalSpace))
                                        FlowRow(
                                            mainAxisSpacing = ConditionItemHorizontalSpace,
                                            crossAxisSpacing = ConditionItemVerticalSpace,
                                        ) {
                                            categoryGroupIncomeListVO.forEach { item ->
                                                BillSearchConditionTextItem(
                                                    text = item.core.getStringItemVO()
                                                        .nameAdapter(),
                                                    isSelect = item.isSelected
                                                ) {
                                                    vm.toggleCategoryGroupIdSelect(id = item.core.uid)
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(height = 16.dp))
                                Divider()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .nothing(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(weight = 1f, fill = true)
                                            .wrapContentHeight()
                                            .clickable { vm.resetCondition() }
                                            .padding(horizontal = 0.dp, vertical = 18.dp)
                                            .nothing(),
                                        text = stringResource(id = R.string.res_str_reset),
                                        style = MaterialTheme.typography.body1,
                                        textAlign = TextAlign.Center,
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(width = 1.dp)
                                            .height(height = 20.dp)
                                            .background(
                                                color = MaterialTheme.colors.onSurface.copy(
                                                    alpha = 0.12f
                                                )
                                            )
                                            .nothing()
                                    )
                                    Text(
                                        modifier = Modifier
                                            .weight(weight = 1f, fill = true)
                                            .wrapContentHeight()
                                            .clickable { vm.conditionComplete() }
                                            .padding(horizontal = 0.dp, vertical = 18.dp)
                                            .nothing(),
                                        text = stringResource(id = R.string.res_str_complete),
                                        style = MaterialTheme.typography.body1.copy(
                                            color = ConditionCompleteColor,
                                        ),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(weight = 2f, fill = true)
                                    .clickable {
                                        vm.isShowConditionObservableDTO.toggle()
                                    }
                                    .background(
                                        color = Color.Black.copy(
                                            alpha = 0.6f
                                        )
                                    )
                                    .nothing()
                            )
                        }
                    }
                }
            }
        }
    }
}