package com.xiaojinzi.tally.bill.module.search.view

import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl
import com.xiaojinzi.tally.bill.module.search.domain.BillSearchUseCase
import com.xiaojinzi.tally.bill.module.search.domain.BillSearchUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@ViewLayer
class BillSearchViewModel(
    @ViewModelLayer private val useCase: BillSearchUseCase = BillSearchUseCaseImpl(),
    @ViewLayer private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.searchResultBillListObservableDTO
    ),
) : BaseViewModel(
), BillSearchUseCase by useCase,
    BillListViewUseCase by billListViewUseCase {

    /**
     * 标签的 UI 数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val labelListObservableVO: Flow<List<BillSearchLabelVO>> = combine(
        useCase.allLabelListObservableDTO,
        useCase.selectedLabelIdListObservableDTO,
    ) { allList, selectedIdList ->
        allList
            .map {
                BillSearchLabelVO(
                    core = it,
                    isSelected = selectedIdList.contains(it.uid),
                )
            }
    }

    /**
     * 账本的 UI 数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val bookListObservableVO: Flow<List<BillSearchBookVO>> = combine(
        useCase.allBookListObservableDTO,
        useCase.selectedBookIdListObservableDTO,
    ) { allList, selectedIdList ->
        allList
            .map {
                BillSearchBookVO(
                    core = it,
                    isSelected = selectedIdList.contains(it.uid),
                )
            }
    }

    /**
     * 账户的 UI 数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val accountListObservableVO: Flow<List<BillSearchAccountVO>> = combine(
        useCase.allAccountListObservableDTO,
        useCase.selectedAccountIdListObservableDTO,
    ) { allList, selectedIdList ->
        allList
            .map {
                BillSearchAccountVO(
                    core = it,
                    isSelected = selectedIdList.contains(it.uid),
                )
            }
    }

    /**
     * 类别组支出的 UI 数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val categoryGroupSpendingListObservableVO: Flow<List<BillSearchCategoryGroupVO>> = combine(
        useCase.allCategoryGroupListObservableDTO,
        useCase.selectedCategoryGroupIdListObservableDTO,
    ) { allList, selectedIdList ->
        allList
            .filter { it.type == TallyCategoryGroupTypeDTO.Spending }
            .map {
                BillSearchCategoryGroupVO(
                    core = it,
                    isSelected = selectedIdList.contains(it.uid),
                )
            }
    }

    /**
     * 类别组收入的 UI 数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val categoryGroupIncomeListObservableVO: Flow<List<BillSearchCategoryGroupVO>> = combine(
        useCase.allCategoryGroupListObservableDTO,
        useCase.selectedCategoryGroupIdListObservableDTO,
    ) { allList, selectedIdList ->
        allList
            .filter { it.type == TallyCategoryGroupTypeDTO.Income }
            .map {
                BillSearchCategoryGroupVO(
                    core = it,
                    isSelected = selectedIdList.contains(it.uid),
                )
            }
    }

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

}