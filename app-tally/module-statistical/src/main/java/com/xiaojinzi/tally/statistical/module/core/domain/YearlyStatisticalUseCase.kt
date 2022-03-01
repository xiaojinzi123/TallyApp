package com.xiaojinzi.tally.statistical.module.core.domain

import android.content.Context
import com.xiaojinzi.component.withHostAndPath
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.module.base.support.getYearInterval
import com.xiaojinzi.module.base.support.getYearStartTime
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.CostTypeDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyCategoryService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.math.abs

interface YearlyStatisticalUseCase : BaseUseCase {

    /**
     * tab 要显示的年份的时间戳
     */
    val tabYearListDTO: List<Long>

    /**
     * 选中的年份的下标
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectYearIndexObservableVO: MutableSharedStateFlow<Int>

    /**
     * 选中年份的时间戳
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectYearTimestampObservableVO: Flow<Long>

    /**
     * 指定的年支出
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val spendingCostObservableDTO: Flow<Long>

    /**
     * 指定的年收入
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val incomeCostObservableDTO: Flow<Long>

    /**
     * 指定的年结余
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val balanceCostObservableDTO: Flow<Long>

    /**
     * 支出的类别消费统计数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val spendingCategoryCostObservableDTO: Flow<List<Pair<TallyCategoryGroupDTO, Long>>>

    /**
     * 收入的类别消费统计数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val incomeCategoryCostObservableDTO: Flow<List<Pair<TallyCategoryGroupDTO, Long>>>

    /**
     * 选择的年份的所有月份的收支报表情况
     * Triple 三个值
     * 第一个值：月份的时间戳
     * 第二个值：支出
     * 第三个值：收入
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val monthReportListObservableDTO: Flow<List<Triple<Long, Long, Long>>>

    /**
     * 去当前选择的月份的展示界面
     */
    fun toCateGroupBillListView(context: Context, cateGroupId: String)

}

class YearlyStatisticalUseCaseImpl : BaseUseCaseImpl(), YearlyStatisticalUseCase {

    // 当前时间
    private val currentTime = System.currentTimeMillis()

    override val tabYearListDTO =
        (1..(getYearOfNow() - getYearOf2020() + 1)).mapIndexedNotNull { index, _ ->
            getYearTimestampByYearOffset(offset = -index)
        }

    override val selectYearIndexObservableVO = MutableSharedStateFlow(initValue = 0)

    override val selectYearTimestampObservableVO = selectYearIndexObservableVO
        .map { index ->
            tabYearListDTO[index]
        }
        .sharedStateIn(scope = scope)

    override val spendingCostObservableDTO = selectYearTimestampObservableVO
        .flatMapLatest { yearTimeStamp ->
            tallyService.subscribeWithDataBaseChanged {
                val (startTime, endTime) = getYearInterval(timeStamp = yearTimeStamp)
                tallyBillService.getNormalBillCostAdjustByCondition(
                    condition = BillQueryConditionDTO(
                        costType = CostTypeDTO.Spending,
                        startTime = startTime,
                        endTime = endTime,
                    )
                )
            }
        }

    override val incomeCostObservableDTO = selectYearTimestampObservableVO
        .flatMapLatest { yearTimeStamp ->
            tallyService.subscribeWithDataBaseChanged {
                val (startTime, endTime) = getYearInterval(timeStamp = yearTimeStamp)
                tallyBillService.getNormalBillCostAdjustByCondition(
                    condition = BillQueryConditionDTO(
                        costType = CostTypeDTO.Income,
                        startTime = startTime,
                        endTime = endTime,
                    )
                )
            }
        }

    override val balanceCostObservableDTO = combine(
        spendingCostObservableDTO, incomeCostObservableDTO
    ) { spending, income ->
        income - spending
    }

    override val spendingCategoryCostObservableDTO =
        combine(cateGroupType = CostTypeDTO.Spending)

    override val incomeCategoryCostObservableDTO =
        combine(cateGroupType = CostTypeDTO.Income)

    override val monthReportListObservableDTO = combine(
        selectYearTimestampObservableVO,
        tallyService.dataBaseChangedObservable,
    ) { timeStamp, _ ->
        // 获取今年的起点
        val yearStart = getYearStartTime(timeStamp = timeStamp)
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = yearStart
        (0..11)
            // 转换出 12 个月的时间戳
            .map { month ->
                cal.set(Calendar.MONTH, month)
                cal.timeInMillis
            }
            .map { monthTimeStamp ->
                val (startTime, endTime) = getMonthInterval(timeStamp = monthTimeStamp)
                Triple(
                    first = monthTimeStamp,
                    second = tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = BillQueryConditionDTO(
                            costType = CostTypeDTO.Spending,
                            startTime = startTime,
                            endTime = endTime,
                        )
                    ),
                    third = tallyBillService.getNormalBillCostAdjustByCondition(
                        condition = BillQueryConditionDTO(
                            costType = CostTypeDTO.Income,
                            startTime = startTime,
                            endTime = endTime,
                        )
                    ),
                )
            }
            // 过滤掉支出和收入都是 0 的
            .filter {
                it.second != 0L || it.third != 0L
            }
    }

    override fun toCateGroupBillListView(context: Context, cateGroupId: String) {
        context
            .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_BILL_CATEGORY_GROUP)
            .putLong(
                "yearTimestamp", selectYearTimestampObservableVO.value
            )
            .putString("cateGroupId", cateGroupId)
            .forward()
    }

    private fun getYearTimestampByYearOffset(offset: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.add(Calendar.YEAR, offset)
        return cal.timeInMillis
    }

    private fun getYearOfNow(): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        cal.set(Calendar.DAY_OF_YEAR, 1)
        return cal.get(Calendar.YEAR)
    }

    private fun getYearOf2020(): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentTime
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.YEAR, 2000)
        return cal.get(Calendar.YEAR)
    }

    private fun combine(cateGroupType: CostTypeDTO): Flow<List<Pair<TallyCategoryGroupDTO, Long>>> {
        return combine(
            selectYearTimestampObservableVO,
            tallyService.dataBaseChangedObservable,
        ) { yearTimeStamp, _ ->
            tallyCategoryService
                // 获取所有类别组
                .getAllTallyCategoryGroups()
                .map { cateGroup ->
                    val (yearStartTime, yearEndTime) = getYearInterval(timeStamp = yearTimeStamp)
                    Pair(
                        first = cateGroup,
                        second = tallyBillService.getNormalBillCostAdjustByCondition(
                            condition = BillQueryConditionDTO(
                                costType = cateGroupType,
                                categoryGroupIdList = listOf(
                                    cateGroup.uid
                                ),
                                startTime = yearStartTime,
                                endTime = yearEndTime,
                            )
                        )
                    )
                }
                .filter { pairItem ->
                    pairItem.second != 0L
                }
                .sortedByDescending { abs(it.second) }
        }
    }

}
