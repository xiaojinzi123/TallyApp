package com.xiaojinzi.tally.home.module.account.view

import androidx.annotation.DrawableRes
import androidx.annotation.Keep

@Keep
data class AccountItemVO(
    @DrawableRes
    val iconRsd: Int,
)

@Keep
data class AccountGroupVO(
    val items: List<AccountItemVO>
)