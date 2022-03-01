package com.xiaojinzi.tally.base.view

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.Keep
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.theme.Yellow900
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.LogSupport
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.support.settingService
import com.xiaojinzi.tally.base.support.tallyNumberFormat1

private const val TAG = "CateGroupCostPercentViews"

private val colorRange = (0..255)

@Keep
data class CateGroupCostPercentItemVO(
    // 类别组的 ID
    val cateGroupId: String,
    @DrawableRes
    val cateGroupIconRsd: Int,
    val cateGroupName: StringItemDTO,
    val cost: Float = 0f,
    @FloatRange(from = 0.0, fromInclusive = true, to = 100.0, toInclusive = true)
    val costPercent: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    val progressPercent: Float = costPercent / 100f,
    val colorIndex: Int = cateGroupName.hashCode() and 255,
    val color: Color = Color(
        red = colorRange.random(),
        green = colorRange.random(),
        blue = colorRange.random(),
    )
) {
    init {
        cateGroupName.hashCode() and 255
    }
}

@Keep
data class CateGroupCostPercentGroupVO(
    // 花费
    val cost: Float,
    val items: List<CateGroupCostPercentItemVO> = emptyList()
)

@Composable
fun CateGroupCostPercentItemView(
    vo: CateGroupCostPercentItemVO,
    onClick: () -> Unit = {}
) {
    Spacer(modifier = Modifier.width(width = 10.dp))
    val costPercentAnim
            by animateFloatAsState(targetValue = vo.costPercent)
    val costAnim
            by animateFloatAsState(targetValue = vo.cost)
    val progressPercent
            by animateFloatAsState(targetValue = vo.progressPercent)
    val colorIsFollowPieChart by settingService
        .cateCostProgressPercentColorIsFollowPieChartObservableDTO
        .collectAsState(
            initial = false
        )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .padding(end = 16.dp),
    ) {

        IconButton(
            onClick = {
                onClick.invoke()
            },
            enabled = false
        ) {
            Image(
                modifier = Modifier
                    .size(size = 24.dp),
                painter = painterResource(id = vo.cateGroupIconRsd),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(width = 0.dp))
        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
        ) {
            Row {
                Text(
                    text = vo.cateGroupName.nameAdapter(),
                    style = MaterialTheme.typography.body2,
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(
                    text = "${costPercentAnim.tallyNumberFormat1()}%",
                    style = MaterialTheme.typography.body2,
                )
                Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                Text(
                    text = costAnim.tallyNumberFormat1(),
                    style = MaterialTheme.typography.body2,
                )
            }
            Spacer(modifier = Modifier.height(height = 4.dp))
            LogSupport.d(tag = TAG, content = "vo.cost = ${vo.cost}")
            LogSupport.d(tag = TAG, content = "vo.costPercent = ${vo.costPercent}")
            LogSupport.d(tag = TAG, content = "vo.progressPercent = ${vo.progressPercent}")
            LogSupport.d(tag = TAG, content = "progressPercent = $progressPercent")
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progressPercent)
                    .height(height = 6.dp)
                    .clip(shape = CircleShape)
                    .background(color = if (colorIsFollowPieChart) vo.color else Yellow900)
                    .nothing()
            )
        }
        Spacer(modifier = Modifier.width(width = 4.dp))
        Icon(
            modifier = Modifier
                .size(size = 16.dp)
                .nothing(),
            painter = painterResource(id = R.drawable.res_arrow_right1),
            contentDescription = null,
            tint = MaterialTheme.colors.secondary,
        )
    }

}

@Composable
fun CateGroupCostPercentView(
    vos: List<CateGroupCostPercentItemVO>,
    onItemClick: (index: Int, item: CateGroupCostPercentItemVO) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .nothing(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (vos.isEmpty()) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.res_lottie_empty2)
            )
            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.4f)
                    .aspectRatio(ratio = 1f)
                    .nothing(),
                iterations = LottieConstants.IterateForever,
            )
        } else {
            vos.forEachIndexed { index, item ->
                if (index > 0) {
                    Divider(
                        modifier = Modifier
                            .padding(start = 16.dp),
                    )
                }
                CateGroupCostPercentItemView(vo = item) {
                    onItemClick.invoke(
                        index, item
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CateGroupCostPercentViewPreview() {
    CateGroupCostPercentView(
        vos = listOf(
            CateGroupCostPercentItemVO(
                cateGroupId = INVALID_STRING,
                cateGroupIconRsd = R.drawable.res_bread1,
                cateGroupName = "测试1".toStringItemDTO(),
                cost = 300f,
                costPercent = 40f,
                progressPercent = 0.5f,
            ),
            CateGroupCostPercentItemVO(
                cateGroupId = INVALID_STRING,
                cateGroupIconRsd = R.drawable.res_bread1,
                cateGroupName = "测试2".toStringItemDTO(),
                cost = 50f,
                costPercent = 50f,
                progressPercent = 0.2f,
            ),
            CateGroupCostPercentItemVO(
                cateGroupId = INVALID_STRING,
                cateGroupIconRsd = R.drawable.res_bread1,
                cateGroupName = "测试3".toStringItemDTO(),
                cost = 5230f,
                costPercent = 80f,
                progressPercent = 0.8f,
            ),
        )
    )
}