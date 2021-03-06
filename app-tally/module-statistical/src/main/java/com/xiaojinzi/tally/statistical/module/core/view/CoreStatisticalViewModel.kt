package com.xiaojinzi.tally.statistical.module.core.view

import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.getMonthByTimeStamp
import com.xiaojinzi.module.base.support.getYearByTimeStamp
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.datasource.CostTypeDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.base.support.settingService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.view.*
import com.xiaojinzi.tally.statistical.R
import com.xiaojinzi.tally.statistical.module.core.domain.CoreStatisticalUseCase
import com.xiaojinzi.tally.statistical.module.core.domain.CoreStatisticalUseCaseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.math.abs

@ViewLayer
class CoreStatisticalViewModel(
    @ViewModelLayer private val useCase: CoreStatisticalUseCase = CoreStatisticalUseCaseImpl(),
    @ViewModelLayer private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailListObservableDTO = useCase.monthlyStatisticalUseCase.selectMonthBillDetailListObservableDTO
    ),
) : BaseViewModel(),
    @ViewModelLayer CoreStatisticalUseCase by useCase,
    @ViewLayer BillListViewUseCase by billListViewUseCase {

    private val monthRsdList = listOf(
        R.string.res_str_january,
        R.string.res_str_february,
        R.string.res_str_march,
        R.string.res_str_april,
        R.string.res_str_may,
        R.string.res_str_june,
        R.string.res_str_july,
        R.string.res_str_august,
        R.string.res_str_september,
        R.string.res_str_october,
        R.string.res_str_november,
        R.string.res_str_december,
    )

    /**
     * ???????????????
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val tabTypeSelectObservableVO = MutableSharedStateFlow(initValue = StatisticalTabType.Month)

    /**
     * ???????????????????????????????????????????????????
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectMonthSpendingCostPercentVO = combine(
        monthlyStatisticalUseCase.selectMonthBillDetailListObservableDTO,
        settingService.cateCostProgressPercentOptimizeObservableDTO
    ) { billDetailList, isProgressPercentOptimize ->
        dataTransform(
            billDetailList = billDetailList,
            type = CostTypeDTO.Spending,
            isProgressPercentOptimize = isProgressPercentOptimize,
        )
    }.flowOn(context = Dispatchers.IO)

    /**
     * ???????????????????????????????????????????????????
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectMonthIncomeCostPercentVO = combine(
        monthlyStatisticalUseCase.selectMonthBillDetailListObservableDTO,
        settingService.cateCostProgressPercentOptimizeObservableDTO
    ) { billDetailList, isProgressPercentOptimize ->
        dataTransform(
            billDetailList = billDetailList,
            type = CostTypeDTO.Income,
            isProgressPercentOptimize = isProgressPercentOptimize,
        )
    }.flowOn(context = Dispatchers.IO)

    /**
     * ????????? tab ???????????? tab
     */
    val tabYearListVO = yearlyStatisticalUseCase.tabYearListDTO.map { timeStamp ->
        getYearByTimeStamp(timeStamp = timeStamp)
    }

    /**
     * ???????????????????????????
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val yearlyStatisticalObservableVO: Flow<YearlyStatisticalVO> =
        combine(
            yearlyStatisticalUseCase.incomeCostObservableDTO,
            yearlyStatisticalUseCase.spendingCostObservableDTO,
        ) { income, spending ->
            YearlyStatisticalVO(
                spending = -spending,
                income = income,
            )
        }

    /**
     * ??????????????????????????? tab ????????????
     */
    val monthCostTabSelectIndexObservableVO = MutableSharedStateFlow(initValue = 0)

    /**
     * ??????????????????????????? tab ????????????
     */
    val yearCostTabSelectIndexObservableVO = MutableSharedStateFlow(initValue = 0)

    /**
     * ????????????????????????????????????????????????
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectYearIncomeCostPercentVO: Flow<CateGroupCostPercentGroupVO> =
        combine(
            yearlyStatisticalUseCase.incomeCategoryCostObservableDTO,
            settingService.cateCostProgressPercentOptimizeObservableDTO
        ) { list, isProgressPercentOptimize ->
            dataTransform2(list = list, isProgressPercentOptimize = isProgressPercentOptimize)
        }

    /**
     * ????????????????????????????????????????????????
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectYearSpendingCostPercentVO: Flow<CateGroupCostPercentGroupVO> =
        combine(
            yearlyStatisticalUseCase.spendingCategoryCostObservableDTO,
            settingService.cateCostProgressPercentOptimizeObservableDTO
        ) { list, isProgressPercentOptimize ->
            dataTransform2(list = list, isProgressPercentOptimize = isProgressPercentOptimize)
        }

    /**
     * ???????????????????????????????????????
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectYearMonthReportListObservableVO: Flow<List<BillMonthListReportItem>> =
        yearlyStatisticalUseCase
            .monthReportListObservableDTO
            .map { list ->
                list
                    .reversed()
                    .map { tripleItem ->
                        val timestamp = tripleItem.first
                        // ??????????????? 0-11, ???????????? index
                        val month = getMonthByTimeStamp(timeStamp = timestamp)
                        BillMonthListReportItem(
                            timestamp = timestamp,
                            monthStr1 = StringItemDTO(nameRsd = monthRsdList[month]),
                            spending = tripleItem.second.tallyCostAdapter(),
                            income = tripleItem.third.tallyCostAdapter(),
                        )
                    }
            }


    private suspend fun dataTransform(
        billDetailList: List<TallyBillDetailDTO>,
        type: CostTypeDTO,
        isProgressPercentOptimize: Boolean,
    ): CateGroupCostPercentGroupVO {
        return withContext(context = Dispatchers.IO) {

            val targetList = billDetailList
                .filter {
                    when (type) {
                        CostTypeDTO.Spending -> it.bill.cost < 0
                        CostTypeDTO.Income -> it.bill.cost > 0
                    }
                }

            // ?????????, ???????????????, ???????????????
            val totalCost = targetList
                .asSequence()
                .map { it.bill.costAdjust.tallyCostAdapter() }
                .reduceOrNull { acc, item -> acc + item } ?: 0f

            // ???????????????????????????
            val costItemListVO = targetList
                .groupBy { it.categoryWithGroup?.group }
                .filter { it.key != null }
                .map { entity ->
                    val cateGroup: TallyCategoryGroupDTO = entity.key!!
                    val itemTotalCost = entity
                        .value
                        .asSequence()
                        .map { it.bill.costAdjust.tallyCostAdapter() }
                        .reduceOrNull { acc, item -> acc + item } ?: 0f
                    CateGroupCostPercentItemVO(
                        cateGroupId = cateGroup.uid,
                        cateGroupIconRsd = cateGroup.iconRsd,
                        cateGroupName = StringItemDTO(
                            nameRsd = cateGroup.nameRsd,
                            name = cateGroup.name,
                        ),
                        cost = itemTotalCost,
                        costPercent = (if (totalCost == 0f) 0f else itemTotalCost / totalCost) * 100f,
                    )
                }
                .sortedByDescending { abs(it.cost) }

            val costItemListResultVO = if (isProgressPercentOptimize) {
                // ??????????????????????????????
                val targetCostMaxItem = costItemListVO.maxByOrNull { abs(it.cost) }
                // ????????? item ????????????????????????
                if (targetCostMaxItem == null || targetCostMaxItem.cost == 0f) {
                    costItemListVO
                } else {
                    // assert(value = targetCostMaxItem.cost > 0f)
                    costItemListVO.map {
                        it.copy(progressPercent = abs(it.cost) / abs(targetCostMaxItem.cost))
                    }
                }
            } else {
                costItemListVO
            }

            return@withContext CateGroupCostPercentGroupVO(
                cost = totalCost,
                items = costItemListResultVO,
            )
        }
    }

    private suspend fun dataTransform2(
        list: List<Pair<TallyCategoryGroupDTO, Long>>,
        isProgressPercentOptimize: Boolean
    ): CateGroupCostPercentGroupVO {
        // ???????????????
        return withContext(context = Dispatchers.IO) {
            val totalCost: Long = list
                .map { it.second }
                .reduceOrNull { acc, item -> acc + item }
                ?: 0L
            val items = list
                .map { pairItem ->
                    val cateGroup = pairItem.first
                    CateGroupCostPercentItemVO(
                        cateGroupId = cateGroup.uid,
                        cateGroupIconRsd = cateGroup.iconRsd,
                        cateGroupName = StringItemDTO(
                            nameRsd = cateGroup.nameRsd,
                            name = cateGroup.name,
                        ),
                        cost = pairItem.second.tallyCostAdapter(),
                        costPercent = if (totalCost == 0L) 0f else pairItem.second * 100f / totalCost,
                    )
                }
            val resultItems = if (isProgressPercentOptimize) {
                val itemMaxCost = items.maxByOrNull { abs(it.cost) }
                if (itemMaxCost == null || itemMaxCost.cost == 0f) {
                    items
                } else {
                    items.map {
                        it.copy(
                            progressPercent = abs(it.cost) / abs(itemMaxCost.cost)
                        )
                    }
                }
            } else {
                items
            }
            return@withContext CateGroupCostPercentGroupVO(
                cost = totalCost.tallyCostAdapter(),
                items = resultItems
            )
        }
    }

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

}