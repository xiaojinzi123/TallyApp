package com.xiaojinzi.module.support.module.image_preview.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.xiaojinzi.module.base.support.ImageDefault
import com.xiaojinzi.module.base.view.compose.AppbarNormal
import com.xiaojinzi.module.support.R
import com.xiaojinzi.support.ktx.nothing

@Composable
private fun ImagePreviewView() {
    val vm: ImagePreviewViewModel = viewModel()
    val filePath by vm.filePathInitData.valueStateFlow.collectAsState(initial = null)
    val url by vm.urlInitData.valueStateFlow.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {
        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale *= zoomChange
            rotation += rotationChange
            offset += offsetChange
        }
        val imagePainter = rememberImagePainter(
            data = filePath ?: url,
            builder = {
                this.crossfade(enable = true)
                this.fallback(drawable = ImageDefault.FallbackImage)
                this.placeholder(drawable = ImageDefault.PlaceholderImage)
                this.error(drawable = ImageDefault.ErrorImage)
                this.allowHardware(enable = true)
                this.allowRgb565(enable = true)
                this.scale(scale = Scale.FIT)
            }
        )
        Image(
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = state)
                .fillMaxSize()
                .nothing()
        )

    }
}

@Composable
fun ImagePreviewViewWrap() {
    Scaffold(
        topBar = {
            AppbarNormal(
                titleRsd = R.string.res_str_preview
            )
        }
    ) {
        ImagePreviewView()
    }
}

@Preview
@Composable
private fun ImagePreviewViewPreview() {
    ImagePreviewView()
}