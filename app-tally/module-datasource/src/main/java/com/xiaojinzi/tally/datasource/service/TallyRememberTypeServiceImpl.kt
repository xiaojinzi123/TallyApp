package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.dbPersistenceNonNull
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO
import com.xiaojinzi.tally.base.service.setting.RememberBillTypeService
import com.xiaojinzi.tally.base.service.setting.TallyBillStatDTO
import com.xiaojinzi.tally.base.support.DBCommonKeys
import com.xiaojinzi.tally.datasource.data.toTallyBillStatDTO
import com.xiaojinzi.tally.datasource.data.toTallyCategoryDTO
import com.xiaojinzi.tally.datasource.db.TallyBillStatDO
import com.xiaojinzi.tally.datasource.db.dbTally


@ServiceAnno(RememberBillTypeService::class)
class TallyRememberTypeServiceImpl : RememberBillTypeService {

    override val autoInferType = MutableSharedStateFlow<Boolean>()
        .dbPersistenceNonNull(
            key = DBCommonKeys.autoInferBillType,
            def = true
        )

    override suspend fun updateRecordBillType(
        categoryId: String,
        time: Long,
        type: Int
    ): TallyBillStatDTO {
        var record = dbTally.tallBillStatDao().findCategoryRecord(categoryId, time, type)
        if (record == null) {
            record = TallyBillStatDO(0, categoryId, time, 1L, type)
            dbTally.tallBillStatDao().insert(record)
        } else {
            dbTally.tallBillStatDao().incrementCount(record.rid, type)
        }
        return record.toTallyBillStatDTO()
    }

    override suspend fun getRecordByTime(long: Long, type: Int): TallyCategoryDTO? {
        var result: TallyCategoryDTO? = null
        dbTally.tallBillStatDao().getCategoryList(long, type)?.apply {
            if (this.isNotEmpty()) {
                result = this[0].toTallyCategoryDTO()
            }
        }
        return result
    }

}