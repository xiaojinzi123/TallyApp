package com.xiaojinzi.tally.home.module.note.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.base.view.compose.BottomView
import com.xiaojinzi.module.base.view.compose.CommonEditText
import com.xiaojinzi.support.ktx.getFragmentActivity
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.home.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun NoteViewWrap() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vm: NoteViewModel = viewModel()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val noteStr by vm.noteObservableDTO.collectAsState(initial = "")
    val noteHistoryList by vm.noteHistoryListObservableDTO.collectAsState(initial = emptyList())
    BottomView(
        maxFraction = 0.8f
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f, fill = true)
                .verticalScroll(state = rememberScrollState())
                // .wrapContentHeight()
                .nothing(),
        ) {
            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    /*.weight(weight = 1f, fill = true)
                    .verticalScroll(state = rememberScrollState())*/
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .nothing(),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 6.dp,
            ) {
                noteHistoryList.forEach { historyNote ->
                    Text(
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.small)
                            .combinedClickable(
                                onLongClick = {
                                    MaterialAlertDialogBuilder(context)
                                        .setMessage(com.xiaojinzi.tally.base.R.string.res_str_is_confirm_to_delete)
                                        .setNegativeButton(com.xiaojinzi.tally.base.R.string.res_str_cancel) { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .setPositiveButton(com.xiaojinzi.tally.base.R.string.res_str_ensure) { dialog, _ ->
                                            dialog.dismiss()
                                            scope.launch {
                                                vm.deleteNoteHistory(value = historyNote)
                                            }
                                        }
                                        .show()
                                }
                            ) {
                                vm.noteObservableDTO.value = historyNote.trim()
                                vm.returnData(context = context)
                            }
                            .background(color = Color.White)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .nothing(),
                        text = historyNote,
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(height = 10.dp))
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .navigationBarsWithImePadding()
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .wrapContentHeight()
                    // .requiredHeightIn(min = 100.dp, max = Dp.Unspecified)
                    .focusRequester(
                        focusRequester = focusRequester
                    )
                    .nothing(),
                value = TextFieldValue(
                    text = noteStr,
                    selection = TextRange(index = noteStr.length),
                ),
                onValueChange = {
                    vm.noteObservableDTO.value = it.text.trim()
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.res_str_please_fill_in_the_note))
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        vm.returnData(context = context)
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Text(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.small)
                    .clickable {
                        vm.returnData(context = context)
                    }
                    .background(color = MaterialTheme.colors.primary)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .nothing(),
                text = stringResource(id = R.string.res_str_confirm),
                style = MaterialTheme.typography.subtitle1.copy(
                    color = MaterialTheme.colors.onPrimary,
                ),
            )
            Spacer(modifier = Modifier.width(width = 16.dp))
        }
        DisposableEffect(key1 = Unit) {
            scope.launch {
                delay(200)
                focusRequester.requestFocus()
                keyboardController?.show()
            }
            onDispose { }
        }

    }
}