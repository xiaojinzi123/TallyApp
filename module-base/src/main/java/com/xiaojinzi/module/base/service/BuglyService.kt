package com.xiaojinzi.module.base.service

interface BuglyService {

    /**
     * 初始化
     */
    suspend fun init(appKey: String, isDebugMode: Boolean)

    /**
     * 测试崩溃
     */
    suspend fun testJavaCrash()

}