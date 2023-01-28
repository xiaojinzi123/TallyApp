package com.xiaojinzi.tally.base.view

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import androidx.paging.map
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.flowlayout.FlowRow
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.*
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.theme.Red300
import com.xiaojinzi.module.base.view.ViewUseCase
import com.xiaojinzi.module.base.view.ViewUseCaseImpl
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.support.settingService
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyNumberFormat1
import com.xiaojinzi.tally.base.theme.body3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun TallyBillDetailDTO.toBillItemVO(): BillItemVO {
    val item = this
    val billItem = item.bill
    val account = item.account
    val transferTargetAccount = item.transferTargetAccount
    val cate = item.categoryWithGroup?.category
    val cateGroup = item.categoryWithGroup?.group
    return BillItemVO(
        billId = billItem.uid,
        reimburseType = billItem.reimburseType,
        isNotIncludedInIncomeAndExpenditure = billItem.isNotIncludedInIncomeAndExpenditure,
        billType = billItem.type,
        createTime = billItem.time,
        accountName = StringItemDTO(
            nameRsd = account.nameRsd,
            name = account.name,
        ),
        transferTargetAccountName = transferTargetAccount?.run {
            StringItemDTO(
                nameRsd = this.nameRsd,
                name = this.name,
            )
        },
        categoryGroupNameRes = cateGroup?.nameRsd,
        categoryGroupName = cateGroup?.name,
        categoryIcon = cate?.iconRsd,
        categoryNameRes = cate?.nameRsd,
        categoryName = cate?.name,
        cost = billItem.cost.tallyCostAdapter(),
        costAdjust = billItem.costAdjust.tallyCostAdapter(),
        type = cateGroup?.type,
        labelList = item.labelList.map { it.toTallyLabelVO() },
        imageUrlList = this.imageUrlList
    )
}

@Keep
data class BillItemVO(
    val billId: String,
    val reimburseType: ReimburseType,
    val isNotIncludedInIncomeAndExpenditure: Boolean,
    val billType: TallyBillTypeDTO,
    val createTime: Long,
    val accountName: StringItemDTO,
    val transferTargetAccountName: StringItemDTO?,
    @StringRes
    val categoryGroupNameRes: Int? = null,
    val categoryGroupName: String? = null,
    @DrawableRes
    val categoryIcon: Int?,
    @StringRes
    val categoryNameRes: Int? = null,
    val categoryName: String? = null,
    val cost: Float,
    val costAdjust: Float,
    val type: TallyCategoryGroupTypeDTO? = TallyCategoryGroupTypeDTO.Other,
    val labelList: List<TallyLabelVO> = emptyList(),
    val imageUrlList: List<String> = emptyList(),
) //
{

    @Composable
    fun getCategoryGroupNameAdapter(): String {
        return categoryGroupName ?: stringResource(id = categoryGroupNameRes!!)
    }

    @Composable
    fun getCategoryNameAdapter(): String {
        return categoryName ?: stringResource(id = categoryNameRes!!)
    }

    @Composable
    fun getItemName(): String {
        return when (billType) {
            TallyBillTypeDTO.Normal -> "${getCategoryGroupNameAdapter()} - ${getCategoryNameAdapter()}"
            TallyBillTypeDTO.Transfer -> stringResource(id = R.string.res_str_transfer)
            TallyBillTypeDTO.Reimbursement -> stringResource(id = R.string.res_str_reimbursement)
        }
    }

    val itemIconAdapter: Int
        get() = when (billType) {
            TallyBillTypeDTO.Normal -> categoryIcon!!
            TallyBillTypeDTO.Transfer -> R.drawable.res_transfer1
            TallyBillTypeDTO.Reimbursement -> R.drawable.res_reimbursement1
        }

    fun costAdapter(): Float {
        return if (billType == TallyBillTypeDTO.Transfer) {
            -cost
        } else {
            cost
        }
    }

    fun costAdjustAdapter(): Float {
        return if (billType == TallyBillTypeDTO.Transfer) {
            -costAdjust
        } else {
            costAdjust
        }
    }

}

