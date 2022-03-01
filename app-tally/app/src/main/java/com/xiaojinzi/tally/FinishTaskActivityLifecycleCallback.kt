package com.xiaojinzi.tally

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.xiaojinzi.component.ComponentActivityStack
import com.xiaojinzi.module.base.support.finishAppAllTask
import com.xiaojinzi.support.ktx.AppScope
import kotlinx.coroutines.launch

/**
 * 如果最后一个界面是正常销毁, 那么就销毁所有 ActivityTask
 */
class FinishTaskActivityLifecycleCallback: Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        AppScope.launch {
            if (ComponentActivityStack.isEmpty) {
                if (!activity.isChangingConfigurations) {
                    finishAppAllTask()
                }
            }
        }
    }

}