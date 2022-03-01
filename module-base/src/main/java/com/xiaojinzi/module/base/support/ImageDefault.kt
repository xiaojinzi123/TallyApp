package com.xiaojinzi.module.base.support

import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import com.xiaojinzi.module.base.R
import com.xiaojinzi.support.ktx.app

object ImageDefault {

    private val DefaultImage = ColorDrawable(ContextCompat.getColor(app, R.color.res_image_default1))
    val PlaceholderImage = DefaultImage
    val ErrorImage = DefaultImage
    val FallbackImage = DefaultImage

}