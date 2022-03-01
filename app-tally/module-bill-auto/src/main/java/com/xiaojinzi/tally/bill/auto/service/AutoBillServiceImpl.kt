package com.xiaojinzi.tally.bill.auto.service

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.tally.base.service.bill.AutoBillService

@ServiceAnno(AutoBillService::class)
class AutoBillServiceImpl: AutoBillService {

    override val canDrawOverlays: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(app)
        } else {
            true
        }

    override val isOpenAutoBill: Boolean
        get() {
            val am = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val componentName = ComponentName(
                app.packageName,
                "com.xiaojinzi.tally.bill.auto.BillCreateAccessibilityService"
            )
            val targetIntent: PendingIntent? = am.getRunningServiceControlPanel(componentName)
            return targetIntent != null && canDrawOverlays
        }

}