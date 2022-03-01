package com.xiaojinzi.tally.base.service.setting

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.dbPersistence
import com.xiaojinzi.module.base.support.dbPersistenceNonNull
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.support.DBCommonKeys
import kotlinx.coroutines.flow.Flow

interface AutoBillSettingService {

    /**
     * 是否提示去开启自动记账
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isTipToOpenAutoBillObservableDTO: MutableSharedStateFlow<Boolean>

}

@ServiceAnno(AutoBillSettingService::class)
class AutoBillSettingServiceImpl : AutoBillSettingService {

    override val isTipToOpenAutoBillObservableDTO = MutableSharedStateFlow<Boolean>()
        .dbPersistenceNonNull(
            key = DBCommonKeys.isTipToOpenAutoBill,
            def = false,
        )

}