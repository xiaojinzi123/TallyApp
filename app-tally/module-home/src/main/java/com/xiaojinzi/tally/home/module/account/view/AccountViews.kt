package com.xiaojinzi.tally.home.module.account.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.home.R

@Composable
fun AccountItemView(vo: AccountItemVO) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(size = 20.dp)
                .nothing(),
            painter = painterResource(id = R.drawable.res_delete1),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .nothing(),
                    text = "白条",
                    style = MaterialTheme.typography.body1
                )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .nothing(),
                    text = "-40.00",
                    style = MaterialTheme.typography.body1
                )
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 10.dp)
                    .clip(shape = CircleShape)
                    .background(color = Color.Red),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.6f)
                        .fillMaxHeight()
                        .clip(shape = CircleShape)
                        .background(color = Color.Green),
                ) {
                }
            }
            Spacer(modifier = Modifier.height(height = 4.dp))
            Text(text = "可用额度: 60", style = MaterialTheme.typography.body2)
        }

    }
}

@Preview
@Composable
fun AccountItemViewPreview() {
    AccountItemView(
        vo = AccountItemVO(
            iconRsd = R.drawable.res_delete1,
        )
    )
}

@Composable
fun AccountGroupView(vo: AccountGroupVO) {

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Column {
            Row {
                Text(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .nothing(),
                    text = "账户1",
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .nothing(),
                    text = "-40.00",
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Start,
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Image(
                    modifier = Modifier
                        .size(size = 20.dp),
                    painter = painterResource(id = R.drawable.res_arrow_right1),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(height = 16.dp))
            vo.items.forEach { item ->
                AccountItemView(vo = item)
            }
        }

    }

}

@Composable
fun AccountView() {

}

@Composable
fun AccountViewWrap() {

}

@Preview
@Composable
fun AccountGroupViewPreview() {
    AccountGroupView(
        vo = AccountGroupVO(
            items = listOf(
                AccountItemVO(iconRsd = R.drawable.res_arrow_right1),
                AccountItemVO(iconRsd = R.drawable.res_arrow_right1),
            )
        )
    )
}

@Preview
@Composable
fun AccountViewPreview() {
}