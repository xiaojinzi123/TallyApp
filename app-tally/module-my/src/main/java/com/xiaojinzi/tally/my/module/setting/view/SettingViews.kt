package com.xiaojinzi.tally.my.module.setting.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.commonRouteResultService
import com.xiaojinzi.module.base.support.flow.toggle
import com.xiaojinzi.module.base.support.restartApp
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.setting.LanguageSettingDTO
import com.xiaojinzi.tally.base.support.settingService
import com.xiaojinzi.tally.base.theme.body3
import com.xiaojinzi.tally.base.view.CommonActionItemView
import com.xiaojinzi.tally.base.view.CommonSwitchItemView
import com.xiaojinzi.tally.my.R
import kotlinx.coroutines.launch

@Composable
private fun SettingItemView(
    contentItem: StringItemDTO,
    descItem: StringItemDTO? = null,
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
                .padding(horizontal = 0.dp, vertical = 12.dp)
                .nothing(),
        ) {
            Text(
                text = contentItem.nameAdapter(),
                style = MaterialTheme.typography.subtitle1,
            )
            if (descItem != null) {
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
        Image(
            modifier = Modifier
                .size(size = 24.dp),
            painter = painterResource(id = R.drawable.res_arrow_right1),
            contentDescription = null
        )

    }
}

@Composable
private fun SettingView() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isVibrateDuringInput by settingService.vibrateDuringInputObservableDTO.collectAsState(
        initial = false
    )
    val language by settingService.languageObservableDTO.collectAsState(
        initial = LanguageSettingDTO.FollowSystem
    )

    val inferType by settingService.autoInferType.collectAsState(
        initial = true
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .nothing(),
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .nothing(),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .nothing(),
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_general),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.Gray
                    ),
                )
                Spacer(modifier = Modifier.height(height = 4.dp))
                CommonActionItemView(
                    contentNameItem = R.string.res_str_switch_language.toStringItemDTO(),
                    contentValueItem = language.nameItem,
                ) {
                    scope.launch(ErrorIgnoreContext) {
                        val menuList = listOf(
                            LanguageSettingDTO.FollowSystem,
                            LanguageSettingDTO.Chinese,
                            LanguageSettingDTO.English,
                        )
                        val index = commonRouteResultService.menuSelect(
                            context = context,
                            items = menuList.map { it.nameItem },
                        )
                        settingService.languageObservableDTO.value = menuList[index]
                        // 重启 App
                        restartApp()
                    }
                }
                Spacer(modifier = Modifier.height(height = 4.dp))
                CommonSwitchItemView(
                    contentItem = StringItemDTO(nameRsd = R.string.res_str_vibrate_during_input),
                    descItem = StringItemDTO(nameRsd = R.string.res_str_tip3),
                    checked = isVibrateDuringInput
                ) {
                    settingService.vibrateDuringInputObservableDTO.toggle()
                }
                CommonSwitchItemView(
                    contentItem = StringItemDTO(nameRsd = R.string.res_str_infer_bill_type),
                    descItem = StringItemDTO(nameRsd = R.string.res_str_infer_bill_type_tips),
                    checked = inferType
                ) {
                    settingService.autoInferType.toggle()
                }
            }
        }

        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .nothing(),
        ) {
            val isShowImageInBillList by settingService.isShowImageInBillListObservableDTO.collectAsState(
                initial = false
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .nothing(),
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_bill_list),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.Gray
                    ),
                )
                Spacer(modifier = Modifier.height(height = 4.dp))
                CommonSwitchItemView(
                    contentItem = StringItemDTO(nameRsd = R.string.res_str_show_image_in_bill_list),
                    checked = isShowImageInBillList
                ) {
                    settingService.isShowImageInBillListObservableDTO.toggle()
                }
            }
        }

        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .nothing(),
        ) {
            val isChecked1 by settingService
                .cateCostProgressPercentColorIsFollowPieChartObservableDTO
                .collectAsState(
                    initial = false
                )
            val isChecked2 by settingService
                .cateCostProgressPercentOptimizeObservableDTO
                .collectAsState(
                    initial = false
                )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .nothing(),
            ) {
                Text(
                    text = stringResource(id = R.string.res_str_statistical),
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.Gray
                    ),
                )
                Spacer(modifier = Modifier.height(height = 4.dp))
                CommonSwitchItemView(
                    contentItem = StringItemDTO(nameRsd = R.string.res_str_desc1),
                    descItem = StringItemDTO(nameRsd = R.string.res_str_tip1),
                    checked = isChecked1
                ) {
                    settingService.cateCostProgressPercentColorIsFollowPieChartObservableDTO.toggle()
                }
                Spacer(modifier = Modifier.height(height = 4.dp))
                CommonSwitchItemView(
                    contentItem = StringItemDTO(nameRsd = R.string.res_str_desc2),
                    descItem = StringItemDTO(nameRsd = R.string.res_str_tip1),
                    checked = isChecked2
                ) {
                    settingService.cateCostProgressPercentOptimizeObservableDTO.toggle()
                }
            }
        }

        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .nothing(),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .nothing(),
            ) {
                SettingItemView(
                    contentItem = StringItemDTO(nameRsd = R.string.res_str_about),
                ) {
                    Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_ABOUT)
                        .forward()
                }
            }
        }


    }
}

@Composable
fun SettingViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_setting
            )
        }
    ) {
        SettingView()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingViewPreview() {
    SettingView()
}