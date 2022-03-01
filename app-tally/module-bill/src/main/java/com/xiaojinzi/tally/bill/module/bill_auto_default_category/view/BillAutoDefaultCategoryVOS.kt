package com.xiaojinzi.tally.bill.module.bill_auto_default_category.view

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class BillAutoDefaultCategoryItemVO(
    val uid: String,
    val name: StringItemDTO,
    @DrawableRes
    val categoryIcon: Int? = null,
    val categoryName: StringItemDTO? = null,
)