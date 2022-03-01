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
     * 标题的选中
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val tabTypeSelectObservableVO = MutableSharedStateFlow(initValue = StatisticalTabType.Month)

    /**
     * 选中的这个月的支出的百分比展示数据
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
     * 选中的这个月的收入的百分比展示数据
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
     * 年统计 tab 要显示的 tab
     */
    val tabYearListVO = yearlyStatisticalUseCase.tabYearListDTO.map { timeStamp ->
        getYearByTimeStamp(timeStamp = timeStamp)
    }

    /**
     * 选择的年的统计数据
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
     * 月统计中类别消费的 tab 下标选择
     */
    val monthCostTabSelectIndexObservableVO = MutableSharedStateFlow(initValue = 0)

    /**
     * 年统计中类别消费的 tab 下标选择
     */
    val yearCostTabSelectIndexObservableVO = MutableSharedStateFlow(initValue = 0)

    /**
     * 选中的这年的支出的百分比展示数据
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
     * 选中的这年的支出的百分比展示数据
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
     * 选择的这年的每月的数据报表
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
                        // 这个因为是 0-11, 正好对上 index
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

            // 总耗费, 可能是收入, 可能是支出
            val totalCost = targetList
                .asSequence()
                .map { it.bill.costAdjust.tallyCostAdapter() }
                .reduceOrNull { acc, item -> acc + item } ?: 0f

            // 根据类别组进行分组
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
                // 找出耗费值最大的那个
                val targetCostMaxItem = costItemListVO.maxByOrNull { abs(it.cost) }
                // 对每个 item 的百分比进行调整
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
        // 可能为负数
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