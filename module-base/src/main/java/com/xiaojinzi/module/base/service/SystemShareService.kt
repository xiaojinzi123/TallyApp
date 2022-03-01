package com.xiaojinzi.module.base.service

import android.app.Application
import android.content.Intent
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.xiaojinzi.component.ComponentActivityStack
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.R
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.module.base.FILE_PROVIDER_AUTHORITIES
import com.xiaojinzi.module.base.support.getFileSuffix
import java.io.File

/**
 * 系统分享的类
 */
interface SystemShareService {

    /**
     * 分享图片
     */
    fun shareImage(localFile: File)

    /**
     * 保存图片到相册
     */
    fun saveImageToAlbum(localFile: File)

}

@ServiceAnno(SystemShareService::class)
class SystemShareServiceImpl : SystemShareService {

    override fun shareImage(localFile: File) {
        val fileUri = FileProvider.getUriForFile(app, FILE_PROVIDER_AUTHORITIES, localFile)
        val shareIntent = Intent();
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        shareIntent.type = "image/*"
        val context = ComponentActivityStack.topAliveActivity ?: app
        if (context is Application) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val titleContent = context.getString(R.string.res_str_share_to)
        context.startActivity(Intent.createChooser(shareIntent, titleContent))
    }

    override fun saveImageToAlbum(localFile: File) {
        val suffix: String = localFile.path.getFileSuffix() ?: "jpg"
        MediaStore.Images.Media.insertImage(
            app.contentResolver,
            localFile.path,
            System.currentTimeMillis().toString() + "." + suffix,
            ""
        )
    }


}