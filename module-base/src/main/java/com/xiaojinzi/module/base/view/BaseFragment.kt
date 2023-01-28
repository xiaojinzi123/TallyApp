package com.xiaojinzi.module.base.view

import android.widget.Toast
import androidx.annotation.LayoutRes
import com.xiaojinzi.support.architecture.mvvm1.BaseFrag
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.architecture.mvvm1.TipBean
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.contentWithContext

open class
BaseFragment<VM : BaseViewModel>(
    @LayoutRes contentLayoutId: Int = 0
) : BaseFrag<VM>(contentLayoutId = contentLayoutId) {

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    override fun onTip(content: TipBean) {
        Toast.makeText(app, content.content.contentWithContext(), Toast.LENGTH_SHORT).show()
    }

    override fun showLoading(isShow: Boolean) {
        if (isShow) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

}