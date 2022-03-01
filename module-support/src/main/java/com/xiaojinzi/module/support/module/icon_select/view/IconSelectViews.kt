package com.xiaojinzi.module.support.module.icon_select.view

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.support.ResData
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.base.view.compose.GridView
import com.xiaojinzi.module.support.R
import com.xiaojinzi.support.ktx.getActivity
import com.xiaojinzi.support.ktx.nothing

@ExperimentalFoundationApi
@Composable
fun IconSelectViewWrap() {
    val context = LocalContext.current
    val iconList = ResData.resIconCategoryList
    Scaffold(
        topBar = {
            AppbarNormal(titleRsd = R.string.res_str_icon_select)
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = 0.dp, vertical = 10.dp)
                .nothing(),
        ) {
            iconList.forEach { groupItem ->
                val name = groupItem.first
                val iconList = groupItem.second
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .nothing(),
                    text = name.nameAdapter(),
                    style = MaterialTheme.typography.subtitle1,
                )
                GridView(items = iconList, columnNumber = 6) { rsdId ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.getActivity()?.let { act ->
                                    act.setResult(
                                        Activity.RESULT_OK,
                                        Intent().apply {
                                            this.putExtra(
                                                "rsdIndex",
                                                ResData.resIconIndexList.indexOf(rsdId)
                                            )
                                        }
                                    )
                                    act.finish()
                                }
                            }
                            .padding(vertical = 16.dp)
                            .nothing(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(size = 24.dp),
                            painter = painterResource(id = rsdId),
                            contentDescription = null
                        )
                    }
                }
            }
        }

    }

}