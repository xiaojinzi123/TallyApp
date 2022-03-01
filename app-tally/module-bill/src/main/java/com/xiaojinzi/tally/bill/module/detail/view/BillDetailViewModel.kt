package com.xiaojinzi.tally.bill.module.detail.view

import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.view.toTallyLabelVO
import com.xiaojinzi.tally.bill.module.detail.domain.BillDetailUseCase
import com.xiaojinzi.tally.bill.module.detail.domain.BillDetailUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class BillDetailViewModel(
    private val billDetailUseCase: BillDetailUseCase = BillDetailUseCaseImpl()
) : BaseViewModel(), BillDetailUseCase by billDetailUseCase {

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val billDetailVOObservable: Flow<BillDetailVO?> = combine(
        billDetailUseCase.billDetailObservable,
        billDetailUseCase.reimburseBillDetailListObservable,
    ) { billDetail, reimburseBillDetailList ->
        if (billDetail == null) {
            null
        } else // 占位
        {
            val targetItem = billDetail.bill
            val book = billDetail.book
            val account = billDetail.account
            val transferTargetAccount = billDetail.transferTargetAccount
            val cate = billDetail.categoryWithGroup?.category
            val cateGroup = billDetail.categoryWithGroup?.group
            BillDetailVO(
                billId = targetItem.uid,
                billType = targetItem.type,
                reimburseType = targetItem.reimburseType,
                createTime = targetItem.time,
                bookName = StringItemDTO(
                    nameRsd = book.nameRsd,
                    name = book.name,
                ),
                accountName = StringItemDTO(
                    nameRsd = account.nameRsd,
                    name = account.name,
                ),
                transferTargetAccountName = transferTargetAccount?.run {
                    StringItemDTO(
                        nameRsd = this.nameRsd,
                        name = this.name,
                    )
                },
                categoryGroupName = cateGroup?.run {
                    StringItemDTO(
                        nameRsd = this.nameRsd,
                        name = this.name,
                    )
                },
                categoryIcon = cate?.iconRsd,
                categoryName = cate?.run {
                    StringItemDTO(
                        nameRsd = this.nameRsd,
                        name = this.name,
                    )
                },
                cost = targetItem.cost.tallyCostAdapter(),
                costAdjust = targetItem.costAdjust.tallyCostAdapter(),
                note = targetItem.note,
                labelList = billDetail.labelList.map { it.toTallyLabelVO() },
                imageUrlList = billDetail.imageUrlList,
                reimburseBillCount = reimburseBillDetailList?.size ?: 0,
                reimburseBillCost = (reimburseBillDetailList?.map { it.bill.costAdjust }?.reduceOrNull { acc, item -> acc + item }?: 0).tallyCostAdapter(),
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        billDetailUseCase.destroy()
    }

}