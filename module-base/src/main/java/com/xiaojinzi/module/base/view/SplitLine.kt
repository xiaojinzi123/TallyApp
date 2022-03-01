package com.xiaojinzi.module.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.xiaojinzi.module.base.R

class SplitLine @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /*override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(
                1.dp,
                MeasureSpec.EXACTLY
            )
        )
    }*/

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.res_split_line))
    }

}