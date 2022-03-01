package com.xiaojinzi.lib.res.dto

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class WPItemDTO(
    val categoryId: Int,
    val name: String,
    val thumbnailUrl: String,
    val downloadUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
) : Parcelable {
    fun getAspectRatio(defValue: Float): Float {
        return try {
            imageWidth.toFloat() / imageHeight
        } catch (e: Exception) {
            defValue
        }
    }
}