@Keep
data class BillDayVO(
    val dayTime: Long,
    // 日期的显示: dd/MM/yyyy
    val dayStr1: String,
    // 日期的显示: MM-dd
    val dayStr2: String,
    @StringRes
    val dayOfWeek: Int,
    val dayIncomeCost: Float = 0f,
    val daySpendingCost: Float = 0f,
    // 一天的花费
    val dayCost: Float = dayIncomeCost + daySpendingCost,
    // 这天的记录
    val billList: List<BillItemVO> = emptyList()
)

@ViewLayer
interface BillListViewUseCase : ViewUseCase {

    /**
     * 当前的账单列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val currBillListObservableVO: Flow<List<BillDayVO>>

    /**
     * 当前的账单列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val currBillPageListObservableVO: Flow<PagingData<BillDayVO>>

}

class BillListViewUseCaseImpl(
    billDetailListObservableDTO: Flow<List<TallyBillDetailDTO>> = flowOf(),
    billDetailPageListObservableDTO: Flow<PagingData<BillDetailDayDTO>> = flowOf(),
) : ViewUseCaseImpl(), BillListViewUseCase {

    private val dayOfWeekList = listOf(
        R.string.res_str_sunday,
        R.string.res_str_monday,
        R.string.res_str_tuesday,
        R.string.res_str_wednesday,
        R.string.res_str_thursday,
        R.string.res_str_friday,
        R.string.res_str_saturday,
    )

    private val billDetailListObservableVO: Flow<List<BillItemVO>> = billDetailListObservableDTO
        .map { list ->
            list.map { it.toBillItemVO() }
        }
        .onStart {
            delay(400)
        }

    // 账单列表
    override val currBillListObservableVO: Flow<List<BillDayVO>> = billDetailListObservableVO
        .map { list ->
            list
                // 根据天分组
                .groupBy {
                    getDayInterval(timeStamp = it.createTime).first
                }
                .entries
                .map { entity ->
                    val dayIncomeCost =
                        entity
                            .value
                            .asSequence()
                            .filter { it.billType == TallyBillTypeDTO.Normal && !it.isNotIncludedInIncomeAndExpenditure }
                            .filter { it.costAdjust > 0f }
                            .map { it.costAdjustAdapter() }
                            .reduceOrNull { acc, value -> acc + value } ?: 0f
                    val daySpendingCost =
                        entity
                            .value
                            .asSequence()
                            .filter { it.billType == TallyBillTypeDTO.Normal && !it.isNotIncludedInIncomeAndExpenditure }
                            .filter { it.costAdjust < 0f }
                            .map { it.costAdjustAdapter() }
                            .reduceOrNull { acc, value -> acc + value } ?: 0f
                    val dayTime = Date(entity.key)
                    BillDayVO(
                        dayTime = dayTime.time,
                        dayStr1 = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(dayTime),
                        dayStr2 = SimpleDateFormat(
                            "MM-dd",
                            Locale.getDefault()
                        ).format(dayTime),
                        dayIncomeCost = dayIncomeCost,
                        daySpendingCost = daySpendingCost,
                        billList = entity.value,
                        // -2 是因为下标的偏移的 1
                        dayOfWeek = dayOfWeekList[getDayOfWeek(timeStamp = entity.key) - 1],
                    )
                }
        }
        .flowOn(Dispatchers.IO)
        .sharedStateIn(scope = scope)

    override val currBillPageListObservableVO: Flow<PagingData<BillDayVO>> =
        billDetailPageListObservableDTO
            .map { pageData ->
                pageData.map { item ->
                    BillDayVO(
                        dayTime = item.dayStartTime,
                        dayStr1 = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(Date(item.dayStartTime)),
                        dayStr2 = SimpleDateFormat(
                            "MM-dd",
                            Locale.getDefault()
                        ).format(Date(item.dayStartTime)),
                        dayIncomeCost = item.dayIncomeCost,
                        daySpendingCost = item.daySpendingCost,
                        billList = item.billList.map {
                            it.toBillItemVO()
                        },
                        // 是因为下标的偏移的 1
                        dayOfWeek = dayOfWeekList[getDayOfWeek(timeStamp = item.dayStartTime) - 1],
                    )
                }
            }

}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun BillDayHeaderItemView(
    dayVO: BillDayVO,
    dayTitleHorizontalPadding: Dp,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.background)
            .padding(top = 4.dp)
            .combinedClickable(
                onLongClick = {
                    Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                        // 时间的小时和分钟的部分使用了当前的时间的
                        .putLong("time",
                            dayVO.dayTime
                                .let {
                                    Calendar.getInstance(Locale.getDefault())
                                        .apply {
                                            this.timeInMillis = it
                                            this.set(
                                                Calendar.HOUR_OF_DAY,
                                                getHourByTimeStamp(timeStamp = System.currentTimeMillis())
                                            )
                                            this.set(
                                                Calendar.MINUTE,
                                                getMinuteByTimeStamp(timeStamp = System.currentTimeMillis())
                                            )
                                        }
                                        .timeInMillis
                                }
                        )
                        .forward()
                }
            ) {
                // empty
            }
            .padding(horizontal = dayTitleHorizontalPadding, vertical = 0.dp)
            .nothing(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .nothing(),
            text = dayVO.dayStr1,
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = stringResource(id = dayVO.dayOfWeek),
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        Text(
            text = (if (dayVO.dayCost > 0) "+" else "") + dayVO.dayCost.tallyNumberFormat1(),
            style = MaterialTheme.typography.subtitle1,
            color = Color.Red,
        )
    }
}


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun BillItemView(
    vo: BillItemVO,
    onAfterDelete: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isShowImageInBillList by settingService.isShowImageInBillListObservableDTO.collectAsState(
        initial = false
    )
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colors.surface,
            )
            .combinedClickable(
                onClick = {
                    Router
                        .with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_BILL_DETAIL)
                        .putString("billDetailId", vo.billId)
                        .forward()
                },
                onLongClick = {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.res_str_is_confirm_to_delete)
                        .setNegativeButton(R.string.res_str_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.res_str_ensure) { dialog, _ ->
                            dialog.dismiss()
                            scope.launch {
                                tallyBillService.deleteById(id = vo.billId)
                                onAfterDelete.invoke()
                            }
                        }
                        .show()
                }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .nothing()
    ) {
        Column {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(size = 20.dp),
                    painter = painterResource(id = vo.itemIconAdapter),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(width = 6.dp))
                Column(
                    modifier = Modifier
                        .weight(weight = 4f, fill = true)
                        .wrapContentHeight()
                        .nothing(),
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = vo.getItemName(),
                        style = MaterialTheme.typography.subtitle1,
                    )
                    if (vo.labelList.isNotEmpty()) {
                        FlowRow(
                            mainAxisSpacing = 4.dp,
                            crossAxisSpacing = 4.dp,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .nothing(),
                        ) {
                            vo.labelList.forEachIndexed { _, item ->
                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = Color(color = item.colorLong),
                                            shape = CircleShape,
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .nothing(),
                                    text = item.name.nameAdapter(),
                                    style = MaterialTheme.typography.body2.copy(
                                        fontSize = 8.sp
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(width = 16.dp))
                Column(
                    modifier = Modifier
                        .weight(weight = 3f, fill = true)
                        .wrapContentHeight()
                        .nothing(),
                    horizontalAlignment = Alignment.End,
                ) {
                    val costStr =
                        if (vo.billType == TallyBillTypeDTO.Transfer) {
                            (abs(vo.costAdjustAdapter())).tallyNumberFormat1()
                        } else {
                            (if (vo.costAdjustAdapter() > 0) "+" else "") + vo.costAdjustAdapter().tallyNumberFormat1()
                        }
                    // 显示金额
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .nothing(),
                        text = costStr,
                        style = MaterialTheme.typography.body1,
                    )
                    // 如果是转账, 显示账户 --> 账户
                    if (vo.billType == TallyBillTypeDTO.Transfer) {
                        Text(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 0.dp, vertical = 2.dp)
                                .nothing(),
                            text = "${vo.accountName.nameAdapter()} → ${vo.transferTargetAccountName?.nameAdapter() ?: ""}",
                            style = MaterialTheme.typography.body3,
                            textAlign = TextAlign.End,
                        )
                    } else {

                        Text(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 0.dp, vertical = 2.dp)
                                .nothing(),
                            text = vo.accountName.nameAdapter(),
                            style = MaterialTheme.typography.body3,
                            textAlign = TextAlign.End,
                        )

                        if (vo.reimburseType != ReimburseType.NoReimburse) { // 如果报销类型不是不用报销的类型
                            Text(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(horizontal = 0.dp, vertical = 2.dp)
                                    .nothing(),
                                text = vo.reimburseType.nameStringItem.nameAdapter(),
                                style = MaterialTheme.typography.body3.copy(
                                    color = Red300,
                                ),
                                textAlign = TextAlign.End,
                            )
                        }

                        if (vo.isNotIncludedInIncomeAndExpenditure) { // 如果不计入收支
                            Text(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(horizontal = 0.dp, vertical = 2.dp)
                                    .nothing(),
                                text = stringResource(id = R.string.res_str_not_included_in_income_and_expenditure),
                                style = MaterialTheme.typography.body3.copy(
                                    color = Red300,
                                ),
                                textAlign = TextAlign.End,
                            )
                        }

                    }
                }

            }
            if (isShowImageInBillList && vo.imageUrlList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(height = 12.dp))
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                ) {
                    vo.imageUrlList.forEach { url ->
                        val imagePainter = rememberImagePainter(
                            data = url,
                            builder = {
                                this.crossfade(enable = true)
                                this.fallback(drawable = ImageDefault.FallbackImage)
                                this.placeholder(drawable = ImageDefault.PlaceholderImage)
                                this.error(drawable = ImageDefault.ErrorImage)
                                this.allowHardware(enable = true)
                                this.allowRgb565(enable = true)
                                this.scale(scale = Scale.FILL)
                            }
                        )
                        Image(
                            painter = imagePainter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(shape = MaterialTheme.shapes.small)
                                .clickable {
                                    Router.with(context)
                                        .hostAndPath(RouterConfig.SUPPORT_IMAGE_PREVIEW)
                                        .putString("url", url)
                                        .forward()
                                }
                                .nothing()
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun BillDayItemView(
    dayVO: BillDayVO,
    isItemsRound: Boolean,
    dayTitleHorizontalPadding: Dp,
    onAfterDelete: () -> Unit = {},
) {
    Column {
        BillDayHeaderItemView(
            dayVO = dayVO,
            dayTitleHorizontalPadding = dayTitleHorizontalPadding,
        )
        Surface(
            shape = if (isItemsRound) {
                MaterialTheme.shapes.medium
            } else {
                RectangleShape
            },
            color = MaterialTheme.colors.surface,
        ) {
            Column {
                dayVO.billList
                    .forEachIndexed { index, vo ->
                        if (index > 0) {
                            Divider(
                                color = MaterialTheme.colors.background,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                        BillItemView(
                            vo = vo,
                            onAfterDelete = onAfterDelete,
                        )
                    }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun BillListView(
    modifier: Modifier = Modifier,
    vos: List<BillDayVO>,
    isItemsRound: Boolean = false,
    dayTitleHorizontalPadding: Dp = 16.dp,
    emptyTipContent: String = stringResource(id = R.string.res_str_bill_is_empty_tip1),
    onEmptyClick: (() -> Unit)? = null,
    onClickEmptyView: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val defaultEmptyClick = {
        Router
            .with(context)
            .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
            .forward()
    }
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier
    ) {
        if (vos.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .clickable {
                        if (onClickEmptyView == null) {
                            if (onEmptyClick == null) {
                                defaultEmptyClick.invoke()
                            } else {
                                onEmptyClick.invoke()
                            }
                        }
                    }
                    .fillMaxSize()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.res_lottie_work1)
                )
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.6f)
                        .aspectRatio(ratio = 1f),
                    iterations = LottieConstants.IterateForever,
                )
                Text(
                    text = emptyTipContent,
                    style = MaterialTheme.typography.body1,
                )
            }
        } //
        else {
            LazyColumn(
                modifier = Modifier
                    // .verticalScroll(state = scrollState)
                    .padding(bottom = 10.dp)
            ) {
                items(items = vos) { item ->
                    BillDayItemView(
                        dayVO = item,
                        isItemsRound = isItemsRound,
                        dayTitleHorizontalPadding = dayTitleHorizontalPadding,
                    )
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun BillPageListView(
    modifier: Modifier = Modifier,
    vos: LazyPagingItems<BillDayVO>,
    isItemsRound: Boolean = false,
    dayTitleHorizontalPadding: Dp = 16.dp,
    emptyTipContent: String = stringResource(id = R.string.res_str_bill_is_empty_tip1),
    onEmptyClick: (() -> Unit)? = null,
    onClickEmptyView: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val defaultEmptyClick = {
        Router
            .with(context)
            .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
            .forward()
    }
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier
    ) {
        if (vos.itemCount == 0) {
            Column(
                modifier = Modifier
                    .clickable {
                        if (onClickEmptyView == null) {
                            if (onEmptyClick == null) {
                                defaultEmptyClick.invoke()
                            } else {
                                onEmptyClick.invoke()
                            }
                        }
                    }
                    .fillMaxSize()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.res_lottie_work1)
                )
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.6f)
                        .aspectRatio(ratio = 1f),
                    iterations = LottieConstants.IterateForever,
                )
                Text(
                    text = emptyTipContent,
                    style = MaterialTheme.typography.body1,
                )
            }
        } //
        else {
            val lazyListState = rememberLazyListState()
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    // .verticalScroll(state = scrollState)
                    .padding(bottom = 10.dp)
                    .nothing(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(items = vos) { item ->
                    item ?: return@items
                    BillDayItemView(
                        dayVO = item,
                        isItemsRound = isItemsRound,
                        dayTitleHorizontalPadding = dayTitleHorizontalPadding,
                        onAfterDelete = {
                            scope.launch {
                                lazyListState.scrollToItem(index = 0)
                            }
                        },
                    )
                }
                vos.apply {
                    when {
                        this.loadState.refresh is LoadState.Loading -> {

                        }
                        this.loadState.append is LoadState.Loading -> {
                            item {
                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.RawRes(R.raw.res_lottie_loading4)
                                )
                                LottieAnimation(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = 1f)
                                        .aspectRatio(ratio = 3f)
                                        // .background(color = Color.Red)
                                        .nothing(),
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BillListViewPreview() {
    BillListView(
        vos = listOf(
            BillDayVO(
                dayTime = System.currentTimeMillis(),
                dayStr1 = "测试1",
                dayStr2 = "测试2",
                dayOfWeek = R.string.res_str_monday,
                billList = listOf(
                    BillItemVO(
                        billId = INVALID_STRING,
                        reimburseType = ReimburseType.NoReimburse,
                        isNotIncludedInIncomeAndExpenditure = false,
                        billType = TallyBillTypeDTO.Normal,
                        createTime = 1,
                        accountName = StringItemDTO(
                            name = "测试"
                        ),
                        transferTargetAccountName = StringItemDTO(
                            name = "测试"
                        ),
                        categoryGroupName = "测试",
                        categoryIcon = R.drawable.res_category1,
                        categoryName = "测试",
                        cost = 100f,
                        costAdjust = 100f,
                        labelList = listOf(
                            TallyLabelVO(
                                name = "标签1".toStringItemDTO(),
                                colorLong = Color.Green.toArgb()
                            ),
                            TallyLabelVO(
                                name = "标签2".toStringItemDTO(),
                                colorLong = Color.Red.toArgb()
                            ),
                        )
                    ),
                    BillItemVO(
                        billId = INVALID_STRING,
                        reimburseType = ReimburseType.NoReimburse,
                        isNotIncludedInIncomeAndExpenditure = false,
                        billType = TallyBillTypeDTO.Transfer,
                        createTime = 1,
                        accountName = StringItemDTO(
                            name = "测试"
                        ),
                        transferTargetAccountName = StringItemDTO(
                            name = "测试"
                        ),
                        categoryGroupName = "测试",
                        categoryIcon = R.drawable.res_category1,
                        categoryName = "测试",
                        cost = 100f,
                        costAdjust = 100f,
                    ),
                )
            ),
            BillDayVO(
                dayTime = System.currentTimeMillis(),
                dayStr1 = "测试1",
                dayStr2 = "测试2",
                dayOfWeek = R.string.res_str_monday,
                billList = listOf(
                    BillItemVO(
                        billId = INVALID_STRING,
                        reimburseType = ReimburseType.NoReimburse,
                        isNotIncludedInIncomeAndExpenditure = false,
                        billType = TallyBillTypeDTO.Normal,
                        createTime = 1,
                        accountName = StringItemDTO(
                            name = "测试"
                        ),
                        transferTargetAccountName = StringItemDTO(
                            name = "测试"
                        ),
                        categoryGroupName = "测试",
                        categoryIcon = R.drawable.res_category1,
                        categoryName = "测试",
                        cost = 100f,
                        costAdjust = 100f,
                    ),
                    BillItemVO(
                        billId = INVALID_STRING,
                        reimburseType = ReimburseType.NoReimburse,
                        isNotIncludedInIncomeAndExpenditure = false,
                        billType = TallyBillTypeDTO.Normal,
                        createTime = 1,
                        accountName = StringItemDTO(
                            name = "测试"
                        ),
                        transferTargetAccountName = StringItemDTO(
                            name = "测试"
                        ),
                        categoryGroupName = "测试",
                        categoryIcon = R.drawable.res_category1,
                        categoryName = "测试",
                        cost = 100f,
                        costAdjust = 100f,
                    ),
                )
            ),
        )
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun BillListViewPreview2() {
    BillListView(vos = emptyList())
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun BillDayHeaderItemViewPreview() {
    BillDayHeaderItemView(
        dayVO = BillDayVO(
            dayTime = System.currentTimeMillis(),
            dayStr1 = "测试1",
            dayStr2 = "测试2",
            dayOfWeek = R.string.res_str_monday,
            billList = listOf(
                BillItemVO(
                    billId = INVALID_STRING,
                    reimburseType = ReimburseType.NoReimburse,
                    isNotIncludedInIncomeAndExpenditure = false,
                    billType = TallyBillTypeDTO.Normal,
                    createTime = 1,
                    accountName = StringItemDTO(
                        name = "测试"
                    ),
                    transferTargetAccountName = StringItemDTO(
                        name = "测试"
                    ),
                    categoryGroupName = "测试",
                    categoryIcon = R.drawable.res_category1,
                    categoryName = "测试",
                    cost = 100f,
                    costAdjust = 100f,
                ),
                BillItemVO(
                    billId = INVALID_STRING,
                    reimburseType = ReimburseType.NoReimburse,
                    isNotIncludedInIncomeAndExpenditure = false,
                    billType = TallyBillTypeDTO.Normal,
                    createTime = 1,
                    accountName = StringItemDTO(
                        name = "测试"
                    ),
                    transferTargetAccountName = StringItemDTO(
                        name = "测试"
                    ),
                    categoryGroupName = "测试",
                    categoryIcon = R.drawable.res_category1,
                    categoryName = "测试",
                    cost = 100f,
                    costAdjust = 100f,
                ),
            )
        ),
        dayTitleHorizontalPadding = 0.dp
    )
}