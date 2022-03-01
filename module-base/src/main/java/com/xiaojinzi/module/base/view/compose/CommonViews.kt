package com.xiaojinzi.module.base.view.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xiaojinzi.module.base.R
import com.xiaojinzi.support.ktx.nothing

@Composable
fun StateBar(
    content: @Composable () -> Unit
) {
    ProvideWindowInsets {
        val systemUiController = rememberSystemUiController()
        val darkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
        }
        content()
    }
}

@Composable
fun EmptyView(
    emptyText: String = stringResource(id = R.string.res_str_common_empty),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.res_lottie_empty1)
        )
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .aspectRatio(ratio = 1f),
            iterations = LottieConstants.IterateForever,
        )
        Text(
            text = emptyText,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
@ExperimentalFoundationApi
fun <T> GridView(
    modifier: Modifier = Modifier,
    items: List<T>,
    columnNumber: Int,
    headerContent: @Composable (index: Int) -> Unit = {},
    afterRowContent: @Composable (index: Int) -> Unit = {},
    // 最后一个 Item 的
    lastItemContent: (@Composable () -> Unit)? = null,
    contentItem: @Composable (item: T) -> Unit,
) {
    val realItemSize = items.size + (if (lastItemContent == null) 0 else 1)
    if (realItemSize == 0) {
        return
    }
    // 计算行数
    val rows = (realItemSize + columnNumber - 1) / columnNumber
    Box(
        modifier = modifier
            // .background(color = Color.DarkGray)
            .nothing(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                // .background(color = Color.Blue)
                .fillMaxWidth(),
        ) {
            // header 头
            Row(
                modifier = Modifier
                    // .background(color = Color.Red)
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (columnIndex in 0 until columnNumber) {
                    Box(
                        modifier = Modifier
                            // .background(color = Color.Red)
                            .weight(1f, fill = true)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                        propagateMinConstraints = true
                    ) {
                        headerContent(columnIndex)
                    }
                }
            }
            // 真正的内容
            for (rowIndex in 0 until rows) {
                Row(
                    modifier = Modifier
                        // .background(color = Color.Red)
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (columnIndex in 0 until columnNumber) {
                        val itemIndex = rowIndex * columnNumber + columnIndex
                        if (itemIndex < items.size) {
                            Box(
                                modifier = Modifier
                                    // .background(color = Color.Red)
                                    .weight(1f, fill = true)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center,
                                propagateMinConstraints = true
                            ) {
                                contentItem(items[itemIndex])
                            }
                        } else {
                            if (lastItemContent != null && itemIndex == items.size) {
                                Box(
                                    modifier = Modifier
                                        // .background(color = Color.Red)
                                        .weight(1f, fill = true)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center,
                                    propagateMinConstraints = true
                                ) {
                                    lastItemContent()
                                }
                            } else {
                                Spacer(Modifier.weight(1f, fill = true))
                            }
                        }
                    }
                }
                // 插入一个每行结束的行的内容
                afterRowContent(rowIndex)
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun GridViewPreview() {
    GridView(
        items = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
        columnNumber = 4,
        headerContent = {
            Text(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .nothing(),
                text = "标题$it"
            )
        },
        afterRowContent = {
            Box(
                modifier = Modifier
                    .background(color = Color.Red)
                    .fillMaxWidth()
                    .height(height = 20.dp)
            )
        },
        lastItemContent = {
            Text(
                text = "我是最后一个 Item",
                style = MaterialTheme.typography.body2,
            )
        }
    ) {
        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = "测试$it"
        )
    }
}

@Composable
fun CommonEditText(
    modifier: Modifier = Modifier,
    text: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholderTextRsd: Int? = null,
    onDelete: () -> Unit = {},
    onKeyboardDown: () -> Unit = {},
    onTextChanged: (String) -> Unit,
) {

    TextField(
        modifier = modifier,
        value = TextFieldValue(
            text = text,
            selection = TextRange(index = text.length),
        ),
        onValueChange = {
            onTextChanged.invoke(it.text)
        },
        placeholder = {
            placeholderTextRsd?.let {
                Text(
                    text = stringResource(id = placeholderTextRsd),
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = onDelete) {
                    Image(
                        modifier = Modifier.size(size = 24.dp),
                        painter = painterResource(id = R.drawable.res_delete2),
                        contentDescription = null
                    )
                }
            }
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onKeyboardDown.invoke()
            }
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}