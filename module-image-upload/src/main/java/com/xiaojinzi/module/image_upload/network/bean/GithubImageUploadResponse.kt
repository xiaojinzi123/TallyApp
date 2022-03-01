package com.xiaojinzi.module.image_upload.network.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GithubImageUploadResponse(
	val content: GithubContent
)

@Keep
data class GithubContent(
	val path: String,
	val size: Int,
	val name: String,
	@SerializedName("download_url")
	val downloadUrl: String,
	val type: String,
	val sha: String,
	val url: String
)

