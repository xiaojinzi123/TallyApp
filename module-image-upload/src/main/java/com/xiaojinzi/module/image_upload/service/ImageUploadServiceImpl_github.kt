package com.xiaojinzi.module.image_upload.service

import android.util.Base64
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.ImageUploadBaseService
import com.xiaojinzi.module.base.support.Counter
import com.xiaojinzi.module.image_upload.BuildConfig
import com.xiaojinzi.module.image_upload.network.GithubApi
import com.xiaojinzi.module.image_upload.network.GithubUploadReq
import com.xiaojinzi.support.util.LogSupport
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.nio.charset.Charset

@ServiceAnno(ImageUploadBaseService::class, name = [ImageUploadBaseService.TYPE_GITHUB])
class ImageUploadServiceImpl_github : ImageUploadBaseService {

    private val api by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://api.github.com/")
            .client(
                OkHttpClient
                    .Builder()
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApi::class.java)
    }

    override suspend fun upload(file: File): String {
        LogSupport.d(tag = ImageUploadBaseService.TAG, content = "file = ${file.path}")
        val authorizationContent = Base64.encodeToString("${BuildConfig.githubUserName}:${BuildConfig.githubToken}".toByteArray(charset = Charsets.UTF_8), Base64.NO_WRAP)
        val response = api
            .uploadImage(
                authorization = "Basic $authorizationContent",
                fileName = "${System.currentTimeMillis()}_${Counter.get()}_${file.name}",
                body = GithubUploadReq(
                    content = Base64.encodeToString(
                        file.inputStream().readBytes(),
                        Base64.NO_WRAP
                    ),
                ),
            )
        LogSupport.d(
            tag = ImageUploadBaseService.TAG,
            content = "response.content = ${response.content}"
        )
        return response.content.downloadUrl
    }

}