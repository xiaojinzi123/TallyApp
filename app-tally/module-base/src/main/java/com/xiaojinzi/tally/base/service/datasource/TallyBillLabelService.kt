package com.xiaojinzi.tally.base.service.datasource

import androidx.annotation.Keep

@Keep
data class TallyBillLabelInsertDTO(
    val billId: String,
    val labelId: String,
)

@Keep
data class TallyBillLabelDTO(
    val uid: String,
    val billId: String,
    val labelId: String,
)

interface TallyBillLabelService {

    /**
     * 插入一个
     */
    suspend fun insert(target: TallyBillLabelInsertDTO): TallyBillLabelDTO

    /**
     * 插入数据, 如果目标账单的标签的记录没有
     */
    suspend fun insertIfNotExist(target: TallyBillLabelInsertDTO): TallyBillLabelDTO?

    /**
     * 插入多个
     */
    suspend fun insertList(target: List<TallyBillLabelInsertDTO>): List<TallyBillLabelDTO>

    /**
     * 根据账单的 ID 删除
     */
    suspend fun deleteByBillIds(ids: List<String>)

    /**
     * 根据标签的 ID 删除
     */
    suspend fun deleteByLabelIds(ids: List<String>)

    /**
     * 更新账单的标签列表
     */
    suspend fun updateLabelList(billId: String, labelIdList: List<String>)

    /**
     * 获取这些标签相关的账单 Id 集合
     */
    suspend fun getBillIdListByLabelIds(labelIds: List<String>): List<String>

}