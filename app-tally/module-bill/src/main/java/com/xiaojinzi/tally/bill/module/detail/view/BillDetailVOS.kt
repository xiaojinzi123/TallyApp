package com.xiaojinzi.tally.bill.module.detail.view

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.tally.base.service.datasource.ReimburseType
import com.xiaojinzi.tally.base.service.datasource.TallyBillTypeDTO
import com.xiaojinzi.tally.base.view.TallyLabelVO
import com.xiaojinzi.tally.bill.R
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class BillDetailVO(
    val billId: String,
    // 账单类型
    val billType: TallyBillTypeDTO,
    val reimburseType: ReimburseType,
    val createTime: Long,
    val createTimeStr: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(createTime)),
    val bookName: StringItemDTO,
    val accountName: StringItemDTO,
    val transferTargetAccountName: StringItemDTO?,
    val categoryGroupName: StringItemDTO?,
    @DrawableRes
    val categoryIcon: Int?,
    val categoryName: StringItemDTO?,
    val cost: Float,
    val costAdjust: Float,
    val note: String?,
    val labelList: List<TallyLabelVO> = emptyList(),
    val imageUrlList: List<String> = emptyList(),
    val reimburseBillCount: Int,
    val reimburseBillCost: Float,
) {

    val categoryIconAdapter: Int get() = categoryIcon?: R.drawable.res_transfer1

}