package com.xiaojinzi.tally.bill.module.category.view

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.bean.INVALID_STRING
import com.xiaojinzi.module.base.view.compose.GridView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.bill.R

@Keep
data class CategoryVO(
    val categoryId: String,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
)

@Keep
data class CategoryGroupVO(
    val cateGroupId: String,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val titleNameRsd: Int? = null,
    val titleName: String? = null,
    val items: List<CategoryVO>
)

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun TallyCategoryItemView(
    vo: CategoryGroupVO,
    cateGroupItemLongClick: () -> Unit = {},
    cateAddClick: () -> Unit = {},
    cateItemClick: (target: CategoryVO) -> Unit = {},
    isExpand: Boolean = false,
) {
    var isExpand by rememberSaveable {
        mutableStateOf(isExpand)
    }
    Surface {
        Column {
            Surface(onClick = { isExpand = !isExpand }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onLongClick = {
                                cateGroupItemLongClick.invoke()
                            }
                        ) {
                            isExpand = !isExpand
                        }
                        .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.res_arrow_bottom2),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(if (isExpand) 0f else -90f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Image(
                        painter = painterResource(id = vo.iconRsd),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = vo.titleName ?: stringResource(id = vo.titleNameRsd!!),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            }
            AnimatedVisibility(visible = isExpand) {
                GridView(
                    items = vo.items,
                    columnNumber = 4,
                    lastItemContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .nothing(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(size = 32.dp)
                                    .clickable {
                                        cateAddClick.invoke()
                                    }
                                    .nothing(),
                                painter = painterResource(id = R.drawable.res_add3),
                                contentDescription = null
                            )
                        }
                    }
                ) { item ->
                    Column(
                        modifier = Modifier
                            .clickable {
                                cateItemClick.invoke(item)
                            }
                            .nothing(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(id = item.iconRsd),
                            contentDescription = null,
                            modifier = Modifier
                                .size(width = 18.dp, height = 30.dp)
                                .padding(top = 12.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(
                            text = item.name
                                ?: stringResource(id = item.nameRsd!!),
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(bottom = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun TallyCategoryListView(
    categoryGroups: List<CategoryGroupVO>,
    cateGroupItemLongClick: (cateGroupId: String) -> Unit = {},
    cateAddClick: (cateGroupId: String) -> Unit = {},
    cateItemClick: (target: CategoryVO) -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 16.dp)
            .verticalScroll(
                state = rememberScrollState()
            )
    ) {
        categoryGroups.forEach { item ->
            TallyCategoryItemView(
                vo = item,
                cateGroupItemLongClick = {
                    cateGroupItemLongClick.invoke(item.cateGroupId)
                },
                cateAddClick = {
                    cateAddClick.invoke(item.cateGroupId)
                },
                cateItemClick = cateItemClick,
                isExpand = false,
            )
        }
    }
}

@Composable
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun tallyCategoryItemViewPreview() {
    TallyCategoryItemView(
        vo = CategoryGroupVO(
            cateGroupId = INVALID_STRING,
            iconRsd = R.drawable.res_home1,
            titleName = "测试标题",
            items = (0..4)
                .mapIndexed { index, _ ->
                    CategoryVO(
                        categoryId = INVALID_STRING,
                        name = "测试$index",
                        iconRsd = R.drawable.res_home1_selected
                    )
                },
        ),
        isExpand = true
    )
}