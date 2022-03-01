package com.xiaojinzi.tally.bill.module.book_select.view

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.bill.module.book_select.domain.BillBookListUseCase
import com.xiaojinzi.tally.bill.module.book_select.domain.BillBookListUseCaseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class BillBookSelectViewModel(
    private val useCase: BillBookListUseCase = BillBookListUseCaseImpl()
) : BaseViewModel(), BillBookListUseCase by useCase {

    /**
     * 显示的账本列表视图
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val billBookListObservableVO: Flow<List<BillBookItemVO>> =
        combine(
            billBookListObservableDTO
                .map { list ->
                    list.map {
                        BillBookItemVO(
                            bookId = it.uid,
                            nameRsd = it.nameRsd,
                            name = it.name,
                        )
                    }
                },
            billBookIdSelectObservableDTO
        ) { voList, selectIdList ->
            voList.map {
                it.copy(isSelect = selectIdList.contains(it.bookId))
            }
        }.flowOn(context = Dispatchers.IO)

    /**
     * 是否全选了
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectAllObservableVO: Flow<Boolean> = selectAllObservableDTO

}