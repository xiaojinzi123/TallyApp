package com.xiaojinzi.tally.bill.module.budget.view

import androidx.annotation.Keep

@Keep
data class MonthBudgetBoardVO(
    // 当月的预算
    val value: Float,
    // 剩余的预算
    val valueRemain: Float,
    val valueRemainPercent: Float = if (value == 0f) {
        0f
    } else {
        (valueRemain / value).coerceIn(
            minimumValue = 0f, maximumValue = 1f,
        )
    }
)