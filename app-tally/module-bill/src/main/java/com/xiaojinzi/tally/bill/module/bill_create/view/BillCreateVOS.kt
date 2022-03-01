package com.xiaojinzi.tally.bill.module.bill_create.view

import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO

enum class BillCreateTabType(val index: Int) {

    Spending(index = 0),
    Income(index = 1),
    Transfer(index = 2),
    ;

    fun toTallyCategoryGroupType(): TallyCategoryGroupTypeDTO {
        return when (this) {
            Spending -> TallyCategoryGroupTypeDTO.Spending
            Income -> TallyCategoryGroupTypeDTO.Income
            else -> notSupportError()
        }
    }

    companion object {
        fun fromIndex(@IntRange(from = 0, to = 2) index: Int): BillCreateTabType {
            return when (index) {
                Spending.index -> Spending
                Income.index -> Income
                Transfer.index -> Transfer
                else -> notSupportError()
            }
        }
    }
}

@Keep
data class BillCreateTransferVO(
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
) {
    @Composable
    fun nameAdapter(): String {
        return name ?: stringResource(id = nameRsd!!)
    }
}

@Keep
data class BillCreateGroupVO(
    val uid: String,
    val groupType: TallyCategoryGroupTypeDTO,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
    val items: List<BillCreateGroupItemVO> = emptyList(),
)

@Keep
data class BillCreateGroupItemVO(
    val groupUid: String,
    val uid: String,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
)