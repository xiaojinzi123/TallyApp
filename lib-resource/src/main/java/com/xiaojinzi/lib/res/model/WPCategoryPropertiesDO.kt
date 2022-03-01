package com.xiaojinzi.lib.res.model

import androidx.annotation.Keep

@Keep
data class WPCategoryPropertiesDO(
    val categoryId: NotionPropertyDO,
    val name_cn: NotionPropertyDO,
    val order: NotionPropertyDO,
)