package com.xiaojinzi.lib.res.model

import androidx.annotation.Keep

@Keep
data class WPPropertiesDO(
    val categoryId: NotionPropertyDO,
    val name_cn: NotionPropertyDO,
    val downloadUrl: NotionPropertyDO,
    val imageWidth: NotionPropertyDO,
    val imageHeight: NotionPropertyDO,
    val order: NotionPropertyDO,
)