package com.xiaojinzi.tally.datasource.service

import androidx.paging.*
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.getDayInterval
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.datasource.data.toTallyBillDetailDTO
import com.xiaojinzi.tally.datasource.db.BillDetailPageQueryType
import com.xiaojinzi.tally.datasource.db.SupportBillDetailPageQueryImpl
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class TallyBillDetailQueryPagingSourceImpl(
    val billQueryCondition: BillQueryConditionDTO,
) : PagingSource<Int, BillDetailDayDTO>() {

    override fun getRefreshKey(state: PagingState<Int, BillDetailDayDTO>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BillDetailDayDTO> {

        LogSupport.d(
            tag = TallyBillDetailQueryPagingService.TAG,
            content = "-------------------${billQueryCondition.businessLogKey} start--------------------------"
        )

        val nextPage = params.key ?: 1
        val pageStartIndex = (nextPage - 1) * params.loadSize
        val pageSize = params.loadSize
        if (nextPage != 1) {
            delay(timeMillis = Random.nextLong(from = 500, until = 1000))
        }
        LogSupport.d(
            tag = TallyBillDetailQueryPagingService.TAG,
            content = "page load, pageStartIndex = $pageStartIndex, pageSize = $pageSize"
        )
        // 如果有 labelId 的集合, 转化为 billId 的集合
        // 这个集合每一个时间是时间戳 / 86400000 得到的
        val dayTimeList = dbTally
            .tallyBillDao()
            .getDayTimeList(
                SupportBillDetailPageQueryImpl(
                    queryType = BillDetailPageQueryType.DayTime,
                    billQueryCondition = billQueryCondition.copy(
                        pageInfo = PageInfo(
                            pageStartIndex = pageStartIndex,
                            pageSize = pageSize,
                        )
                    ),
                )
            )
        LogSupport.d(
            tag = TallyBillDetailQueryPagingService.TAG,
            content = "page load, dayTimeList = $dayTimeList"
        )
        val dataList = if (dayTimeList.isEmpty()) {
            emptyList()
        } else // 占位
        {
            val startTime: Long = dayTimeList.minOrNull()!! * 86400000
            // -1 是因为 sql 中是 <= 判断的
            val endTime: Long = (dayTimeList.maxOrNull()!! + 1) * 86400000 - 1
            LogSupport.d(
                tag = TallyBillDetailQueryPagingService.TAG,
                content = "page load, startTime = $startTime, endTime = $endTime"
            )
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            LogSupport.d(
                tag = TallyBillDetailQueryPagingService.TAG,
                content = "page load, startTime = ${format.format(Date(startTime))}, endTime = ${
                    format.format(
                        Date(endTime)
                    )
                }"
            )
            dbTally
                .tallyBillDao()
                .queryPageBillDetail(
                    condition = SupportBillDetailPageQueryImpl(
                        billQueryCondition = billQueryCondition.copy(
                            startTime = startTime,
                            endTime = endTime,
                        )
                    )
                )
                .map { it.toTallyBillDetailDTO() }
                .groupBy { getDayInterval(timeStamp = it.bill.time).first }
                .map { entity ->
                    val dayIncomeCost = entity
                        .value
                        .asSequence()
                        .filter { it.bill.type == TallyBillTypeDTO.Normal && !it.bill.isNotIncludedInIncomeAndExpenditure }
                        .filter { it.bill.cost > 0f }
                        .map { it.bill.costAdjustConverter() }
                        .reduceOrNull { acc, value -> acc + value } ?: 0f
                    val daySpendingCost = entity
                        .value
                        .asSequence()
                        .filter { it.bill.type == TallyBillTypeDTO.Normal && !it.bill.isNotIncludedInIncomeAndExpenditure }
                        .filter { it.bill.cost < 0f }
                        .map { it.bill.costAdjustConverter() }
                        .reduceOrNull { acc, value -> acc + value } ?: 0f
                    BillDetailDayDTO(
                        dayStartTime = entity.key,
                        dayIncomeCost = dayIncomeCost,
                        daySpendingCost = daySpendingCost,
                        billList = entity.value,
                    )
                }
        }
        LogSupport.d(
            tag = TallyBillDetailQueryPagingService.TAG,
            content = "page load, 加载第 $nextPage 页数据成功"
        )
        val nextKey = if (dayTimeList.size == pageSize) nextPage + 1 else null
        LogSupport.d(
            tag = TallyBillDetailQueryPagingService.TAG,
            content = "page load, 是否有下一页：${nextKey != null}"
        )
        return LoadResult.Page(
            data = dataList,
            prevKey = if (nextPage == 1) null else nextPage - 1,
            nextKey = nextKey
        ).apply {
            LogSupport.d(
                tag = TallyBillDetailQueryPagingService.TAG,
                content = "-------------------${billQueryCondition.businessLogKey} end--------------------------"
            )
        }
    }

}

@ServiceAnno(TallyBillDetailQueryPagingService::class)
class TallyBillDetailQueryPagingServiceImpl : TallyBillDetailQueryPagingService {

    private fun subscribePageBillDetailObservableCore(billQueryCondition: BillQueryConditionDTO): Flow<PagingData<BillDetailDayDTO>> {
        LogSupport.d(
            tag = TallyBillDetailQueryPagingService.TAG,
            content = "subscribePageBillDetailObservable start, queryCondition = $billQueryCondition"
        )
        return Pager(
            PagingConfig(
                pageSize = TallyBillDetailQueryPagingService.PAGE_DAY_SIZE,
                // initialLoadSize = TallyBillDetailQueryPagingService.PAGE_DAY_SIZE * 2,
                initialLoadSize = TallyBillDetailQueryPagingService.PAGE_DAY_SIZE,
            ),
            initialKey = 1,
        ) {
            TallyBillDetailQueryPagingSourceImpl(
                billQueryCondition = billQueryCondition,
            )
        }.flow
    }

    override fun subscribeCommonPageBillDetailObservable(billQueryCondition: BillQueryConditionDTO): Flow<PagingData<BillDetailDayDTO>> {
        return subscribePageBillDetailObservableCore(
            billQueryCondition = billQueryCondition.copy(
                billTypes = listOf(
                    TallyBillTypeDTO.Normal,
                    TallyBillTypeDTO.Transfer,
                )
            )
        )
    }

    override fun subscribeReimbursementPageBillDetailObservable1(billQueryCondition: BillQueryConditionDTO): Flow<PagingData<BillDetailDayDTO>> {
        return subscribePageBillDetailObservableCore(
            billQueryCondition = billQueryCondition.copy(
                billTypes = listOf(
                    TallyBillTypeDTO.Reimbursement,
                )
            )
        )
    }

}