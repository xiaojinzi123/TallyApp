package com.xiaojinzi.lib.res.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotionResponseDO<T>(
    @SerializedName("object")
    val objectStr: String,
    // 下一页数据的指针
    @SerializedName("next_cursor")
    val nextCursor: String,
    // 是否有更多
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("results")
    val results: T
)
