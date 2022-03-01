package com.xiaojinzi.tally.my.module.setting.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.appInfoService
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.my.R

@Composable
private fun AboutItemView(
    name: StringItemDTO,
    onItemClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clickable {
                onItemClick.invoke()
            }
            .padding(horizontal = 0.dp, vertical = 12.dp)
            .nothing(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
            text = name.nameAdapter(),
            style = MaterialTheme.typography.body1,
        )
        Spacer(modifier = Modifier.width(width = 16.dp))
        Image(
            modifier = Modifier
                .size(size = 20.dp),
            painter = painterResource(id = R.drawable.res_arrow_right1),
            contentDescription = null
        )
    }
}

@Composable
private fun AboutView() {
    val context = LocalContext.current
    Column {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .nothing(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(height = 16.dp))
            /*Image(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.3f)
                    .aspectRatio(ratio = 1f)
                    .clip(shape = CircleShape)
                    .background(color = colorResource(id = appIconBgColorRsd))
                    .nothing(),
                painter = painterResource(id = appIconRsd),
                contentDescription = null
            )*/
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(com.xiaojinzi.module.base.R.raw.res_lottie_cat2)
            )
            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .aspectRatio(ratio = 1f),
                iterations = LottieConstants.IterateForever,
            )
            Spacer(modifier = Modifier.height(height = 24.dp))
            Text(
                text = stringResource(id = R.string.res_tally_app_name),
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(modifier = Modifier.height(height = 4.dp))
            Text(
                text = "v${appInfoService.appVersionName}",
                style = MaterialTheme.typography.body2,
            )
        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .nothing(),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp, vertical = 0.dp)
                    .nothing(),
            ) {

                Text(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_technical_support),
                    style = MaterialTheme.typography.body2.copy(
                        color = Color.Gray
                    ),
                )
                AboutItemView(name = StringItemDTO(name = "Kotlin")) {
                    Router.with(context)
                        .url("https://kotlinlang.org/")
                        .forward()
                }
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .nothing(),
                )
                AboutItemView(name = StringItemDTO(name = "Compose")) {
                    Router.with(context)
                        .url("https://developer.android.com/jetpack/compose")
                        .forward()
                }
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .nothing(),
                )
                AboutItemView(name = StringItemDTO(nameRsd = R.string.res_str_component_lib)) {
                    Router.with(context)
                        .url("https://github.com/xiaojinzi123/Component")
                        .forward()
                }
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .nothing(),
                )
                AboutItemView(name = StringItemDTO(name = "Room")) {
                    Router.with(context)
                        .url("https://developer.android.com/jetpack/androidx/releases/room")
                        .forward()
                }
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                        .nothing(),
                )
                AboutItemView(name = StringItemDTO(nameRsd = R.string.res_str_support_lib)) {
                    Router.with(context)
                        .url("https://github.com/xiaojinzi123/AndroidSupport")
                        .forward()
                }

            }

        }

    }
}

@Composable
fun AboutViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_about
            )
        }
    ) {
        AboutView()
    }
}

@Preview
@Composable
private fun AboutViewPreview() {
    AboutView()
}