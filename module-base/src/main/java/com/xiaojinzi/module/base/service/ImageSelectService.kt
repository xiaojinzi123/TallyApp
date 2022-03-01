package com.xiaojinzi.module.base.service

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.UiContext
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.ktx.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

interface ImageSelectService {

    /**
     * 选择一个图片
     */
    suspend fun selectOne(@UiContext context: Context): Uri

    /**
     * 选择多个
     */
    suspend fun selectMultiple(@UiContext context: Context): List<Uri>

    /**
     * 复制文件到本地的缓存
     */
    suspend fun copyFileToCacheFromUri(uri: Uri): File

}

@ServiceAnno(ImageSelectService::class)
class ImageSelectServiceImpl : ImageSelectService {

    private suspend fun doSelect(context: Context, isMultiple: Boolean = false): List<Uri> {
        val intent = Router.with(context)
            .hostAndPath(RouterConfig.SYSTEM_IMAGE_PICKER)
            .putBoolean("isSelectMultiple", isMultiple)
            .requestCodeRandom()
            .intentAwait()
        // 文件的 uri
        val uriList: List<Uri> = when {
            intent.data != null -> listOf(intent.data!!)
            intent.clipData != null -> intent.clipData?.let { targetClipData ->
                (0 until targetClipData.itemCount).map {
                    targetClipData.getItemAt(it).uri
                }
            } ?: emptyList()
            else -> emptyList()
        }
        return uriList
    }

    override suspend fun selectOne(context: Context): Uri {
        return doSelect(
            context = context,
            isMultiple = false,
        ).first()
    }

    override suspend fun selectMultiple(context: Context): List<Uri> {
        return doSelect(
            context = context,
            isMultiple = true,
        )
    }

    override suspend fun copyFileToCacheFromUri(uri: Uri): File {
        return withContext(context = Dispatchers.IO) {
            var fileName: String? = null
            // 读取文件的名称
            app.contentResolver
                .query(
                    uri, null, null,
                    null, null, null
                )
                ?.use { cursor ->
                    val displayNameColumnName = MediaStore.Images.ImageColumns.DISPLAY_NAME
                    if (cursor.moveToNext()) {
                        val index = cursor.getColumnIndex(displayNameColumnName)
                        if (index > -1) {
                            fileName = cursor.getString(index)
                        }
                    }
                }
            if (fileName == null) {
                notSupportError()
            }
            val targetFile = File(app.cacheDir, fileName)
            targetFile.outputStream().use { outStream ->
                app.contentResolver.openInputStream(uri)?.use {
                    it.copyTo(outStream)
                }
            }
            targetFile
        }
    }

}

