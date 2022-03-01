package com.xiaojinzi.module.base.view

import android.content.Context
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.xiaojinzi.module.base.databinding.BaseLoadingDialogBinding
import com.xiaojinzi.support.ktx.viewBindings

class LoadingDialog(context: Context) : AppCompatDialog(context) {

    private val viewBinding: BaseLoadingDialogBinding by context.viewBindings()

    init {
        setContentView(viewBinding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.attributes = window?.attributes?.apply {
            this.width = WindowManager.LayoutParams.WRAP_CONTENT
            this.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
    }

}