package com.xiaojinzi.module.system.module.datetime.view

import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.xiaojinzi.module.base.view.compose.BottomView
import com.xiaojinzi.module.system.R
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.LogSupport

private const val TAG = "YearSelectViews"

@Composable
fun YearSelectViewWrap() {
    val context = LocalContext.current
    val vm: YearSelectViewModel = viewModel()
    val initYear by vm.yearInitData.valueStateFlow.collectAsState(initial = -1)
    val yearList by vm.yearListObservableDTO.collectAsState(initial = emptyList())
    BottomView {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colors.background
                )
                .nothing(),
        ) {
            if (yearList.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .nothing(),
                    factory = { context ->
                        NumberPicker(context)
                            .apply {
                                this.setOnValueChangedListener { _, nextValue, currValue ->
                                    LogSupport.d(
                                        tag = TAG,
                                        content = "ValueChanged: nextValue = $nextValue, currValue = $currValue"
                                    )
                                    vm.selectYearObservableDTO.value = currValue
                                }
                                // 不循环显示
                                this.wrapSelectorWheel = false
                                this.minValue = yearList.first()
                                this.maxValue = yearList.last()
                                if (initYear != -1) {
                                    this.value = initYear
                                }
                            }
                    }
                )
            }
            Button(
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(size = 0.dp)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min),
                onClick = {
                    vm.returnData(context = context)
                }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(vertical = 8.dp)
                        .nothing(),
                    text = stringResource(id = R.string.res_str_complete),
                    style = MaterialTheme.typography.button,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}