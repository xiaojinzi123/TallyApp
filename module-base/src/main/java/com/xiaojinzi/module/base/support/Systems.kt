package com.xiaojinzi.module.base.support

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import com.xiaojinzi.support.ktx.app

fun shake() {
  shake(40L)
}

fun shakeStrong() {
  shake(40L, 140)
}

fun shake(milliseconds: Long, amplitude: Int  = VibrationEffect.DEFAULT_AMPLITUDE) {
  val vib: Vibrator? = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    vib?.vibrate(
        VibrationEffect.createOneShot(
            milliseconds, amplitude
        ),
        null
    )
  } else {
    vib?.vibrate(milliseconds)
  }
}