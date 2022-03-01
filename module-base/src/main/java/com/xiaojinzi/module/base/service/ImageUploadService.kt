package com.xiaojinzi.module.base.service

import com.xiaojinzi.module.base.support.notSupportError
import java.io.File

enum class ImageUploadServerType(val value: Int) {

    Gitee(value = 1), Github(value = 2);

    companion object {

        fun fromValue(value: Int): ImageUploadServerType {
            return when (value) {
                1 -> Gitee
                2 -> Github
                else -> notSupportError()
            }
        }

    }
}

/**
 * 图片上传的服务
 */
interface ImageUploadBaseService {

    companion object {
        const val TAG = "ImageUploadService"
        const val TYPE_GITEE = "gitee"
        const val TYPE_GITHUB = "github"
    }

    /**
     * 图片上传
     */
    suspend fun upload(file: File): String

}

/**
 * 图片上传的服务
 */
interface ImageUploadService: ImageUploadBaseService {

    /**
     * 设置图床服务器
     */
    fun setImageServer(server: ImageUploadServerType)

}