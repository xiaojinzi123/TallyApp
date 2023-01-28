package com.xiaojinzi.tally.base.service.datasource

import androidx.annotation.Keep
import androidx.paging.PagingData
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import kotlinx.coroutines.flow.Flow

@Keep
data class BillDetailDayDTO(
    val dayStartTime: Long,
    val dayIncomeCost: Float = 0f,
    val daySpendingCost: Float = 0f,
    // 一天的花费
    val dayCost: Float = dayIncomeCost + daySpendingCost,
    // 这天的记录
    val billList: List<TallyBillDetailDTO> = emptyList(),
)

@Keep
data class PageInfo(
    val pageStartIndex: Int,
    val pageSize: Int,
)

@Keep
data class SearchQueryConditionDTO(
    val billIdList: List<String>? = null,
    val categoryGroupIdList: List<String>? = null,
    val categoryIdList: List<String>? = null,
    val bookIdList: List<String>? = null,
    val aboutAccountIdList: List<String>? = null,
    val noteKey: String? = null,
)

@Keep
enum class CostTypeDTO {
    Income, Spending,
}

/**
 * 给普通账单标记的, 标记这个账单是报销的哪种状态
 */
@Keep
enum class ReimburseType(
    val nameStringItem: StringItemDTO,
    val dbValue: Int,
) {
    // 不报销
    NoReimburse(
        nameStringItem = R.string.res_str_no_reimbursement.toStringItemDTO(),
        dbValue = 0,
    ),

    // 等待报销
    WaitReimburse(
        nameStringItem = R.string.res_str_wait_reimbursement.toStringItemDTO(),
        1,
    ),

    // 已报销
    Reimbursed(
        nameStringItem = R.string.res_str_reimbursed.toStringItemDTO(),
        2,
    ),
    ;

    companion object {

        fun fromValue(dbValue: Int): ReimburseType {
            return when (dbValue) {
                NoReimburse.dbValue -> NoReimburse
                WaitReimburse.dbValue -> WaitReimburse
                Reimbursed.dbValue -> Reimbursed
                else -> notSupportError()
            }
        }

    }

}

/**
 * 这里的每个条件都是 and 的关系
 * 如果要搜索之类的. 用 [BillQueryConditionDTO.searchQueryInfo] 对象.
 * 这个对象内的属性都会是 or 的关系
 */
@Keep
data class BillQueryConditionDTO(
    val businessLogKey: String? = null,
    val billUsage: TallyBillUsageDTO = TallyBillUsageDTO.Nothing,
    val billTypes: List<TallyBillTypeDTO>? = null,
    val cateGroupType: TallyCategoryGroupTypeDTO? = null,
    val costType: CostTypeDTO? = null,
    val categoryGroupIdList: List<String>? = null,
    val categoryIdList: List<String>? = null,
    val accountIdList: List<String>? = null,
    val accountAboutIdList: List<String>? = null,
    val bookIdList: List<String>? = null,
    val billIdList: List<String>? = null,
    val reimburseType: ReimburseType? = null,
    val reimburseBillIdList: List<String>? = null,
    val searchQueryInfo: SearchQueryConditionDTO? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val pageInfo: PageInfo? = null,
)

enum class TallyBillUsageDTO(
    val dbValue: Int,
) {

    // 无
    Nothing(
        dbValue = 0,
    ),

    // 周期任务
    CycleTask(
        dbValue = 1,
    ),
    ;

    companion object {
        fun fromValue(dbValue: Int): TallyBillUsageDTO {
            return when (dbValue) {
                Nothing.dbValue -> Nothing
                CycleTask.dbValue -> CycleTask
                else -> notSupportError()
            }
        }
    }

}

/**
 * 账单类型.
 */
enum class TallyBillTypeDTO(
    val nameItem: StringItemDTO,
    val dbValue: Int,
) {

    // 普通类型
    Normal(
        R.string.res_str_bill_normal.toStringItemDTO(),
        dbValue = 0,
    ),

    // 转账类型
    Transfer(
        R.string.res_str_bill_transfer.toStringItemDTO(),
        dbValue = 1,
    ),

    // 报销
    Reimbursement(
        R.string.res_str_bill_reimbursement.toStringItemDTO(),
        dbValue = 2,
    ),

    ;

    companion object {

        fun fromValue(dbValue: Int): TallyBillTypeDTO {
            return when (dbValue) {
                Normal.dbValue -> Normal
                Transfer.dbValue -> Transfer
                Reimbursement.dbValue -> Reimbursement
                else -> notSupportError()
            }
        }

    }

}

