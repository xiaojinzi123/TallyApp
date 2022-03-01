package com.xiaojinzi.tally.base.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.theme.body3

@Composable
fun CommonActionItemView(
    verticalSpace: Dp = 16.dp,
    contentNameItem: StringItemDTO,
    contentValueItem: StringItemDTO? = null,
    @DrawableRes imgRsd: Int? = null,
    descItem: StringItemDTO? = null,
    onclick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onclick.invoke() }
            .padding(horizontal = 0.dp, vertical = verticalSpace)
            .nothing(),
        verticalAlignment = Alignment.Top,
    ) {

        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .nothing(),
        ) {
            Text(
                text = contentNameItem.nameAdapter(),
                style = MaterialTheme.typography.subtitle1,
            )
            descItem?.let {
                Spacer(modifier = Modifier.height(height = 4.dp))
                Text(
                    text = descItem.nameAdapter(),
                    style = MaterialTheme.typography.body3.copy(
                        color = Color.Gray
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.width(width = 16.dp))
        Row(
            modifier = Modifier
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            imgRsd?.let {
                Image(
                    modifier = Modifier
                        .size(size = 20.dp)
                        .nothing(),
                    painter = painterResource(id = imgRsd),
                    contentDescription = null
                )
            }
            contentValueItem?.let {
                Spacer(modifier = Modifier.width(width = 6.dp))
                Text(
                    text = contentValueItem.nameAdapter(),
                    style = MaterialTheme.typography.subtitle1,
                )
            }
            Spacer(modifier = Modifier.width(width = 4.dp))
            Image(
                modifier = Modifier
                    .size(size = 16.dp)
                    .nothing(),
                painter = painterResource(id = R.drawable.res_arrow_right1),
                contentDescription = null
            )
        }
    }
}

@Composable
fun CommonSwitchItemView(
    contentItem: StringItemDTO,
    descItem: StringItemDTO? = null,
    checked: Boolean,
    onclick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onclick.invoke() }
            .nothing(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .wrapContentHeight()
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .nothing(),
        ) {
            Text(
                text = contentItem.nameAdapter(),
                style = MaterialTheme.typography.subtitle1,
            )
            descItem?.let {
                Spacer(modifier = Modifier.height(height = 4.dp))
                Text(
                    text = descItem.nameAdapter(),
                    style = MaterialTheme.typography.body3.copy(
                        color = Color.Gray
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.width(width = 16.dp))
        Switch(
            checked = checked,
            onCheckedChange = {
                onclick.invoke()
            }
        )

    }
}

@Composable
fun CommonSurface1(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colors.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .nothing(),
    ) {
        content()
    }
}