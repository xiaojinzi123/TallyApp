package com.xiaojinzi.tally.home.module.label.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.service.datasource.TallyLabelService
import com.xiaojinzi.tally.home.R

@Composable
fun LabelCreateView() {
    val vm: LabelCreateViewModel = viewModel()
    val context = LocalContext.current
    val colorSelectIndex: Int by vm.colorSelectIndexObservableDTO.collectAsState(initial = 0)
    val text: StringItemDTO by vm.nameObservableDTO.collectAsState(initial = "".toStringItemDTO())
    Column {
        AppbarNormal(
            titleRsd = R.string.res_str_new_tag,
            menu1IconRsd = R.drawable.res_right1,
            menu1ClickListener = {
                vm.addOrUpdate(context = context)
            }
        )
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Column(
                modifier = Modifier.padding(all = 16.dp)
            ) {

                val focusRequester = remember { FocusRequester() }

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(
                            focusRequester = focusRequester
                        ),
                    value = text.nameAdapter(),
                    onValueChange = { vm.nameObservableDTO.value = it.trim().toStringItemDTO() },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.res_str_please_input_label_name),
                            style = MaterialTheme.typography.body2
                        )
                    },
                    trailingIcon = {
                        if (text.isEmpty) {
                            IconButton(onClick = {
                                vm.nameObservableDTO.value = "".toStringItemDTO()
                            }) {
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
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions.Default,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

            }
        }
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FlowRow(
                modifier = Modifier.padding(all = 16.dp),
                mainAxisSpacing = 12.dp,
                crossAxisSpacing = 12.dp,
            ) {
                TallyLabelService
                    .labelInnerColorList
                    .forEachIndexed { index, color ->
                        Column(
                            modifier = Modifier
                                .nothing(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(size = 36.dp)
                                    .clip(shape = CircleShape)
                                    .clickable {
                                        vm.colorSelectIndexObservableDTO.value = index
                                    }
                                    .background(
                                        color = color
                                    )
                                    .nothing()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(size = 4.dp)
                                    .clip(shape = CircleShape)
                                    .background(
                                        color = if (index == colorSelectIndex) Color.Red else Color.Transparent
                                    )
                                    .nothing()
                            )
                        }
                    }
            }
        }
    }

}

@Composable
fun LabelCreateViewWrap() {
    LabelCreateView()
}

@Preview(locale = "zh")
@Composable
fun LabelCreateViewPreview() {
    LabelCreateView()
}