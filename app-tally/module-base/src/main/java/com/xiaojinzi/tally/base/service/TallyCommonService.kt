package com.xiaojinzi.tally.base.service

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.annotation.UiContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.tally.base.R
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Keep
enum class DialogConfirmResult {
    Cancel, Negative, Positive,
}

interface TallyCommonService {

    suspend fun confirmDialog(
        @UiContext context: Context,
        message: StringItemDTO,
        @StringRes negativeRsd: Int = R.string.res_str_cancel,
        @StringRes positiveRsd: Int = R.string.res_str_confirm,
    ): DialogConfirmResult

}

@ServiceAnno(TallyCommonService::class)
class TallyCommonServiceImpl : TallyCommonService {

    override suspend fun confirmDialog(
        context: Context,
        message: StringItemDTO,
        negativeRsd: Int,
        positiveRsd: Int,
    ): DialogConfirmResult {
        return suspendCoroutine { cot ->
            MaterialAlertDialogBuilder(context)
                .apply {
                    message.nameRsd?.let {
                        this.setMessage(it)
                    }
                }
                .apply {
                    message.name?.let {
                        this.setMessage(it)
                    }
                }
                .setOnCancelListener {
                    cot.resume(value = DialogConfirmResult.Cancel)
                }
                .setNegativeButton(negativeRsd) { dialog, _ ->
                    cot.resume(value = DialogConfirmResult.Negative)
                    dialog.dismiss()
                }
                .setPositiveButton(positiveRsd) { dialog, _ ->
                    cot.resume(value = DialogConfirmResult.Positive)
                    dialog.dismiss()
                }
                .show()
        }
    }

}