package com.xiaojinzi.module.image_upload.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.base.service.ImageUploadBaseService
import com.xiaojinzi.module.base.service.ImageUploadServerType
import com.xiaojinzi.module.base.service.ImageUploadService
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.spPersistenceNonNull
import com.xiaojinzi.support.ktx.AppScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@ServiceAnno(ImageUploadService::class)
class ImageUploadServiceImpl : ImageUploadService {

    private val uploadServerType = MutableSharedStateFlow<Int>().spPersistenceNonNull(
        key = "uploadServerType",
        def = ImageUploadServerType.Gitee.value
    )

    override fun setImageServer(server: ImageUploadServerType) {
        AppScope.launch {
            // 等一个初始化
            uploadServerType.first()
            uploadServerType.value = server.value
        }
    }

    override suspend fun upload(file: File): String {
        return when (ImageUploadServerType.fromValue(value = uploadServerType.value)) {
            ImageUploadServerType.Gitee -> {
                ServiceManager
                    .requiredGet(
                        ImageUploadBaseService::class.java,
                        ImageUploadBaseService.TYPE_GITEE
                    )
                    .upload(file = file)
            }
            ImageUploadServerType.Github -> {
                ServiceManager
                    .requiredGet(
                        ImageUploadBaseService::class.java,
                        ImageUploadBaseService.TYPE_GITHUB
                    )
                    .upload(file = file)
            }
        }
    }

}