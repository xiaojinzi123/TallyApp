package com.xiaojinzi.module.base.support

import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.base.service.*
import com.xiaojinzi.module.base.service.develop.DevelopDateTimeFieldService
import com.xiaojinzi.module.base.service.develop.DevelopFunctionService
import com.xiaojinzi.module.base.service.develop.DevelopService

val appInfoService: AppInfoService
    get() = ServiceManager.requiredGet(AppInfoService::class.java)
val buglyService: BuglyService?
    get() = ServiceManager.get(BuglyService::class.java)
val appInfoAdapterService: AppInfoAdapterService
    get() = ServiceManager.requiredGet(AppInfoAdapterService::class.java)
val spService: SPService
    get() = ServiceManager.requiredGet(SPService::class.java)
val dbCommonService: DbCommonService
    get() = ServiceManager.requiredGet(DbCommonService::class.java)
val appThemeService: AppThemeService
    get() = ServiceManager.requiredGet(AppThemeService::class.java)
val systemShareService: SystemShareService
    get() = ServiceManager.requiredGet(SystemShareService::class.java)
val imageUploadService: ImageUploadService
    get() = ServiceManager.requiredGet(ImageUploadService::class.java)
val imageSelectService: ImageSelectService
    get() = ServiceManager.requiredGet(ImageSelectService::class.java)
val commonRouteResultService: CommonRouteResultService
    get() = ServiceManager.requiredGet(CommonRouteResultService::class.java)

// 开发者功能
val developService: DevelopService?
    get() = ServiceManager.get(DevelopService::class.java)
val developDateTimeFieldService: DevelopDateTimeFieldService
    get() = ServiceManager.requiredGet(DevelopDateTimeFieldService::class.java)
