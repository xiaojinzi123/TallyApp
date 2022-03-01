package com.xiaojinzi.module.develop.view

import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.service.develop.DevelopService
import com.xiaojinzi.module.base.support.DevelopHelper
import com.xiaojinzi.module.base.support.developService
import com.xiaojinzi.module.develop.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class DevelopToolView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context!!, attrs, defStyleAttr) {

    private val disposables = CompositeDisposable()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBind()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposables.clear()
    }

    init {
        inflate(context, R.layout.develop_tools_view, this)
        setOnClickListener {
            Router.with(getContext())
                .hostAndPath(RouterConfig.COMMON_DEVELOP_MAIN)
                .forward()
        }
        setOnLongClickListener {
            AlertDialog.Builder(getContext())
                .setTitle("tip")
                .setMessage("是否关闭开发者工具?")
                .setPositiveButton("ok") { _: DialogInterface?, _: Int ->
                    ServiceManager.requiredGet(DevelopService::class.java)
                        .toggleValue(DevelopService.SP_KEY_IS_DEVELOP_VIEW_VISIBLE)
                }
                .create()
                .show()
            true
        }

    }

    private fun onBind() {
        developService?.let { targetService ->
            targetService.subscribeBehaviorBoolValue(DevelopService.SP_KEY_IS_DEVELOP_VIEW_VISIBLE)
                .map { it && DevelopHelper.isDevelop }
                .subscribeBy { b: Boolean -> visibility = if (b) VISIBLE else GONE }
                .addTo(disposables)
        }

    }

}