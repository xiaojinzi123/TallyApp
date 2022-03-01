package com.xiaojinzi.tally.bill.module.book.view

import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class BillBookVO(
    val bookId: String,
    val name: StringItemDTO,
    val spending: Float,
    val income: Float,
    val numberOfBill: Int,
)