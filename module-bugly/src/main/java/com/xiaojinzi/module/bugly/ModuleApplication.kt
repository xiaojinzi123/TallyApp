package com.xiaojinzi.module.bugly

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged
import com.xiaojinzi.support.ktx.AppInitSupport
import com.xiaojinzi.support.ktx.AppInitTask
import com.xiaojinzi.support.ktx.app

@ModuleAppAnno
class ModuleApplication: IApplicationLifecycle, IModuleNotifyChanged {

    override
    fun onCreate(app: Application) {
    }

    override
    fun onDestroy() {
    }

    override fun onModuleChanged(app: Application) {
    }

}