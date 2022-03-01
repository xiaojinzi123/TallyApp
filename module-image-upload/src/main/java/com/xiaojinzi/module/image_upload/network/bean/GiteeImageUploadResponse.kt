package com.xiaojinzi.module.image_upload.network.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GiteeImageUploadResponse(
	val content: GiteeContent
)

@Keep
data class GiteeContent(
	val path: String,
	val size: Int,
	val name: String,
	@SerializedName("download_url")
	val downloadUrl: String,
	val type: String,
	val sha: String,
	val url: String
)

