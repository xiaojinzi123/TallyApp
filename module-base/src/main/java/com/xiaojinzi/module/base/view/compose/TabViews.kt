package com.xiaojinzi.module.base.view.compose

import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.module.base.theme.Yellow900
import com.xiaojinzi.support.ktx.nothing

@Keep
data class TabVO(
    val isSelect: Boolean,
    val content: StringItemDTO,
)

@Composable
fun AppTabView(
    backgroundColor: Color = Yellow900,
    items: List<TabVO>,
    itemClick: (index: Int) -> Unit = {},
) {
    Assert.assertTrue(b = items.size > 1, message = "item size = ${items.size}")
    Row(
        modifier = Modifier
            .width(intrinsicSize = IntrinsicSize.Max)
            .wrapContentHeight()
            .clip(shape = CircleShape)
            .background(color = backgroundColor)
            .padding(all = 1.dp)
            .nothing(),
    ) {

        val startShape = RoundedCornerShape(
            topStartPercent = 50, bottomStartPercent = 50
        )
        val centerShape = RectangleShape
        val endShape = RoundedCornerShape(
            topEndPercent = 50, bottomEndPercent = 50
        )

        items.forEachIndexed { index, item ->
            val targetShape = when (index) {
                0 -> {
                    startShape
                }
                items.lastIndex -> {
                    endShape
                }
                else -> {
                    centerShape
                }
            }
            val targetColor = if (item.isSelect) {
                backgroundColor
            } else {
                Color.White
            }
            Text(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    // .clip(shape = CircleShape)
                    .clip(
                        shape = targetShape
                    )
                    .clickable {
                        itemClick.invoke(index)
                    }
                    .background(color = targetColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .nothing(),
                text = item.content.nameAdapter(),
                style = MaterialTheme.typography.subtitle2.run {
                    if (item.isSelect) {
                        this.copy(
                            color = Color.White,
                        )
                    } else {
                        this
                    }
                },
                textAlign = TextAlign.Center,
            )

        }

    }
}

@Preview
@Composable
fun AppTabViewPreview() {
    AppTabView(
        items = (0..2)
            .map { item ->
                TabVO(
                    isSelect = item == 1,
                    content = "测试$item".toStringItemDTO()
                )
            }
    )
}