package com.xiaojinzi.module.base.view.compose

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.support.ktx.nothing
import kotlin.math.roundToInt

@Composable
fun WheelItem(
    content: StringItemDTO,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .nothing(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = content.nameAdapter(),
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
private fun WheelColumnView(
    itemHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            val size = itemHeight.toPx().toInt()
            val placeables = measurables.map {
                it.measure(constraints)
            }
            val totalHeight =
                placeables.map { it.height }.reduceOrNull { acc, item -> acc + item } ?: 0
            layout(constraints.maxWidth, totalHeight) {
                var currentY = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(x = 0, y = currentY)
                    currentY += placeable.height
                }
            }
        }
    )

}

@ExperimentalMaterialApi
@Composable
fun <T> WheelView(
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,
    lineWidth: Dp = 1.dp,
    itemHeight: Dp = 48.dp,
    items: List<T>,
    wheelState: WheelState = rememberWheelState(initIndex = 0),
    itemContent: @Composable (item: T) -> Unit,
) {
    WheelViewWithIndex(
        modifier = modifier,
        visibleCount = visibleCount,
        lineWidth = lineWidth,
        itemHeight = itemHeight,
        items = items,
        wheelState = wheelState,
    ) { _, item ->
        itemContent(item)
    }
}

@ExperimentalMaterialApi
@Composable
fun <T> WheelViewWithIndex(
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,
    lineWidth: Dp = 1.dp,
    itemHeight: Dp = 48.dp,
    items: List<T>,
    wheelState: WheelState = rememberWheelState(initIndex = 0),
    itemContent: @Composable (index: Int, item: T) -> Unit,
) {

    val itemCount = items.size
    // 必须至少有一个
    Assert.assertTrue(b = itemCount > 0, message = "items is empty")
    // 选中的下标不可以超过 items 的长度
    Assert.assertTrue(b = wheelState.currentIndex < itemCount)
    val targetSelectIndex: Int = (visibleCount) / 2
    val containerHeight: Dp = itemHeight * visibleCount
    val itemHeightPx: Float = with(LocalDensity.current) { itemHeight.toPx() }
    // 因为容器内的内容是居中的, 为了贴合 top, 所以计算需要的便宜
    val offsetY: Float = (itemCount - visibleCount) * itemHeightPx / 2f

    // 当第一项 index = 0 作为被选择项目的时候, 测试数据如下
    // itemCount    anchorStart     anchorEnd       ...
    // 1            0               0
    // 2            -1              0
    // 3            -2              0

    // 当考虑 visibleCount 的中间的 targetSelectIndex 作为被选择项目的时候,
    // 只需要将 [anchorStart, anchorEnd] 整体移动一个偏移量 anchorOffset
    val anchorOffset = targetSelectIndex - 0

    val anchorList = ((1 - itemCount)..(0))
        .map {
            it + anchorOffset
        }
        .map {
            (itemHeightPx * it) to it
        }

    val anchorMap = anchorList.toMap()

    val firstAnchorIndex = anchorList.last().second

    val swipeableState = rememberSwipeableState(firstAnchorIndex - wheelState.currentIndex)
    LaunchedEffect(key1 = swipeableState) {
        snapshotFlow { swipeableState.currentValue }
            .collect {
                val itemIndex = firstAnchorIndex - it
                if (wheelState.currentIndex != itemIndex) {
                    wheelState.currentIndex = itemIndex
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(containerHeight)
            .swipeable(
                state = swipeableState,
                anchors = anchorMap,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Vertical
            )
            .drawWithContent {
                val width = drawContext.size.width
                drawContent()
                drawLine(
                    color = Color.LightGray,
                    start = Offset(
                        0f,
                        targetSelectIndex * itemHeightPx,
                    ),
                    end = Offset(
                        width,
                        targetSelectIndex * itemHeightPx,
                    ),
                    strokeWidth = lineWidth.toPx()
                )
                drawLine(
                    color = Color.LightGray,
                    start = Offset(
                        0f,
                        (targetSelectIndex + 1) * itemHeightPx,
                    ),
                    end = Offset(
                        width,
                        (targetSelectIndex + 1) * itemHeightPx,
                    ),
                    strokeWidth = lineWidth.toPx()
                )
            }
            .graphicsLayer { clip = true }
            // .padding(horizontal = 16.dp, vertical = 0.dp)
            .nothing(),
        contentAlignment = Alignment.Center,
    ) {
        WheelColumnView(
            itemHeight = itemHeight,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                .nothing(),
        ) {
            items.forEachIndexed { index, t ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .nothing(),
                ) {
                    itemContent(index, t)
                }
            }
        }
    }

}

class WheelState(currentIndex: Int) {

    var currentIndex by mutableStateOf(currentIndex)

    companion object {
        val Saver = object : Saver<WheelState, Int> {
            override fun restore(value: Int): WheelState {
                return WheelState(currentIndex = value)
            }

            override fun SaverScope.save(value: WheelState): Int {
                return value.currentIndex
            }
        }
    }

}

@Composable
fun rememberWheelState(
    initIndex: Int = 0
) = rememberSaveable(saver = WheelState.Saver) {
    WheelState(currentIndex = initIndex)
}