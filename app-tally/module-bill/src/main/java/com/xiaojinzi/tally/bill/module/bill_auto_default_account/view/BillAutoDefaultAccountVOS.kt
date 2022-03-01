package com.xiaojinzi.tally.bill.module.bill_auto_default_account.view

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class BillAutoDefaultAccountItemVO(
    val uid: String,
    val name: StringItemDTO,
    @DrawableRes
    val accountIcon: Int? = null,
    val accountName: StringItemDTO? = null,
)