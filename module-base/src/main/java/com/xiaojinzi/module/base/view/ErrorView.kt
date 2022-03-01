package com.xiaojinzi.module.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.xiaojinzi.module.base.databinding.BaseErrorViewBinding
import com.xiaojinzi.module.base.support.Apps
import com.xiaojinzi.support.ktx.inflater

class ErrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var retryListener: ((view: View) -> Unit)? = null

    init {
        val viewBinding = BaseErrorViewBinding.inflate(context.inflater)
        viewBinding.tvRetry.setOnClickListener {
            retryListener?.invoke(it)
        }
        viewBinding.tvTryUpdate.setOnClickListener {
            Apps.toMarketIgnoreException()
        }

        addView(
            viewBinding.root,
            generateDefaultLayoutParams().apply {
                this.width = ViewGroup.LayoutParams.MATCH_PARENT
                this.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        )
    }

}