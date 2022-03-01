package com.xiaojinzi.lib.res.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotionPropertyRichTextListItemTextDO(
    val content: String,
    val link: String,
)

@Keep
data class NotionPropertyRichTextListItemDO(
    val type: String,
    val text: NotionPropertyRichTextListItemTextDO,
    @SerializedName("plain_text")
    val plainText: String,
)

@Keep
data class NotionPropertyDO(
    val id: String,
    val type: String,
    val number: Int?,
    val url: String?,
    @SerializedName("rich_text")
    val richText: List<NotionPropertyRichTextListItemDO>?,
)