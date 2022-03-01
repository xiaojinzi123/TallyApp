package com.xiaojinzi.tally.statistical.module.calendar.view

import androidx.annotation.IntRange
import com.xiaojinzi.component.support.LogUtil
import com.xiaojinzi.module.base.support.*
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.view.compose.CalendarItemVO
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.datasource.TallyBillTypeDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyService
import com.xiaojinzi.tally.base.view.BillListViewUseCase
import com.xiaojinzi.tally.base.view.BillListViewUseCaseImpl
import com.xiaojinzi.tally.statistical.module.calendar.domain.CalendarStatisticalUseCase
import com.xiaojinzi.tally.statistical.module.calendar.domain.CalendarStatisticalUseCaseImpl
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

/**
 * 实现思路, 参考 desc.txt 文件
 */
class CalendarStatisticalViewModel(
    private val useCase: CalendarStatisticalUseCase = CalendarStatisticalUseCaseImpl(),
    private val billListViewUseCase: BillListViewUseCase = BillListViewUseCaseImpl(
        billDetailPageListObservableDTO = useCase.dayBillDetailListObservableDTO
    ),
) : BaseViewModel(), CalendarStatisticalUseCase by useCase,
    BillListViewUseCase by billListViewUseCase {

    companion object {
        const val PAGE_COUNT = 3
        const val PAGE_INIT = 1
    }

    override fun destroy() {
        useCase.destroy()
        billListViewUseCase.destroy()
    }

    val currDateTime = System.currentTimeMillis()

    var pageIndexOffset = 0
        private set

    // 三个月的数据源源头
    private val pageIndexChangeList = (0..2).map { MutableSharedStateFlow(initValue = Unit) }

    // 计算出三个月的时间起始点
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val pageMonthTimeListObservableVO = pageIndexChangeList
        .mapIndexed { pageIndex, signalObservable ->
            signalObservable
                .map {
                    LogUtil.loge("日历", "map.pageIndexOffset = $pageIndexOffset")
                    // 这块需要看 desc.txt 的解释
                    when {
                        pageIndexOffset < 0 -> {
                            ((pageIndexOffset - pageIndex) / PAGE_COUNT) * PAGE_COUNT + pageIndex - 1
                        }
                        pageIndexOffset > 0 -> {
                            ((pageIndexOffset + 2 - pageIndex) / PAGE_COUNT) * PAGE_COUNT + pageIndex - 1
                        }
                        else -> {
                            pageIndex - 1
                        }
                    }
                }
                // 获取目标月份的开始时间
                .map {
                    LogUtil.loge("日历", "monthOffset = $it")
                    getMonthStartTime(timeStamp = currDateTime, monthOffset = it)
                }
        }

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val pageMonthCalendarItemListObservableVO = pageMonthTimeListObservableVO
        .map { dateTimeFlow ->
            combine(
                dateTimeFlow, dateTimeOfDaySelectDTO, tallyService.dataBaseChangedObservable,
            ) { timeStamp, timeStampSelect, _ ->
                val month = getMonthByTimeStamp(timeStamp = timeStamp)
                LogUtil.loge("日历", "month = ${month + 1}")
                dataTransform(
                    monthTimeStamp = timeStamp,
                    dayTimeStampSelect = timeStampSelect
                )
            }
        }

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val pageMonthTimeStrListObservableVO = pageMonthTimeListObservableVO
        .map { dateTimeFlow ->
            dateTimeFlow.map {
                SimpleDateFormat("yyyy-MM").format(Date(it))
            }
        }

    private val currPageIndex = MutableSharedStateFlow(initValue = PAGE_INIT)

    fun setNewPageIndex(@IntRange(from = 0, to = 2) targetPageIndex: Int) {
        val currValue = currPageIndex.value
        if (currValue != targetPageIndex) {
            if (targetPageIndex !in (0..2)) {
                notSupportError()
            }
            val pageIndexOffsetOffset = if (targetPageIndex == 0 && currValue == 2) {
                1
            } else if (targetPageIndex == 2 && currValue == 0) {
                -1
            } else {
                (targetPageIndex - currPageIndex.value)
            }
            pageIndexOffset += pageIndexOffsetOffset
            val targetNeedUpdatePageIndex =
                (((targetPageIndex + pageIndexOffsetOffset) % PAGE_COUNT + PAGE_COUNT) % PAGE_COUNT)
            LogUtil.loge("日历", "targetNeedUpdatePageIndex = $targetNeedUpdatePageIndex")
            pageIndexChangeList[targetNeedUpdatePageIndex].replay()
            currPageIndex.value = targetPageIndex
        }
        LogUtil.loge("日历", "vm.pageIndexOffset2 = $pageIndexOffset")
    }

    private suspend fun dataTransform(
        monthTimeStamp: Long,
        dayTimeStampSelect: Long
    ): List<CalendarItemVO<DayCostVO>> {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = monthTimeStamp
        // calendar.add(Calendar.MONTH, -2)
        // 获取这个月的开始和结束的时间戳
        val (monthStart, monthEnd) = getMonthInterval(timeStamp = calendar.timeInMillis)
        calendar.timeInMillis = monthStart
        val dayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfMonthStart = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.timeInMillis = monthEnd
        val dayOfMonthEnd = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        LogUtil.loge("日历", "dataTransform.month = $month")
        // 一个星期的开始值是 1 开始的, 并且 1 表示的是星期日
        // 配合 UI 视图上, 星期日放在最前面, 所以计算第一排的空格数量为:
        val spaceCount = dayOfWeekStart - 1
        val resultList = mutableListOf<CalendarItemVO<DayCostVO>>()
        // 先添加前面占位的
        for (index in 0 until spaceCount) {
            resultList.add(
                CalendarItemVO(
                    isShow = false
                )
            )
        }
        // calendar.set(Calendar.YEAR, year)
        // calendar.set(Calendar.MONTH, month)
        for (index in (dayOfMonthStart..dayOfMonthEnd)) {
            calendar.set(Calendar.DAY_OF_MONTH, index)
            // 意思一下弄个 1
            calendar.set(Calendar.HOUR_OF_DAY, 1)
            val thisDayTimeInMillis = calendar.timeInMillis
            // 获取这一天的所有账单
            val dayCost = tallyBillService
                .getBillDetailListByDay(
                    timeStamp = thisDayTimeInMillis
                )
                .asSequence()
                .map {
                    when (it.bill.type) {
                        TallyBillTypeDTO.Normal -> it.bill.cost.tallyCostAdapter()
                        TallyBillTypeDTO.Transfer -> 0f
                        TallyBillTypeDTO.Reimbursement -> it.bill.cost.tallyCostAdapter()
                    }
                }
                .reduceOrNull { acc, item -> acc + item } ?: 0f
            resultList.add(
                CalendarItemVO(
                    isShow = true,
                    isToday = isSameDay(timeStamp1 = thisDayTimeInMillis, currDateTime),
                    isSelect = isSameDay(timeStamp1 = thisDayTimeInMillis, dayTimeStampSelect),
                    year = year,
                    month = month,
                    dayOfMonth = index,
                    additionalData1 = DayCostVO(
                        cost = dayCost
                    )
                )
            )
        }
        val monthList = resultList.joinToString(separator = ",") { it.month.toString() }
        LogUtil.loge("日历", "resultList.monthList = $monthList")
        return resultList
    }

}