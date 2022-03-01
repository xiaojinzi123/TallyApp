package com.xiaojinzi.module.base.support

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import android.graphics.BitmapFactory
import com.xiaojinzi.support.ktx.app


/**
 * 图片压缩
 */
suspend fun File.compressBitmap(maxByteSize: Long = 1 * 1024 * 1024): File {
    val targetFile = this
    return withContext(context = Dispatchers.IO) {
        // Bitmap.Config.RGB_565 每个像素是2字节
        val maxPixelSize = maxByteSize / 2
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 1
        BitmapFactory.decodeFile(targetFile.path, options)
        var outWidth = options.outWidth
        var outHeight = options.outHeight
        while (outWidth * outHeight > maxPixelSize) {
            options.inSampleSize *= 2
            BitmapFactory.decodeFile(targetFile.path, options)
            outWidth = options.outWidth
            outHeight = options.outHeight
        }
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val bitmap = BitmapFactory.decodeFile(targetFile.path, options)
        val targetFile = File(app.cacheDir, "${System.currentTimeMillis()}_compress.jpg")
        targetFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        targetFile
    }
}