open class TallyBillCommon(
    // 作用
    open val usage: TallyBillUsageDTO,
    // 账单类型
    open val type: TallyBillTypeDTO,
    // 账单选择的时间
    open val time: Long,
    // 付款账户
    open val accountId: String,
    // 转账目标账户
    open val transferTargetAccountId: String?,
    // 所属的账本
    open val bookId: String,
    // 所属的类别
    open val categoryId: String?,
    // 花销. 当用户设置 100 的值, 实际会存100。 放大了一百倍存. 方便计算
    open val cost: Long,
    // 备注
    open val note: String?,
    // 账本的报销类型
    open val reimburseType: ReimburseType,
    // 报销的账单
    open val reimburseBillId: String?,
    // 是否不计入收支
    open val isNotIncludedInIncomeAndExpenditure: Boolean,
) // 卡位
{
    // 检查插入的参数
    fun check() {
        when (type) {
            TallyBillTypeDTO.Normal, TallyBillTypeDTO.Reimbursement -> {
                Assert.assertNull(value = transferTargetAccountId)
                Assert.assertNotNull(value = categoryId)
                when (type) {
                    TallyBillTypeDTO.Reimbursement -> {
                        Assert.assertNotEmptyString(value = reimburseBillId)
                    }
                    else -> {}
                }
            }
            TallyBillTypeDTO.Transfer -> {
                Assert.assertNotNull(value = transferTargetAccountId)
                Assert.assertNull(value = categoryId)
                Assert.assertEquals(value1 = reimburseType, value2 = ReimburseType.NoReimburse)
            }
        }
    }
}

@Keep
data class TallyBillInsertDTO(
    // 作用
    override val usage: TallyBillUsageDTO = TallyBillUsageDTO.Nothing,
    // 账单类型, 默认是普通的账单
    override val type: TallyBillTypeDTO = TallyBillTypeDTO.Normal,
    // 账单的时间
    override val time: Long,
    // 付款账户
    override val accountId: String,
    // 转账的目标账户
    override val transferTargetAccountId: String? = null,
    // 所属的账本
    override val bookId: String,
    // 所属的类别
    override val categoryId: String?,
    // 花销. 当用户设置 100 的值, 实际会存10000。 放大了一百倍存. 方便计算
    @ValueLong
    override val cost: Long,
    // 备注
    override val note: String?,
    // 报销的类型, 默认不报销
    override val reimburseType: ReimburseType = ReimburseType.NoReimburse,
    override val reimburseBillId: String? = null,
    // 是否不计入收支
    override val isNotIncludedInIncomeAndExpenditure: Boolean,
) : TallyBillCommon(
    usage = usage,
    type = type,
    time = time,
    accountId = accountId,
    transferTargetAccountId = transferTargetAccountId,
    bookId = bookId,
    categoryId = categoryId,
    cost = cost,
    note = note,
    reimburseType = reimburseType,
    reimburseBillId = reimburseBillId,
    isNotIncludedInIncomeAndExpenditure = isNotIncludedInIncomeAndExpenditure,
) // 卡位

