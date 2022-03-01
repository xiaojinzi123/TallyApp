package com.xiaojinzi.tally.statistical

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ModuleAppAnno
class ModuleApplication : IApplicationLifecycle, IModuleNotifyChanged {

    override fun onCreate(app: Application) {
    }

    override fun onDestroy() {
    }

    override fun onModuleChanged(app: Application) {
    }


}