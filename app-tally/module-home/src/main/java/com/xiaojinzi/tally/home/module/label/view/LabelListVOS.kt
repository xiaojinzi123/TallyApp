package com.xiaojinzi.tally.home.module.label.view

import androidx.annotation.ColorInt
import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class LabelItemVO(
    // 标签的 ID
    val labelId: String,
    val content: StringItemDTO,
    @ColorInt
    val color: Int,
    // 是否选中了
    val isSelect: Boolean = false,
)