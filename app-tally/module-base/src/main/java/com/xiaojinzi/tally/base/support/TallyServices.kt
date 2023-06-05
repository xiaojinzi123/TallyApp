package com.xiaojinzi.tally.base.support

import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.tally.base.service.*
import com.xiaojinzi.tally.base.service.statistical.TallyDataStatisticalService
import com.xiaojinzi.tally.base.service.bill.AutoBillService
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.service.setting.*

val tallyAppService: TallyAppService
    get() = ServiceManager.requiredGet(TallyAppService::class.java)
val tallyRouteResultService: TallyRouteResultService
    get() = ServiceManager.requiredGet(TallyRouteResultService::class.java)
val tallyImageService: TallyImageService
    get() = ServiceManager.requiredGet(TallyImageService::class.java)
val tallyAccountTypeService: TallyAccountTypeService
    get() = ServiceManager.requiredGet(TallyAccountTypeService::class.java)
val tallyAccountService: TallyAccountService
    get() = ServiceManager.requiredGet(TallyAccountService::class.java)
val tallyBookService: TallyBookService
    get() = ServiceManager.requiredGet(TallyBookService::class.java)
val tallyBillDetailQueryPagingService: TallyBillDetailQueryPagingService
    get() = ServiceManager.requiredGet(TallyBillDetailQueryPagingService::class.java)
val tallyBillService: TallyBillService
    get() = ServiceManager.requiredGet(TallyBillService::class.java)
val tallyCategoryService: TallyCategoryService
    get() = ServiceManager.requiredGet(TallyCategoryService::class.java)
val tallyService: TallyService
    get() = ServiceManager.requiredGet(TallyService::class.java)
val tallyLabelService: TallyLabelService
    get() = ServiceManager.requiredGet(TallyLabelService::class.java)
val tallyBillLabelService: TallyBillLabelService
    get() = ServiceManager.requiredGet(TallyBillLabelService::class.java)
val tallyBillAutoSourceAppService: TallyBillAutoSourceAppService
    get() = ServiceManager.requiredGet(TallyBillAutoSourceAppService::class.java)
val tallyBillAutoSourceViewService: TallyBillAutoSourceViewService
    get() = ServiceManager.requiredGet(TallyBillAutoSourceViewService::class.java)
val tallyDataStatisticalService: TallyDataStatisticalService
    get() = ServiceManager.requiredGet(TallyDataStatisticalService::class.java)
val tallyBudgetService: TallyBudgetService
    get() = ServiceManager.requiredGet(TallyBudgetService::class.java)
val tallyCommonService: TallyCommonService
    get() = ServiceManager.requiredGet(TallyCommonService::class.java)

val mainService: MainService
    get() = ServiceManager.requiredGet(MainService::class.java)
val statisticalService: StatisticalService
    get() = ServiceManager.requiredGet(StatisticalService::class.java)
val myService: MyService
    get() = ServiceManager.requiredGet(MyService::class.java)

// setting 相关
val billSettingService: BillSettingService
    get() = ServiceManager.requiredGet(BillSettingService::class.java)
val autoBillSettingService: AutoBillSettingService
    get() = ServiceManager.requiredGet(AutoBillSettingService::class.java)
val statisticalSettingService: StatisticalSettingService
    get() = ServiceManager.requiredGet(StatisticalSettingService::class.java)
val systemSettingService: SystemSettingService
    get() = ServiceManager.requiredGet(SystemSettingService::class.java)
val settingService: SettingService
    get() = ServiceManager.requiredGet(SettingService::class.java)

val autoBillService: AutoBillService?
    get() = ServiceManager.get(AutoBillService::class.java)

val rememberBillTypeService: RememberBillTypeService
    get() = ServiceManager.requiredGet(RememberBillTypeService::class.java)

