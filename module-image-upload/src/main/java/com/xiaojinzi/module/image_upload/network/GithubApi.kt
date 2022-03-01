package com.xiaojinzi.module.image_upload.network

import androidx.annotation.Keep
import com.xiaojinzi.module.image_upload.network.bean.GithubImageUploadResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

@Keep
data class GithubUploadReq(
    val message: String = "imageUpload",
    val content: String,
)

interface GithubApi {

    /**
     * 上传图片到 gitee
     */
    @PUT("repos/xiaojinzi123/images/contents/{fileName}")

    suspend fun uploadImage(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json; charset=utf-8",
        @Path("fileName") fileName: String,
        // 对文件进行 Base64 的处理
        @Body body: GithubUploadReq,
    ): GithubImageUploadResponse

}