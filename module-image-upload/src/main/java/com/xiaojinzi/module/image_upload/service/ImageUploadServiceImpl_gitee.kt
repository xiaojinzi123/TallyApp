package com.xiaojinzi.module.image_upload.service

import android.util.Base64
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.ImageUploadBaseService
import com.xiaojinzi.module.base.support.Counter
import com.xiaojinzi.module.base.support.compressBitmap
import com.xiaojinzi.module.image_upload.network.GiteeApi
import com.xiaojinzi.support.ktx.LogSupport
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@ServiceAnno(ImageUploadBaseService::class, name = [ImageUploadBaseService.TYPE_GITEE])
class ImageUploadServiceImpl_gitee : ImageUploadBaseService {

    private val api by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://gitee.com/api/v5/")
            .client(
                OkHttpClient
                    .Builder()
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiteeApi::class.java)
    }

    override suspend fun upload(file: File): String {
        LogSupport.d(tag = ImageUploadBaseService.TAG, content = "file = ${file.path}")
        val compressedFile = file.compressBitmap()
        LogSupport.d(
            tag = ImageUploadBaseService.TAG,
            content = "compressedFile = ${compressedFile.path}"
        )
        val response = api
            .uploadImage(
                fileName = "${System.currentTimeMillis()}_${Counter.get()}_${compressedFile.name}",
                // fileName = "${System.currentTimeMillis()}_test.txt",
                content = Base64.encodeToString(
                    compressedFile.inputStream().readBytes(),
                    Base64.NO_WRAP
                ),
                // content = Base64.encodeToString("test data".toByteArray(), Base64.NO_WRAP or Base64.NO_PADDING),
            )
        LogSupport.d(
            tag = ImageUploadBaseService.TAG,
            content = "response.content = ${response.content.toString()}"
        )
        return response.content.downloadUrl
    }

}