package com.xiaojinzi.module.bugly

import com.tencent.bugly.crashreport.CrashReport
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.BuglyService
import com.xiaojinzi.support.ktx.app

@ServiceAnno(BuglyService::class)
class BuglyServiceImpl: BuglyService {

    override suspend fun init(appKey: String, isDebugMode: Boolean) {
        CrashReport.initCrashReport(app, appKey, isDebugMode, )
    }

    override suspend fun testJavaCrash() {
        CrashReport.testJavaCrash()
    }

}