@Keep
data class TallyBillDTO(
    val uid: String,
    // 是否删除了
    private val isDeleted: Boolean,
    // 作用
    override val usage: TallyBillUsageDTO,
    // 账单类型
    override val type: TallyBillTypeDTO,
    override val time: Long,
    // 付款账户
    override val accountId: String,
    // 转账目标账户
    override val transferTargetAccountId: String?,
    // 所属的账本
    override val bookId: String,
    // 所属的类别
    override val categoryId: String?,
    // 花销. 当用户设置 100 的值, 实际会存100。 放大了一百倍存. 方便计算
    override val cost: Long,
    // 花销. 这个是是在原有值的基础上, 进行调整的, 大部分场景都需要用这个值
    val costAdjust: Long,
    // 备注
    override val note: String?,
    // 账本的报销类型
    override val reimburseType: ReimburseType,
    // 报销的账单
    override val reimburseBillId: String?,
    // 是否不计入收支
    override val isNotIncludedInIncomeAndExpenditure: Boolean,
    // 是否执行删除了的检测
    val isDeleteCheck: Boolean = true
) : TallyBillCommon(
    usage = usage,
    type = type,
    time = time,
    accountId = accountId,
    transferTargetAccountId = transferTargetAccountId,
    bookId = bookId,
    categoryId = categoryId,
    cost = cost,
    note = note,
    reimburseType = reimburseType,
    reimburseBillId = reimburseBillId,
    isNotIncludedInIncomeAndExpenditure = isNotIncludedInIncomeAndExpenditure,
)  // 卡位
{

    fun costAdjustConverter(): Float {
        return if (type == TallyBillTypeDTO.Transfer) {
            -costAdjust.tallyCostAdapter()
        } else {
            costAdjust.tallyCostAdapter()
        }
    }

    init {
        // 检查这个是否是被删除了的账单
        if (isDeleteCheck) {
            Assert.assertTrue(b = !isDeleted)
        }
    }

}

@Keep
data class TallyBillDetailDTO(
    // 对应记录
    val bill: TallyBillDTO,
    // 账本
    val book: TallyBookDTO,
    // 账户
    val account: TallyAccountDTO,
    // 转账的目标账户
    val transferTargetAccount: TallyAccountDTO?,
    // 对应的类别
    val categoryWithGroup: TallyCategoryWithGroupDTO?,
    // 标签的列表
    val labelList: List<TallyLabelDTO> = emptyList(),
    // 图片的列表
    val imageUrlList: List<String> = emptyList(),
)

interface TallyBillDetailQueryPagingService {

    companion object {
        const val TAG = "TallyBillDetailQueryPagingService"

        // 每页的数量
        const val PAGE_SIZE = 20

        // 每页的数量, 按天计算的
        const val PAGE_DAY_SIZE = 10
    }

    /**
     * 数据库更新是没法自动更新的
     */
    fun subscribeCommonPageBillDetailObservable(
        billQueryCondition: BillQueryConditionDTO = BillQueryConditionDTO()
    ): Flow<PagingData<BillDetailDayDTO>>

    /**
     * 数据库更新是没法自动更新的
     */
    fun subscribeReimbursementPageBillDetailObservable1(
        billQueryCondition: BillQueryConditionDTO = BillQueryConditionDTO()
    ): Flow<PagingData<BillDetailDayDTO>>

}

/**
 * 记账的记录的 Service
 */
interface TallyBillService {

    /**
     * 订阅类别表的个数变化
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val billCountObservable: Flow<Long>

    /**
     * 账本之间的账户转移
     */
    suspend fun bookBillTransfer(oldBookId: String, newBookId: String)

    /**
     * 账户之间的账户转移
     */
    suspend fun accountBillTransfer(oldAccountId: String, newAccountId: String)

    /**
     * 类别之间的账户转移
     */
    suspend fun categoryBillTransfer(oldCategoryId: String, newCategoryId: String)

    /**
     * 根据 Id 获取
     */
    suspend fun getById(id: String): TallyBillDTO?

    /**
     * 根据 Id 获取
     */
    suspend fun getDetailById(id: String): TallyBillDetailDTO?

    /**
     * 更新某个账单的 CostAdapter 的值
     */
    suspend fun updateCostAdjust(billId: String)

    /**
     * 更新
     */
    suspend fun update(
        target: TallyBillDTO,
        labelIdList: List<String>? = null,
        imageUrlList: List<String>? = null,
    )

    /**
     * 插入一条账单
     */
    suspend fun insertBill(
        targetBill: TallyBillInsertDTO,
        labelIdList: List<String> = emptyList(),
        imageUrlList: List<String> = emptyList(),
    ): TallyBillDTO

    /**
     * 插入账单集合
     */
    suspend fun insertBills(targetList: List<TallyBillInsertDTO>): List<TallyBillDTO>

