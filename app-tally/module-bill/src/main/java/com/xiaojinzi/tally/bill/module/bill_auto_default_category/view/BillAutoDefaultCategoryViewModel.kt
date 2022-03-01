package com.xiaojinzi.tally.bill.module.bill_auto_default_category.view

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.bill.module.bill_auto_default_category.domain.BillAutoDefaultCategoryUseCase
import com.xiaojinzi.tally.bill.module.bill_auto_default_category.domain.BillAutoDefaultCategoryUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.tally.bill.module.bill_auto_default_account.view.BillAutoDefaultAccountItemVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ViewLayer
class BillAutoDefaultCategoryViewModel(
    private val useCase: BillAutoDefaultCategoryUseCase = BillAutoDefaultCategoryUseCaseImpl(),
) : BaseViewModel(), BillAutoDefaultCategoryUseCase by useCase {

    /**
     * 显示的列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val dataListObservableVO: Flow<List<BillAutoDefaultCategoryItemVO>> = dataListObservableDTO
        .map { list ->
            list.map {
                BillAutoDefaultCategoryItemVO(
                    uid = it.core.uid,
                    name = it.core.name,
                    categoryIcon = it.category?.iconRsd,
                    categoryName = it.category?.getStringItemVO()
                )
            }
        }

}