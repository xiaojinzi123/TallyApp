package com.xiaojinzi.tally.base.service.setting

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.dbPersistenceNonNull
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.support.DBCommonKeys

interface BillSettingService {

    /**
     * 是否在账单列表中显示图片
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isShowImageInBillListObservableDTO: MutableSharedStateFlow<Boolean>

}

@ServiceAnno(BillSettingService::class)
class BillSettingServiceImpl: BillSettingService {

    override val isShowImageInBillListObservableDTO = MutableSharedStateFlow<Boolean>()
        .dbPersistenceNonNull(
            key = DBCommonKeys.isShowImageInBillList,
            def = true,
        )

}