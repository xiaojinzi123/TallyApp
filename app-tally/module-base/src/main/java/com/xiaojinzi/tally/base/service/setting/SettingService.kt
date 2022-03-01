package com.xiaojinzi.tally.base.service.setting

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.dbPersistence
import com.xiaojinzi.module.base.support.dbPersistenceNonNull
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.tally.base.support.autoBillSettingService
import com.xiaojinzi.tally.base.support.billSettingService
import com.xiaojinzi.tally.base.support.statisticalSettingService
import com.xiaojinzi.tally.base.support.systemSettingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 设置相关的 Service
 */
interface SettingService : SystemSettingService, StatisticalSettingService, BillSettingService,
    AutoBillSettingService {

    val testObservableDTO: Flow<String?>

}

@ServiceAnno(SettingService::class, autoInit = true)
class SettingServiceImpl : SettingService,
    SystemSettingService by systemSettingService,
    StatisticalSettingService by statisticalSettingService,
    BillSettingService by billSettingService,
    AutoBillSettingService by autoBillSettingService {

    override val testObservableDTO =
        MutableSharedStateFlow<String?>(initValue = null).dbPersistence(key = "test", def = "111")

    val testObservableDTO1 =
        MutableSharedStateFlow<String>(initValue = "").dbPersistenceNonNull(
            key = "test",
            def = "111"
        )

    init {
        testObservableDTO
            .onEach {
                println("testObservableDTO value = $it")
            }
            .launchIn(scope = AppScope)
    }

}