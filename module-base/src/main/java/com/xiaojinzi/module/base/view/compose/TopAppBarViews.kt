package com.xiaojinzi.module.base.view.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.xiaojinzi.module.base.R
import com.xiaojinzi.module.base.support.tryFinishActivity

@Composable
fun AppbarNormal(
    @DrawableRes backIconRsd: Int = R.drawable.res_back1,
    backClickListener: (() -> Unit)? = null,
    @StringRes
    titleRsd: Int = -1,
    title: String = "",
    titleAlign: TextAlign = TextAlign.Start,
    menu1IconRsd: Int? = null,
    menu1ClickListener: (() -> Unit)? = null,
    menu2IconRsd: Int? = null,
    menu2ClickListener: (() -> Unit)? = null,
    menu3IconRsd: Int? = null,
    menu3ClickListener: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val defaultBackListener: () -> Unit = {
        context.tryFinishActivity()
    }
    val targetBackListener = backClickListener ?: defaultBackListener
    TopAppBar(
        contentPadding = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.statusBars)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = targetBackListener) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = backIconRsd),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.size(width = 12.dp, height = 0.dp))
            val targetTitleString = if (titleRsd == -1) title else stringResource(id = titleRsd)
            Text(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .padding(end = 16.dp),
                text = targetTitleString,
                textAlign = titleAlign,
                style = MaterialTheme.typography.h6.copy(
                    color = Color.White
                )
            )
            if (menu3IconRsd == null) {
                Spacer(modifier = Modifier.width(width = 0.dp))
            } else {
                IconButton(
                    onClick = {
                        menu3ClickListener?.invoke()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = menu3IconRsd),
                        contentDescription = null
                    )
                }
            }
            if (menu2IconRsd == null) {
                Spacer(modifier = Modifier.width(width = 0.dp))
            } else {
                IconButton(
                    onClick = {
                        menu2ClickListener?.invoke()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = menu2IconRsd),
                        contentDescription = null
                    )
                }
            }
            if (menu1IconRsd == null) {
                Spacer(modifier = Modifier.width(width = 24.dp))
            } else {
                IconButton(
                    onClick = {
                        menu1ClickListener?.invoke()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = menu1IconRsd),
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.width(width = 8.dp))
        }
    }
}

@Preview
@Composable
private fun AppbarNormalPreview() {
    AppbarNormal(
        title = "测试",
        menu1IconRsd = R.drawable.res_right1,
    )
}