package com.xiaojinzi.tally.bill.module.book_select.view

import androidx.annotation.Keep
import androidx.annotation.StringRes

@Keep
data class BillBookItemVO(
    val bookId: String,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
    // 是否选中
    var isSelect: Boolean = false,
)