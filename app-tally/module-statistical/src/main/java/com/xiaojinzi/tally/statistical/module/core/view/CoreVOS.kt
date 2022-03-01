package com.xiaojinzi.tally.statistical.module.core.view

import androidx.annotation.IntRange
import androidx.annotation.Keep
import com.xiaojinzi.module.base.support.notSupportError

@Keep
enum class StatisticalTabType(val index: Int) {

    Month(index = 0), Year(index = 1);

    companion object {
        fun fromIndex(@IntRange(from = 0, to = 1) index: Int): StatisticalTabType {
            return when (index) {
                0 -> StatisticalTabType.Month
                1 -> StatisticalTabType.Year
                else -> notSupportError()
            }
        }
    }

}

@Keep
data class YearlyStatisticalVO(
    val spending: Long,
    val income: Long,
    val balance: Long = income - spending,
) {
    val spendingAdapter = spending / 100f
    val incomeAdapter = income / 100f
    val balanceAdapter = balance / 100f
}