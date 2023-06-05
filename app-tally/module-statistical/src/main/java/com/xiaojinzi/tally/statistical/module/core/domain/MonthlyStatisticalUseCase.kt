package com.xiaojinzi.tally.statistical.module.core.domain

import android.content.Context
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.component.withHostAndPath
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.getMonthByTimeStamp
import com.xiaojinzi.module.base.support.getMonthInterval
import com.xiaojinzi.module.base.support.getYearByTimeStamp
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.CostTypeDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillTypeDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

@ViewModelLayer
interface MonthlyStatisticalUseCase : BaseUseCase {

    /**
     * 选择的年份
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectYearObservableDTO: MutableSharedStateFlow<Int>

    /**
     * 选择的月份
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectMonthObservableDTO: MutableSharedStateFlow<Int>

    /**
     * 选择好的月份的账单列表数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectMonthBillDetailListObservableDTO: Flow<List<TallyBillDetailDTO>>

    /**
     * 选择月后的月度概况
     * 第一个值: 支出
     * 第二个值: 收入
     * 第三个值: 结余
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectMonthReportObservableDTO: Flow<Triple<Float, Float, Float>>

    /**
     * 去当前选择的月份的展示界面
     */
    fun toMonthlyBillView(context: Context)

    /**
     * 去当前选择的月份的展示界面
     */
    fun toCateGroupBillListView(context: Context, cateGroupId: String)

    /**
     * 去选择年份
     */
    fun toChooseYear(context: Context)

}

class MonthlyStatisticalUseCaseImpl : BaseUseCaseImpl(), MonthlyStatisticalUseCase {

    override val selectYearObservableDTO =
        MutableSharedStateFlow(initValue = getYearByTimeStamp(timeStamp = System.currentTimeMillis()))

    override val selectMonthObservableDTO =
        MutableSharedStateFlow(initValue = getMonthByTimeStamp(timeStamp = System.currentTimeMillis()))

    override val selectMonthBillDetailListObservableDTO =
        combine(
            tallyService.dataBaseChangedObservable,
            selectYearObservableDTO,
            selectMonthObservableDTO,
        ) { _, year, month ->
            Calendar
                .getInstance()
                .apply {
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                }
                .timeInMillis
        }.map { timeStamp ->
            tallyBillService
                .getMonthlyBillDetailList(timeStamp = timeStamp)
                .filter {
                    // 去掉不纳入支出的账单和非正常账单
                    !it.bill.isNotIncludedInIncomeAndExpenditure && it.bill.type == TallyBillTypeDTO.Normal
                }
        }

    override val selectMonthReportObservableDTO: Flow<Triple<Float, Float, Float>> = combine(
        selectMonthObservableDTO, tallyService.dataBaseChangedObservable,
    ) { month, _ ->
        month.run {
            var cal = Calendar.getInstance()
            cal.set(Calendar.MONTH, month)
            cal.timeInMillis
        }.run {
            val (startTime, endTime) = getMonthInterval(timeStamp = this)
            var first = tallyBillService.getNormalBillCostAdjustByCondition(
                condition = BillQueryConditionDTO(
                    costType = CostTypeDTO.Spending,
                    startTime = startTime,
                    endTime = endTime,
                )
            )
            var second = tallyBillService.getNormalBillCostAdjustByCondition(
                condition = BillQueryConditionDTO(
                    costType = CostTypeDTO.Income,
                    startTime = startTime,
                    endTime = endTime,
                )
            )
            Triple(
                first.tallyCostAdapter(),
                second.tallyCostAdapter(),
                (first + second).tallyCostAdapter()
            )
        }
    }

    override fun toMonthlyBillView(context: Context) {
        context
            .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_BILL_MONTHLY)
            .putLong(
                "timestamp", Calendar
                    .getInstance()
                    .apply {
                        this.set(Calendar.YEAR, selectYearObservableDTO.value)
                        this.set(Calendar.MONTH, selectMonthObservableDTO.value)
                    }
                    .timeInMillis
            )
            .forward()
    }

    override fun toCateGroupBillListView(context: Context, cateGroupId: String) {
        context
            .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_BILL_CATEGORY_GROUP)
            .putLong(
                "monthTimestamp", Calendar
                    .getInstance()
                    .apply {
                        this.set(Calendar.YEAR, selectYearObservableDTO.value)
                        this.set(Calendar.MONTH, selectMonthObservableDTO.value)
                        this.set(Calendar.DAY_OF_MONTH, 1)
                    }
                    .timeInMillis
            )
            .putString("cateGroupId", cateGroupId)
            .forward()
    }

    override fun toChooseYear(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val targetYear = Router.with(context)
                .hostAndPath(RouterConfig.SYSTEM_YEAR_SELECT)
                .putInt("initYear", selectYearObservableDTO.value)
                .requestCodeRandom()
                .intentAwait()
                .getIntExtra("data", getYearByTimeStamp(timeStamp = System.currentTimeMillis()))
            selectYearObservableDTO.value = targetYear
        }
    }

}