package com.xiaojinzi.tally.bill.module.sub_reimbursement_bill_list.view

import com.xiaojinzi.tally.bill.module.sub_reimbursement_bill_list.domain.SubReimbursementBillListUseCase
import com.xiaojinzi.tally.bill.module.sub_reimbursement_bill_list.domain.SubReimbursementBillListUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl

@ViewLayer
class SubReimbursementBillListViewModel(
    private val useCase: SubReimbursementBillListUseCase = SubReimbursementBillListUseCaseImpl(),
    @ViewModelLayer private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.reimburseBillList,
    ),
) : BaseViewModel(),
    @ViewLayer SubReimbursementBillListUseCase by useCase,
    @ViewLayer BillListViewUseCase by billListViewUseCase {

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

}