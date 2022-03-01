package com.xiaojinzi.tally.bill.module.cate_group_bill_list.domain

import androidx.paging.PagingData
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.module.base.support.getYearInterval
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.base.support.tallyBillDetailQueryPagingService
import com.xiaojinzi.tally.base.support.tallyCategoryService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

interface CateGroupBillListUseCase : BaseUseCase {

    val targetMonthTimeData: MutableInitOnceData<Long?>

    val targetYearTimeData: MutableInitOnceData<Long?>

    val targetCategoryGroupIdData: MutableInitOnceData<String>

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val cateGroupObservableDTO: Flow<TallyCategoryGroupDTO>

    /**
     * 当前类别组下面的所有账单
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val cateGroupBillListObservableDTO: Flow<PagingData<BillDetailDayDTO>>

}

class CateGroupBillListUseCaseImpl : BaseUseCaseImpl(), CateGroupBillListUseCase {

    override val targetMonthTimeData = MutableInitOnceData<Long?>()

    override val targetYearTimeData = MutableInitOnceData<Long?>()

    override val targetCategoryGroupIdData = MutableInitOnceData<String>()

    override val cateGroupObservableDTO = targetCategoryGroupIdData
        .valueStateFlow
        .map { tallyCategoryService.getTallyCategoryGroupById(id = it) }
        .filterNotNull()

    @ExperimentalCoroutinesApi
    @FlowPreview
    override val cateGroupBillListObservableDTO =
        combine(
            merge(
                targetMonthTimeData.valueStateFlow.map {
                    it?.run {
                        getMonthInterval(timeStamp = this)
                    }
                },
                targetYearTimeData.valueStateFlow.map {
                    it?.run {
                        getYearInterval(timeStamp = this)
                    }
                }
            ).filterNotNull(),
            targetCategoryGroupIdData.valueStateFlow,
            tallyService.dataBaseChangedObservable,
        ) { startAndEndTime, cateGroupId, _ ->
            tallyBillDetailQueryPagingService.subscribeCommonPageBillDetailObservable(
                billQueryCondition = BillQueryConditionDTO(
                    startTime = startAndEndTime.first,
                    endTime = startAndEndTime.second,
                    categoryGroupIdList = listOf(cateGroupId)
                )
            )
        }.flatMapLatest { it }

}
