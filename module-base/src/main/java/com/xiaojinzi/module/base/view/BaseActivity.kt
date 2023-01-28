package com.xiaojinzi.module.base.view

import android.os.Bundle
import android.widget.Toast
import com.xiaojinzi.component.Component
import com.xiaojinzi.module.base.R
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.architecture.mvvm1.TipBean
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.contentWithContext

open class BaseActivity<VM : BaseViewModel> : BaseAct<VM>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Component.inject(this)
    }

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(mContext)
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.none, R.anim.bottom_out)
    }

}