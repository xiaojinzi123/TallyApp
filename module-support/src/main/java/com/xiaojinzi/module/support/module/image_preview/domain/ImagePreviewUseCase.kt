package com.xiaojinzi.module.support.module.image_preview.domain

import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import java.io.File

@ViewModelLayer
interface ImagePreviewUseCase: BaseUseCase {

    /**
     * 初始化的文件
     */
    val filePathInitData: MutableInitOnceData<String?>

    /**
     * 初始化的远程的地址
     */
    val urlInitData: MutableInitOnceData<String?>

}

@ViewModelLayer
class ImagePreviewUseCaseImpl: BaseUseCaseImpl(), ImagePreviewUseCase {

    override val filePathInitData = MutableInitOnceData<String?>()

    override val urlInitData = MutableInitOnceData<String?>()

}