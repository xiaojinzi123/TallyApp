package com.xiaojinzi.tally.bill.module.book_detail.view

import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.datasource.CostTypeDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl
import com.xiaojinzi.tally.bill.module.book_detail.domain.BookDetailUseCase
import com.xiaojinzi.tally.bill.module.book_detail.domain.BookDetailUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ViewLayer
class BookDetailViewModel(
    @ViewModelLayer private val useCase: BookDetailUseCase = BookDetailUseCaseImpl(),
    @ViewLayer private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.bookBillListObservableDTO
    ),
) : BaseViewModel(), BookDetailUseCase by useCase,
    BillListViewUseCase by billListViewUseCase {

    val isShowMenuVO = MutableSharedStateFlow(initValue = false)

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val bookDetailObservableVO: Flow<BookDetailVO?> = bookObservableDTO
        .map { book ->
            book?.run {
                BookDetailVO(
                    bookId = this.uid,
                    name = StringItemDTO(
                        nameRsd = this.nameRsd,
                        name = this.name,
                    ),
                    totalSpending = (tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = BillQueryConditionDTO(
                            costType = CostTypeDTO.Spending,
                            bookIdList = listOf(
                                book.uid,
                            ),
                        )
                    ).tallyCostAdapter()).run {
                        if (this == 0f) {
                            this
                        } else {
                            -this
                        }
                    },
                    totalIncome = tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = BillQueryConditionDTO(
                            costType = CostTypeDTO.Income,
                            bookIdList = listOf(
                                book.uid,
                            ),
                        )
                    ).tallyCostAdapter(),
                )
            }
        }

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

}