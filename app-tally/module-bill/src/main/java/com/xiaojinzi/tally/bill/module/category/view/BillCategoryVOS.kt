package com.xiaojinzi.tally.bill.module.category.view

import androidx.annotation.IntRange
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO

enum class BillCategoryTabType(val index: Int) {

    Spending(index = 0), Income(index = 1);

    fun toTallyCategoryGroupType(): TallyCategoryGroupTypeDTO {
        return when(this) {
            Spending -> TallyCategoryGroupTypeDTO.Spending
            Income -> TallyCategoryGroupTypeDTO.Income
        }
    }

    companion object {
        fun fromIndex(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) index: Int): BillCategoryTabType {
            return when (index) {
                0 -> Spending
                1 -> Income
                else -> notSupportError()
            }
        }
    }

}