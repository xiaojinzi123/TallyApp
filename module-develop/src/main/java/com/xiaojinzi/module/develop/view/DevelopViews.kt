package com.xiaojinzi.module.develop.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.service.develop.*
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.develop.R
import com.xiaojinzi.support.ktx.nothing

@Composable
fun DevelopItemInfoViewView(item: DevelopItemInfoViewConfig) {
    Row(
        modifier = Modifier.padding(
            top = 12.dp, bottom = 12.dp,
            start = 16.dp, end = 16.dp,
        )
    ) {
        Text(
            text = item.content,
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.width(width = 10.dp))
        item.observable?.let { targetObservable ->
            val content by targetObservable.collectAsState(initial = "")
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                text = content,
                style = MaterialTheme.typography.body2
            )
        }

    }
}

@Composable
fun DevelopItemCheckBoxView(item: DevelopItemCheckBoxConfig) {
}

@Composable
fun DevelopItemActionView(item: DevelopItemActionConfig) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(
                top = 12.dp, bottom = 12.dp,
                start = 16.dp, end = 16.dp,
            )
            .clickable { item.action.invoke(context) }
            .nothing()
    ) {
        Text(
            text = item.content,
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        Image(
            modifier = Modifier
                .size(size = 20.dp)
                .nothing(),
            painter = painterResource(id = R.drawable.res_arrow_right1),
            contentDescription = null
        )
    }
}

@Composable
fun DevelopItemView(item: DevelopItemConfig) {
    when (item) {
        is DevelopItemCheckBoxConfig -> {
            DevelopItemCheckBoxView(item = item)
        }
        is DevelopItemInfoViewConfig -> {
            DevelopItemInfoViewView(item = item)
        }
        is DevelopItemActionConfig -> {
            DevelopItemActionView(item = item)
        }
    }
}

@Composable
fun DevelopGroupView(group: DevelopGroupConfig) {
    Surface(
        modifier = Modifier.padding(
            horizontal = 16.dp, vertical = 12.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 0.dp)
                    .nothing()
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                        .nothing(),
                    text = group.groupName,
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                Text(
                    modifier = Modifier
                        .clickable {
                            group.action.invoke()
                        }
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                        .nothing(),
                    text = group.actionName,
                    style = MaterialTheme.typography.subtitle1
                )
            }
            Spacer(modifier = Modifier.height(height = 10.dp))
            Column {
                group
                    .items
                    .forEachIndexed { _, item ->
                        /*Divider(
                            modifier = Modifier.padding(start = 16.dp)
                        )*/
                        DevelopItemView(item = item)
                    }
            }
        }
    }
}

@Composable
fun DevelopView() {
    Surface(
        color = MaterialTheme.colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .nothing(),
        ) {
            DevelopFunctionService.getFunctionList()
                .flatMap { it.developMainGroup }
                .forEach {
                    DevelopGroupView(group = it)
                }
        }
    }
}

@Composable
fun DevelopViewWarp() {
    Scaffold(
        topBar = {
            AppbarNormal(
                title = "开发者界面"
            )
        }
    ) {
        DevelopView()
    }
}

@Preview
@Composable
fun DevelopViewPreview() {
    DevelopView()
}