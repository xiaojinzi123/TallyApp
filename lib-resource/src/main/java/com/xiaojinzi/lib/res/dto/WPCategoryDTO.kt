package com.xiaojinzi.lib.res.dto

import androidx.annotation.Keep

@Keep
data class WPCategoryDTO(
    val categoryId: Int,
    val name: String,
    val order: Int,
)