package com.xiaojinzi.tally.bill.module.category.view

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO
import com.xiaojinzi.tally.bill.module.category.domain.TallyCategoryUseCase
import com.xiaojinzi.tally.bill.module.category.domain.TallyCategoryUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BillCategoryViewModel(
    private val useCase: TallyCategoryUseCase = TallyCategoryUseCaseImpl()
) : BaseViewModel(), TallyCategoryUseCase by useCase {

    val dataObservableVO: Flow<List<Pair<TallyCategoryGroupTypeDTO, List<CategoryGroupVO>>>> =
        dataObservableDTO
            .map { list ->
                list
                    .groupBy {
                        it.first.type
                    }
                    .toList()
                    .sortedByDescending { it.first.order }
                    .map {
                        Pair(
                            first = it.first,
                            second = it.second.map { groupEntity ->
                                val group = groupEntity.first
                                CategoryGroupVO(
                                    cateGroupId = group.uid,
                                    iconRsd = group.iconRsd,
                                    titleNameRsd = group.nameRsd,
                                    titleName = group.name,
                                    items = groupEntity.second
                                        .map { groupItem ->
                                            CategoryVO(
                                                categoryId = groupItem.uid,
                                                iconRsd = groupItem.iconRsd,
                                                nameRsd = groupItem.nameRsd,
                                                name = groupItem.name
                                            )
                                        }
                                )
                            }
                        )
                    }
            }

}