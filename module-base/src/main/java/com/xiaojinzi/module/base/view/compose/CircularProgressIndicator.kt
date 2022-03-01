package com.xiaojinzi.module.base.view.compose

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.ktx.nothing

private val CircularIndicatorDiameter = 50.dp

@Composable
fun CommonCircularProgressIndicator(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0)
    progress: Float,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = color.copy(
        alpha = 0.4f,
    ),
    circleSize: Dp = CircularIndicatorDiameter,
    strokeWidth: Dp = 6.dp,
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
    }
    Canvas(
        modifier = modifier
            .progressSemantics(progress)
            .size(circleSize)
            .nothing()
    ) {
        val startAngle = 270f
        val sweep = progress * 360f
        val diameterOffset = stroke.width / 2
        val arcDimen = this.size.width - 2 * diameterOffset
        // 绘制背景
        drawArc(
            color = backgroundColor,
            startAngle = startAngle,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
        // 绘制进度
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
    }
}

@Preview
@Composable
private fun CommonCircularProgressIndicatorPreview() {
    CommonCircularProgressIndicator(
        modifier = Modifier.nothing(),
        progress = 0.7f,
    )
}