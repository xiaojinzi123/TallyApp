package com.xiaojinzi.tally.bill.module.book_detail.view

import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO

@Keep
data class BookDetailVO(
    val bookId: String,
    val name: StringItemDTO,
    // 总支出
    val totalSpending: Float,
    // 总收入
    val totalIncome: Float,
)