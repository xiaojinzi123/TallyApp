package com.xiaojinzi.tally.account.module.select.view

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class AccountSelectItemVO(
    val uid: String,
    // 是否选中
    val isSelect: Boolean = false,
    // 是否禁用
    val isDisable: Boolean = false,
    @DrawableRes
    val iconRsd: Int,
    val name: StringItemDTO,
    // 余额
    val balance: Long = 0,
)