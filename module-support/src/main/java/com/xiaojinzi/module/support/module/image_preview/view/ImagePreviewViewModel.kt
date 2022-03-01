package com.xiaojinzi.module.support.module.image_preview.view

import com.xiaojinzi.module.support.module.image_preview.domain.ImagePreviewUseCase
import com.xiaojinzi.module.support.module.image_preview.domain.ImagePreviewUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
class ImagePreviewViewModel(
    private val useCase: ImagePreviewUseCase = ImagePreviewUseCaseImpl(),
) : BaseViewModel(), ImagePreviewUseCase by useCase {
    // TODO
}