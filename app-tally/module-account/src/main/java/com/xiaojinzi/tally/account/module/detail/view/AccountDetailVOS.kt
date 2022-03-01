package com.xiaojinzi.tally.account.module.detail.view

import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class AccountDetailVO(
    val isDefault: Boolean,
    val accountId: String,
    val accountName: StringItemDTO,
    val initialBalance: Float,
    val balance: Float,
)