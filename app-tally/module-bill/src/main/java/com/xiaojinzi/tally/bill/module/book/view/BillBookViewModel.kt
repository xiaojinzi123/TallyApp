package com.xiaojinzi.tally.bill.module.book.view

import androidx.lifecycle.viewModelScope
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.datasource.CostTypeDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyBookService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ViewLayer
class BillBookViewModel : BaseViewModel() {

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dataListObservableVO: Flow<List<BillBookVO>> = tallyService.dataBaseChangedObservable
        .map {
            tallyBookService.getAll()
        }
        .map { bookList ->
            bookList.map {
                BillBookVO(
                    bookId = it.uid,
                    name = StringItemDTO(
                        nameRsd = it.nameRsd,
                        name = it.name,
                    ),
                    spending = (tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = BillQueryConditionDTO(
                            costType = CostTypeDTO.Spending,
                            bookIdList = listOf(
                                it.uid,
                            ),
                        )
                    ).tallyCostAdapter()).run {
                        if (this == 0f) {
                            this
                        } else {
                            -this
                        }
                    },
                    income = tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = BillQueryConditionDTO(
                            costType = CostTypeDTO.Income,
                            bookIdList = listOf(
                                it.uid,
                            ),
                        )
                    ).tallyCostAdapter(),
                    numberOfBill = tallyBillService.getCountByBookId(bookId = it.uid),
                )
            }
        }
        .sharedStateIn(scope = viewModelScope)

}