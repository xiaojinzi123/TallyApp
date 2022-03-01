package com.xiaojinzi.module.base.bean

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.support.ktx.app
import kotlinx.parcelize.Parcelize

const val INVALID_STRING = "invalidString"

@Keep
enum class CommonViewStateVO {
    Loading, Error, Success
}

@Keep
@Parcelize
data class StringItemDTO(
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
): Parcelable {

    val isEmpty: Boolean
        get() = nameRsd == null && name.isNullOrEmpty()

    val nameOfApp: String
        get() = name ?: app.getString(nameRsd!!)

    @Composable
    fun nameAdapter(): String {
        return name ?: stringResource(id = nameRsd!!)
    }

    init {
        Assert.assertTrue(b = (nameRsd != null || name != null))
    }

}

inline fun @receiver:StringRes Int.toStringItemDTO() = StringItemDTO(nameRsd = this)
inline fun String.toStringItemDTO() = StringItemDTO(name = this)