    /**
     * 获取所有的记录
     */
    suspend fun getAll(): List<TallyBillDTO>

    /**
     * 根据 id 删除
     */
    suspend fun deleteById(id: String)

    /**
     * 根据 id集合 删除
     */
    suspend fun deleteByIds(ids: List<String>)

    /**
     * 根据 类别 id 删除
     */
    suspend fun deleteByCategoryId(categoryId: String)

    /**
     * 删除账本的账单
     */
    suspend fun deleteByBookId(bookId: String)

    /**
     * 删除和这个账户 ID 相关的账单
     */
    suspend fun deleteAboutAccountId(accountId: String)

    /**
     * 获取和这个账户有关的账单数量
     */
    suspend fun getCountAboutAccountId(accountId: String): Int

    /**
     * 获取当前时间对应的月份的所有账单详情
     */
    suspend fun getMonthlyBillDetailList(
        timeStamp: Long,
    ): List<TallyBillDetailDTO>

    /**
     * 获取某一天的账单数据
     */
    suspend fun getBillDetailListByDay(timeStamp: Long): List<TallyBillDetailDTO>

    /**
     * 获取一个账户的支出总和. 对账单上的 cost 进行简单的相加即可
     * 因为账单上的记录的值的意义和支出的意义是相同的, 支出就是 cost
     * 比如 A账户 --> b账户 转账 100
     * 那么 cost 值记录的是 -100, 因为对于 A账户是损失了 100
     * 但是 B账户是算收入 100 的
     */
    suspend fun getAccountSpendingCost(accountId: String): Long

    /**
     * 获取一个账户的收入总和. 由于账单上记录的 cost 值是相对于转出方
     * 所以对于接受方, 收入就是 -cost
     * 比如 A账户 --> b账户 转账 100
     * 那么 cost 值记录的是 -100, 因为对于 A账户是损失了 100
     * 但是 B账户是算收入 100 的
     */
    suspend fun getAccountIncomeCost(accountId: String): Long

    /**
     * 根据条件查询 Count
     */
    suspend fun getCountByCondition(conditionBill: BillQueryConditionDTO): Int

    /**
     * 根据条件查询账单 ID 列表
     */
    suspend fun getBillIdListByCondition(condition: BillQueryConditionDTO): List<String>

    /**
     * 根据条件查询账单列表
     */
    suspend fun getBillListByCondition(condition: BillQueryConditionDTO): List<TallyBillDetailDTO>

    /**
     * 根据条件查询 cost
     * 这个 cost 查询出来的就是账单表的 cost 的值的相加, 没有任何符号的处理
     */
    suspend fun getBillCostByCondition(condition: BillQueryConditionDTO): Long

    /**
     * 根据条件查询 costAdjust
     */
    suspend fun getBillCostAdjustByCondition(condition: BillQueryConditionDTO): Long

    /**
     * 这个比上面的方法, 是平常经常用到的 cost 类型
     * [TallyBillTypeDTO.Normal] and [TallyBillTypeDTO.Reimbursement]
     * 根据条件查询 cost
     * 这个 cost 查询出来的就是账单表的 cost 的值的相加, 没有任何符号的处理
     */
    suspend fun getCommonBillCostByCondition(condition: BillQueryConditionDTO): Long

    /**
     * 这个比上面的方法, 是平常经常用到的 costAdjust 类型
     */
    suspend fun getNormalBillCostAdjustByCondition(condition: BillQueryConditionDTO): Long

    /**
     * 获取某个账本下有多少条记录
     */
    suspend fun getCountByBookId(
        bookId: String,
    ): Int

}

fun TallyBillDTO.toTallyBillInsertDTO(): TallyBillInsertDTO {
    return TallyBillInsertDTO(
        type = this.type,
        time = this.time,
        accountId = this.accountId,
        transferTargetAccountId = this.transferTargetAccountId,
        bookId = this.bookId,
        categoryId = this.categoryId,
        cost = this.cost,
        note = this.note,
        reimburseType = this.reimburseType,
        reimburseBillId = this.reimburseBillId,
        isNotIncludedInIncomeAndExpenditure = this.isNotIncludedInIncomeAndExpenditure,
    )
}