package com.xiaojinzi.module.base.service

import android.os.Build
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.base.support.appInfoAdapterService
import com.xiaojinzi.module.base.support.appInfoService
import com.xiaojinzi.support.ktx.app

interface AppInfoService: AppInfoAdapterService {

    /**
     * 类似 1.0.0 这种
     */
    val appVersionName: String

    /**
     * App 版本号
     */
    val appVersionCode: Long

}

/**
 * 默认实现
 */
@ServiceAnno(AppInfoService::class)
class AppInfoServiceImpl : AppInfoService, AppInfoAdapterService by appInfoAdapterService {

    override val appVersionName: String = try {
        app
            .packageManager
            .getPackageInfo(app.packageName, 0).versionName
    } catch (e: Exception) {
        "UnKnow"
    }

    override val appVersionCode: Long = try {
        app
            .packageManager
            .getPackageInfo(app.packageName, 0).run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    this.longVersionCode
                } else {
                    this.versionCode.toLong()
                }
            }
    } catch (e: Exception) {
        0
    }

}