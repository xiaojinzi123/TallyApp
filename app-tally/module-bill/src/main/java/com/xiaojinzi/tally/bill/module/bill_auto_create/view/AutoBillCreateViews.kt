package com.xiaojinzi.tally.bill.module.bill_auto_create.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.module.base.domain.CostEmptyState
import com.xiaojinzi.module.base.support.appInfoService
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.module.base.theme.Gray200
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.bill.R
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalMaterialApi
@Composable
private fun AutoBillCreateView() {
    val itemVPadding = 16.dp
    val itemNameTextStyle = MaterialTheme.typography.subtitle1
    val context = LocalContext.current
    val vm: AutoBillCreateViewModel = viewModel()
    val costStr by vm.costUseCase.costStrObservableDTO.collectAsState(initial = CostEmptyState())
    val dateTime by vm.timeObservableDTO.collectAsState(initial = null)
    val category by vm.categorySelectObservableDTO.collectAsState(initial = null)
    val account by vm.selectedAccountObservableDTO.collectAsState(initial = null)
    val note by vm.noteStrObservableDTO.collectAsState(initial = null)
    val canNext by vm.canNextObservableDTO.collectAsState(initial = false)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
    LaunchedEffect(key1 = sheetState) {
        snapshotFlow { sheetState.isVisible }
            .collect {
                if (!it) {
                    context.tryFinishActivity()
                }
            }
    }
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = MaterialTheme.colors.surface)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .nothing(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                        .nothing(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier
                            .size(size = 24.dp)
                            .nothing(),
                        painter = painterResource(id = appInfoService.appIconRsd),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(width = 16.dp))
                    Text(
                        text = stringResource(id = R.string.res_str_auto_bill),
                        style = MaterialTheme.typography.body1,
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                    Row(
                        modifier = Modifier
                            .clickable {
                                vm.toBillCreateView(context = context)
                            }
                            .padding(horizontal = 0.dp, vertical = 4.dp)
                            .nothing(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.res_str_advanced_bill),
                            style = MaterialTheme.typography.body1,
                        )
                        Image(
                            modifier = Modifier
                                .size(size = 16.dp)
                                .nothing(),
                            painter = painterResource(id = R.drawable.res_arrow_right1),
                            contentDescription = null
                        )
                    }

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = itemVPadding)
                        .nothing(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = stringResource(id = R.string.res_str_money),
                        style = itemNameTextStyle,
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                    Text(
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.small)
                            .background(color = MaterialTheme.colors.primary)
                            .clickable {
                                vm.costUseCase.toggleCostValue()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .nothing(),
                        text = stringResource(id = R.string.res_str_toggle_money),
                        style = MaterialTheme.typography.body2.copy(
                            color = MaterialTheme.colors.onPrimary
                        ),
                    )
                    Spacer(modifier = Modifier.width(width = 10.dp))
                    Text(
                        modifier = Modifier
                            // .fillMaxWidth()
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .nothing(),
                        text = if (costStr.length == 0) "0.00" else costStr.strValue,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.End,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = itemVPadding)
                        .nothing(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = stringResource(id = R.string.res_str_date),
                        style = itemNameTextStyle,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .nothing(),
                        text = if (dateTime == null) {
                            stringResource(id = R.string.res_str_now)
                        } else {
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm",
                                Locale.getDefault()
                            ).format(dateTime)
                        },
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.End,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.toChooseCategory(context = context)
                        }
                        .padding(horizontal = 0.dp, vertical = itemVPadding)
                        .nothing(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = stringResource(id = R.string.res_str_category),
                        style = itemNameTextStyle,
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                    category?.iconRsd?.let {
                        Image(
                            modifier = Modifier
                                .size(size = 20.dp)
                                .nothing(),
                            painter = painterResource(id = it),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(width = 6.dp))
                    }
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = category?.getStringItemVO()?.nameAdapter()
                            ?: stringResource(id = R.string.res_str_desc5),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.End,
                    )
                    Spacer(modifier = Modifier.width(width = 4.dp))
                    Image(
                        modifier = Modifier
                            .size(size = 16.dp)
                            .nothing(),
                        painter = painterResource(id = R.drawable.res_arrow_right1),
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.toChooseAccount(context = context)
                        }
                        .padding(horizontal = 0.dp, vertical = itemVPadding)
                        .nothing(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = stringResource(id = R.string.res_str_account),
                        style = itemNameTextStyle,
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                    account?.iconRsd?.let {
                        Image(
                            modifier = Modifier
                                .size(size = 20.dp)
                                .nothing(),
                            painter = painterResource(id = it),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(width = 6.dp))
                    }
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = account?.getStringItemVO()?.nameAdapter()
                            ?: stringResource(id = R.string.res_str_desc6),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.End,
                    )
                    Spacer(modifier = Modifier.width(width = 4.dp))
                    Image(
                        modifier = Modifier
                            .size(size = 16.dp)
                            .nothing(),
                        painter = painterResource(id = R.drawable.res_arrow_right1),
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.toFillNote(context = context)
                        }
                        .padding(horizontal = 0.dp, vertical = itemVPadding)
                        .nothing(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = stringResource(id = R.string.res_str_note),
                        style = itemNameTextStyle,
                    )
                    Text(
                        modifier = Modifier
                            .weight(weight = 1f, fill = true)
                            .wrapContentHeight()
                            .nothing(),
                        text = if (note.isNullOrEmpty()) stringResource(id = R.string.res_str_nothing) else note!!,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.End,
                    )
                    Spacer(modifier = Modifier.width(width = 4.dp))
                    Image(
                        modifier = Modifier
                            .size(size = 16.dp)
                            .nothing(),
                        painter = painterResource(id = R.drawable.res_arrow_right1),
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 12.dp)
                        .nothing(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        modifier = Modifier
                            .weight(weight = 1f, fill = true)
                            .wrapContentHeight()
                            .clip(shape = CircleShape)
                            .clickable {
                                context.tryFinishActivity()
                            }
                            .background(color = Color.LightGray)
                            .padding(horizontal = 0.dp, vertical = 16.dp)
                            .nothing(),
                        text = stringResource(id = R.string.res_str_cancel),
                        style = MaterialTheme.typography.subtitle1.copy(
                            color = MaterialTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.width(width = 16.dp))
                    Text(
                        modifier = Modifier
                            .weight(weight = 1f, fill = true)
                            .wrapContentHeight()
                            .clip(shape = CircleShape)
                            .clickable {
                                vm.addOrUpdateBill(context = context)
                            }
                            .background(color = if (canNext) MaterialTheme.colors.primary else Gray200)
                            .padding(horizontal = 0.dp, vertical = 16.dp)
                            .nothing(),
                        text = stringResource(id = R.string.res_str_confirm),
                        style = MaterialTheme.typography.subtitle1.copy(
                            color = MaterialTheme.colors.onPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    ) {
    }
}

@ExperimentalMaterialApi
@Composable
fun AutoBillCreateViewWrap() {
    AutoBillCreateView()
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun AutoBillCreateViewPreview() {
    AutoBillCreateView()
}