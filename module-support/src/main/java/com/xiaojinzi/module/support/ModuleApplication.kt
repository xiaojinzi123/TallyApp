package com.xiaojinzi.module.support

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged

@ModuleAppAnno
class ModuleApplication : IApplicationLifecycle, IModuleNotifyChanged {

    override fun onCreate(app: Application) {
    }

    override fun onDestroy() {
    }

    override fun onModuleChanged(app: Application) {

    }


}