package com.xiaojinzi.tally.bill.module.cate_group_bill_list.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl
import com.xiaojinzi.tally.bill.module.cate_group_bill_list.domain.CateGroupBillListUseCase
import com.xiaojinzi.tally.bill.module.cate_group_bill_list.domain.CateGroupBillListUseCaseImpl

class CateGroupBillViewModel(
    private val useCase: CateGroupBillListUseCase = CateGroupBillListUseCaseImpl(),
    private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.cateGroupBillListObservableDTO
    ),
) : BaseViewModel(), CateGroupBillListUseCase by useCase,
    BillListViewUseCase by billListViewUseCase {

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

}