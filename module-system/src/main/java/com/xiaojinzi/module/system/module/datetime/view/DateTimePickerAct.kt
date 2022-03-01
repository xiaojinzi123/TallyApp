package com.xiaojinzi.module.system.module.datetime.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.support.ParameterSupport
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.interceptor.AlphaInAnimInterceptor
import com.xiaojinzi.module.base.theme.CommonTheme
import com.xiaojinzi.module.base.view.BaseActivity
import com.xiaojinzi.module.base.view.compose.BottomView
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.module.base.view.compose.date.DateTimeSelectView
import com.xiaojinzi.module.base.view.compose.date.SelectType
import com.xiaojinzi.module.base.view.compose.date.rememberDateTimeWheelState
import com.xiaojinzi.module.system.R
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.ktx.nothing
import kotlinx.coroutines.flow.collect
import java.util.*

@RouterAnno(
    hostAndPath = RouterConfig.SYSTEM_DATE_TIME_PICKER,
    interceptors = [AlphaInAnimInterceptor::class]
)
class DateTimePickerAct : BaseActivity<BaseViewModel>() {

    private val calendar by lazy {
        val targetDateTime: Long =
            ParameterSupport.getLong(intent, "dateTime") ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = targetDateTime
        calendar
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CommonTheme {
                StateBar {
                    var isSelectDate by remember { mutableStateOf(value = true) }
                    val dateTimeTimeState =
                        rememberDateTimeWheelState(initTime = calendar.timeInMillis)
                    LaunchedEffect(key1 = dateTimeTimeState) {
                        snapshotFlow { dateTimeTimeState.currentTime }
                            .collect {
                                calendar.timeInMillis = it
                            }
                    }
                    BottomView {
                        Column(
                            modifier = Modifier
                                .clickable(enabled = false) {

                                }
                                .background(color = MaterialTheme.colors.surface)
                                .nothing(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(
                                        color = Color.LightGray
                                    )
                                    .padding(horizontal = 0.dp, vertical = 6.dp)
                                    .nothing(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            finish()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .nothing(),
                                    text = stringResource(id = R.string.res_str_cancel),
                                    style = MaterialTheme.typography.subtitle1,
                                )
                                Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            isSelectDate = !isSelectDate
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .nothing(),
                                    text = stringResource(id = R.string.res_str_switch_date_and_time),
                                    style = MaterialTheme.typography.subtitle1,
                                )
                                Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            setResult(
                                                Activity.RESULT_OK,
                                                Intent().apply {
                                                    this.putExtra("dateTime", calendar.timeInMillis)
                                                }
                                            )
                                            finish()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .nothing(),
                                    text = stringResource(id = R.string.res_str_confirm),
                                    style = MaterialTheme.typography.subtitle1,
                                )
                            }
                            DateTimeSelectView(
                                dateTimeTimeState = dateTimeTimeState,
                                selectType = if (isSelectDate) SelectType.Date else SelectType.Time,
                            )
                        }

                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.alpha_out)
    }

}