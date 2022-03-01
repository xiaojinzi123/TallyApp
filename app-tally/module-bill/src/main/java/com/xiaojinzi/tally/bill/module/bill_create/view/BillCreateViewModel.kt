package com.xiaojinzi.tally.bill.module.bill_create.view

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.bill.module.bill_create.domain.BillCreateUseCase
import com.xiaojinzi.tally.bill.module.bill_create.domain.BillCreateUseCaseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class BillCreateViewModel(
    private val billCreateUseCase: BillCreateUseCase = BillCreateUseCaseImpl()
) : BaseViewModel(),
    BillCreateUseCase by billCreateUseCase {

    /**
     * 收入的类别
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val inComeCategoryListObservableVO: Flow<List<BillCreateGroupVO>> =
        inComeCategoryListObservableDTO
            .map { list ->
                dataTransform(targetList = list)
            }
            .flowOn(context = Dispatchers.IO)

    /**
     * 支出的类别
     */
    val spendingCategoryListObservableVO: Flow<List<BillCreateGroupVO>> =
        spendingCategoryListObservableDTO
            .map { list ->
                dataTransform(targetList = list)
            }
            .flowOn(context = Dispatchers.IO)

    /**
     * 转账转出账户的 vo 数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val outTransferAccountVO: Flow<BillCreateTransferVO?> = billCreateUseCase
        .billCreateTransferUseCase
        .selectedOutAccountObservableDTO
        .map {
            if (it == null) {
                null
            } else {
                BillCreateTransferVO(
                    iconRsd = it.iconRsd,
                    nameRsd = it.nameRsd,
                    name = it.name,
                )
            }

        }

    /**
     * 转账转入账户的 vo 数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val inTransferAccountVO: Flow<BillCreateTransferVO?> = billCreateUseCase
        .billCreateTransferUseCase
        .selectedInAccountObservableDTO.map {
            if (it == null) {
                null
            } else {
                BillCreateTransferVO(
                    iconRsd = it.iconRsd,
                    nameRsd = it.nameRsd,
                    name = it.name,
                )
            }

        }

    private fun dataTransform(targetList: List<Pair<TallyCategoryGroupDTO, List<TallyCategoryDTO>>>): List<BillCreateGroupVO> {
        return targetList
            .filter { !it.second.isNullOrEmpty() }
            .map { pairItem ->
                val group = pairItem.first
                BillCreateGroupVO(
                    uid = group.uid,
                    groupType = group.type,
                    iconRsd = group.iconRsd,
                    nameRsd = group.nameRsd,
                    name = group.name,
                    items = pairItem.second.map { item ->
                        BillCreateGroupItemVO(
                            groupUid = group.uid,
                            uid = item.uid,
                            iconRsd = item.iconRsd,
                            nameRsd = item.nameRsd,
                            name = item.name,
                        )
                    }
                )
            }
    }

}