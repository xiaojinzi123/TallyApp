package com.xiaojinzi.module.image_upload.network

import com.xiaojinzi.module.image_upload.BuildConfig
import com.xiaojinzi.module.image_upload.network.bean.GiteeImageUploadResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface GiteeApi {

    /**
     * 上传图片到 gitee
     */
    @FormUrlEncoded
    @POST("repos/xiaojinzi_develop/images/contents/{fileName}")
    suspend fun uploadImage(
        @Path("fileName") fileName: String,
        @Field("access_token") accessToken: String = BuildConfig.giteeToken,
        // 对文件进行 Base64 的处理
        @Field("content") content: String,
        @Field("message") message: String = "imageUpload",
    ): GiteeImageUploadResponse

}