package com.xiaojinzi.tally.base.service.datasource

import androidx.annotation.Keep

@Keep
data class TallyImageInsertDTO(
    val key1: String? = null,
    val url: String,
)

@Keep
data class TallyImageDTO(
    val uid: String,
    val key1: String?,
    val url: String,
)

interface TallyImageService {

    /**
     * 插入一个记录
     */
    suspend fun insert(target: TallyImageInsertDTO): TallyImageDTO

    /**
     * 插入多条记录
     */
    suspend fun insertList(targetList: List<TallyImageInsertDTO>): List<TallyImageDTO>

    /**
     * 根据 key1 删除
     */
    suspend fun deleteByKey1(key1: String)

    /**
     * 根据 key1 获取
     */
    suspend fun getListByKey1(key1: String): List<TallyImageDTO>

}