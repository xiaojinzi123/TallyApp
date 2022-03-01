package com.xiaojinzi.module.develop.bean

import com.xiaojinzi.module.base.service.develop.DevelopService
import com.xiaojinzi.module.base.service.develop.KeyAndDefValue

/**
 * 配置对应的配置项的 Key 对应的默认值
 * 没有配置的就是 [DevelopService] 上的默认值
 */
val keyAndDefValues = listOf(
    KeyAndDefValue(key = DevelopService.SP_KEY_IS_DEVELOP_VIEW_VISIBLE, defBool = true),
)

