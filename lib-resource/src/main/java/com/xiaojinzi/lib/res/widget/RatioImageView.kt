package com.xiaojinzi.lib.res.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min
import kotlin.properties.Delegates

class RatioImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    /**
     * 宽高比
     */
    var ratio by Delegates.observable(1f) { _, _, newValue ->
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val targetHeight = when(MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> (widthSize / ratio).toInt()
            else -> min((widthSize / ratio).toInt(), heightSize)
        }
        val targetWidth = min(targetHeight * ratio, widthSize.toFloat()).toInt()
        val targetWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
            targetWidth,
            MeasureSpec.EXACTLY
        )
        val targetHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            targetHeight,
            MeasureSpec.EXACTLY
        )
        super.onMeasure(targetWidthMeasureSpec, targetHeightMeasureSpec)
    }

}