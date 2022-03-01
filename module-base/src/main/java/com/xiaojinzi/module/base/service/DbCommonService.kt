package com.xiaojinzi.module.base.service

/**
 * 一个简单存储的数据库实现.
 * 方便存一些比较大的数据. 如果数据是很简单的基础类型.
 * 可以参考 [SPService]
 */
interface DbCommonService {

    suspend fun saveString(key: String, value: String?)
    suspend fun saveInt(key: String, value: Int?)
    suspend fun saveLong(key: String, value: Long?)
    suspend fun saveFloat(key: String, value: Float?)
    suspend fun saveDouble(key: String, value: Double?)
    suspend fun saveBoolean(key: String, value: Boolean?)

    suspend fun getString(key: String): String?
    suspend fun getInt(key: String): Int?
    suspend fun getLong(key: String): Long?
    suspend fun getFloat(key: String): Float?
    suspend fun getDouble(key: String): Double?
    suspend fun getBoolean(key: String): Boolean?

}