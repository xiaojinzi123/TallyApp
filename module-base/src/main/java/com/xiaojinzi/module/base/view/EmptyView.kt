package com.xiaojinzi.module.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.xiaojinzi.module.base.databinding.BaseEmptyViewBinding
import com.xiaojinzi.module.base.support.Apps
import com.xiaojinzi.support.ktx.inflater

class EmptyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        /*context.inflateLayout(
            layoutId = R.layout.base_empty_view,
            parent = this,
            attachToRoot = true
        )*/
        val viewBinding = BaseEmptyViewBinding.inflate(context.inflater)
        viewBinding.tvEmpty.setOnClickListener {
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