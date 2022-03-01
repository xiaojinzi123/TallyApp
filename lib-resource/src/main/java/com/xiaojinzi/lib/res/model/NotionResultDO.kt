package com.xiaojinzi.lib.res.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotionResultDO<P>(
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("properties")
    val properties: P
)