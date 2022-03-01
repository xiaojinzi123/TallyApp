package com.xiaojinzi.tally.base.view

import android.graphics.PointF
import androidx.annotation.FloatRange
import androidx.annotation.Keep
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.support.tallyNumberFormat1

private val testVO by lazy {
    PieChartVO(
        content = "总支出：100.00".toStringItemDTO(),
        items = listOf(
            PieChartItemVO(
                content = "夜宵 44.00".toStringItemDTO(),
                percent = 0.2f,
            ),
            PieChartItemVO(
                content = "公交 33.00".toStringItemDTO(),
                percent = 0.3f,
            ),
            PieChartItemVO(
                content = "礼金红包 1000.00".toStringItemDTO(),
                percent = 0.4f,
            ),
            PieChartItemVO(
                content = "测试4".toStringItemDTO(),
                percent = 0.1f,
            ),
        )
    )
}

@Keep
data class PieChartVO(
    // 中间的空白的比例
    val centerCircleRatio: Float = 5f / 7f,
    val content: StringItemDTO,
    val items: List<PieChartItemVO>,
)

@Keep
data class PieChartItemVO(
    val content: StringItemDTO,
    val content2: StringItemDTO? = null,
    @FloatRange(from = 0.0, to = 1.0)
    val percent: Float,
    val color: Color = Color(
        red = (0..255).random(),
        green = (0..255).random(),
        blue = (0..255).random(),
    )
)

fun CateGroupCostPercentItemVO.toPieChartItemVO(): PieChartItemVO {
    return PieChartItemVO(
        content = this.cateGroupName,
        content2 = this.cost.tallyNumberFormat1().toStringItemDTO(),
        percent = this.costPercent / 100f,
        color = this.color,
    )
}

@ExperimentalAnimationApi
@Composable
private fun PieChartCoreView(
    modifier: Modifier = Modifier.nothing(),
    vo: PieChartVO,
) {
    AnimatedVisibility(
        visible = true,
        enter = scaleIn()
    ) {
        Canvas(
            modifier = modifier,
        ) {
            val targetSizeValue = this.size.minDimension
            val centerCircleRadius = targetSizeValue * vo.centerCircleRatio / 2f
            val targetSize = this.size.copy(
                width = targetSizeValue,
                height = targetSizeValue,
            )
            val centerPoint = PointF(
                this.size.width / 2f,
                this.size.height / 2f,
            )
            this.clipPath(
                path = AndroidPath().apply {
                    this.addRoundRect(
                        roundRect = RoundRect(
                            left = centerPoint.x - centerCircleRadius,
                            top = centerPoint.y - centerCircleRadius,
                            right = centerPoint.x + centerCircleRadius,
                            bottom = centerPoint.y + centerCircleRadius,
                            radiusX = centerCircleRadius,
                            radiusY = centerCircleRadius,
                        )
                    )
                },
                clipOp = ClipOp.Difference,
            ) {
                // 总共的角度
                val totalAngle = 360f
                var startAngle = -90f
                vo.items.forEachIndexed { index, itemVO ->
                    val sweepAngle = totalAngle * itemVO.percent
                    drawArc(
                        topLeft = Offset(
                            x = (this.size.width - targetSizeValue) / 2f,
                            y = (this.size.height - targetSizeValue) / 2f,
                        ),
                        size = targetSize,
                        color = itemVO.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        style = Fill,
                    )
                    startAngle += sweepAngle
                }
            }
            /*drawCircle(
                color = Color.White,
                radius = centerCircleRadius,
                center = Offset(x = targetSizeValue / 2f, y = targetSizeValue / 2f),
            )*/
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun PieChartView(
    modifier: Modifier = Modifier.nothing(),
    vo: PieChartVO,
) {
    if (vo.items.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .nothing(),
            horizontalArrangement = Arrangement.Center,
        ) {
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
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .nothing(),
            verticalAlignment = Alignment.Top
        ) {
            PieChartCoreView(
                modifier = Modifier
                    .widthIn(
                        min = 0.dp, max = 240.dp
                    )
                    .fillMaxWidth(fraction = 0.5f)
                    .aspectRatio(ratio = 1f)
                    .nothing(),
                vo = vo
            )
            Spacer(modifier = Modifier.width(width = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(fraction = 1f)
                    .wrapContentHeight()
                    .nothing(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                FlowRow(
                    modifier = Modifier
                        .wrapContentSize()
                        .nothing(),
                    mainAxisAlignment = FlowMainAxisAlignment.Start,
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 4.dp,
                ) {
                    vo.items.forEachIndexed { _, itemVO ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 0.dp, vertical = 4.dp)
                                .nothing(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(size = 6.dp)
                                    .clip(shape = CircleShape)
                                    .background(
                                        color = itemVO.color
                                    )
                                    .nothing(),
                            )
                            Spacer(modifier = Modifier.width(width = 2.dp))
                            Text(
                                text = itemVO.content.nameAdapter(),
                                style = MaterialTheme.typography.body2,
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
private fun PieChartViewPreview() {
    PieChartView(vo = testVO)
}

@ExperimentalAnimationApi
@Preview
@Composable
private fun PieChartCoreViewPreview() {
    PieChartCoreView(
        modifier = Modifier
            .width(400.dp)
            .height(600.dp)
            .nothing(),
        vo = testVO
    )
}