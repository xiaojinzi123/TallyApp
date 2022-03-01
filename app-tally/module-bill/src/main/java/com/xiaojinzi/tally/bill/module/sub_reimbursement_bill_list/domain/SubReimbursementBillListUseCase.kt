package com.xiaojinzi.tally.bill.module.sub_reimbursement_bill_list.domain

import androidx.paging.PagingData
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.tallyBillDetailQueryPagingService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

@ViewModelLayer
interface SubReimbursementBillListUseCase: BaseUseCase {

    /**
     * 初始化的账单 Id
     */
    val billIdInitDate: MutableInitOnceData<String>

    /**
     * 报销单列表
     */
    val reimburseBillList: Flow<PagingData<BillDetailDayDTO>>

}

@ViewModelLayer
class SubReimbursementBillListUseCaseImpl: BaseUseCaseImpl(), SubReimbursementBillListUseCase {

    override val billIdInitDate = MutableInitOnceData<String>()

    @ExperimentalCoroutinesApi
    override val reimburseBillList = combine(
        tallyService.dataBaseChangedObservable,
        billIdInitDate.valueStateFlow,
    ) { _, billId ->
        tallyBillDetailQueryPagingService.subscribeReimbursementPageBillDetailObservable1(
            billQueryCondition = BillQueryConditionDTO(
                reimburseBillIdList = listOf(billId),
            )
        )
    }.flatMapLatest { it }

}