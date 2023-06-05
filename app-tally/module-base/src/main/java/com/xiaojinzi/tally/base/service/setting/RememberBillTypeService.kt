package com.xiaojinzi.tally.base.service.setting


import androidx.annotation.Keep
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO

interface RememberBillTypeService {

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val autoInferType: MutableSharedStateFlow<Boolean>


    suspend fun updateRecordBillType(categoryId: String, time: Long,type:Int): TallyBillStatDTO

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    suspend fun getRecordByTime(long: Long = System.currentTimeMillis(),type:Int): TallyCategoryDTO?
}

@Keep
data class TallyBillStatDTO(
    val id: Long = 0,
    val categoryId: String,
    var time: Long,
    var useCount: Long,
    var type:Int,
) {
    override fun toString(): String {
        return "TallyBillStatDTO(id=$id, categoryId='$categoryId', time=$time, useCount=$useCount, type=$type)"
    }
}

