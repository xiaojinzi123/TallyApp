package com.xiaojinzi.tally.bill.module.label_list.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl
import com.xiaojinzi.tally.bill.module.label_list.domain.LabelBillListUseCase
import com.xiaojinzi.tally.bill.module.label_list.domain.LabelBillListUseCaseImpl

class LabelBillListViewModel(
    private val useCase: LabelBillListUseCase = LabelBillListUseCaseImpl(),
    private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.labelBillListObservableDTO
    ),
): BaseViewModel(), LabelBillListUseCase by useCase,
    BillListViewUseCase by billListViewUseCase {

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

}