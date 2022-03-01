package com.xiaojinzi.tally.my.module.main.view

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class MyFunctionVO(
    @DrawableRes
    val iconRsd: Int,
    val name: StringItemDTO,
    val action: (context: Context) -> Unit,
)