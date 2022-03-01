package com.xiaojinzi.tally.base.support

import android.content.Context
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import com.xiaojinzi.support.ktx.app
import kotlin.math.roundToLong

fun Long.tallyCostAdapter(): Float = this / 100f
fun Float.tallyCostToLong(): Long = (this * 100f).roundToLong()
fun Float.tallyNumberFormat1(): String = "%.2f".format(this)

@MainThread
fun tallyAppToast(context: Context = app, @StringRes contentRsd: Int, isShort: Boolean = true) {
    tallyAppToast(context = context, content = context.getString(contentRsd), isShort = isShort)
}

@MainThread
fun tallyAppToast(context: Context = app, content: String, isShort: Boolean = true) {
    Toast.makeText(
        context,
        content,
        if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}