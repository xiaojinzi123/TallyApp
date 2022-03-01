package com.xiaojinzi.module.develop

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged
import com.xiaojinzi.module.base.support.DevelopHelper
import com.xiaojinzi.module.base.support.anno.DevelopToolsVisible
import com.xiaojinzi.module.develop.view.DevelopToolView
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.dpInt

@ModuleAppAnno
class ModuleApplication: IApplicationLifecycle, IModuleNotifyChanged {

    val unSupportClassList = setOf(
        "com.google.android.gms.ads.AdActivity"
    )

    private var activityLifecycleCallback: Application.ActivityLifecycleCallbacks = object :
        Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(
            activity: Activity,
            savedInstanceState: Bundle?
        ) {
            if (activity.javaClass.name in unSupportClassList) {
                return
            }
            val developToolsVisibleAnno = activity.javaClass.getAnnotation(DevelopToolsVisible::class.java)
            if (developToolsVisibleAnno == null || developToolsVisibleAnno.value) {
                // 拿到顶层的 View
                val decorViewGroup = activity.window.decorView as ViewGroup
                // 判断类型然后添加一个 DevelopView
                if (decorViewGroup is FrameLayout) {
                    val layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        layoutParams.marginEnd = 10f.dpInt
                    } else {
                        layoutParams.rightMargin = 10f.dpInt
                    }
                    layoutParams.topMargin = 10f.dpInt
                    var developView = DevelopToolView(activity)
                    developView.layoutParams = layoutParams
                    decorViewGroup.addView(developView)
                }
            }
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle
        ) {
        }

        override fun onActivityDestroyed(activity: Activity) {}
    }

    override
    fun onCreate(application: Application) {
        if (DevelopHelper.isDevelop) {
            app.registerActivityLifecycleCallbacks(activityLifecycleCallback)
        }
    }

    override
    fun onDestroy() {
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    override fun onModuleChanged(app: Application) {
    }

}