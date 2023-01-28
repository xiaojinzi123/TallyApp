package com.xiaojinzi.tally.datasource.db

import androidx.annotation.Keep
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteProgram
import androidx.sqlite.db.SupportSQLiteQuery
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.service.datasource.*
import kotlinx.coroutines.flow.Flow

/*@Keep
@Entity(tableName = TallyBillDao.TableName)
data class TallyBillCostDO(
    @ColumnInfo(name = "cost") val cost: Long,
)*/

/**
 * 账单表最原始的表的映射对象
 */
@Keep
@Entity(
    tableName = TallyBillDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyBillDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = createTime,
    // 是否被删除了 ----------------------- 这个值暂时没被用到, 因为现在是真删除 -----------------------
    @ColumnInfo(name = "isDeleted") val isDeleted: Boolean = false,
    // 作用： 无：0  周期任务 1
    @ColumnInfo(name = "usage") val usage: Int,
    // 账单的类型: 普通账单 0, 转账 1, 报销单 2(reimburseBillId 会有值)
    @ColumnInfo(name = "type") val type: Int,
    // 账单的时间
    @ColumnInfo(name = "time", index = true) val time: Long,
    // 账户的 ID, 付款的账户
    @ColumnInfo(name = "accountId") val accountId: String,
    // 目标账户, 当类型是转账的时候, 这个会有值
    @ColumnInfo(name = "transferTargetAccountId") val transferTargetAccountId: String?,
    // 所属的账本 ID
    @ColumnInfo(name = "bookId") val bookId: String,
    // 所属的类别的 id, 有些类型是没有这个类别字段的
    @ColumnInfo(name = "categoryId") val categoryId: String?,
    // 消耗的金钱. 值可能有正有负. 存的值是计量单位的 100 倍
    @ValueLong
    @ColumnInfo(name = "cost") val cost: Long,
    // 对 cost 进行调整后的值
    @ValueLong
    @ColumnInfo(name = "costAdjust") val costAdjust: Long,
    // 备注
    @ColumnInfo(name = "note") val note: String?,
    // 报销的类型, 0 不报销, 1 待报销, 2 已经报销
    @ColumnInfo(name = "reimburseType", defaultValue = "0") val reimburseType: Int,
    // 报销的账单 Id, 这个账单是依赖另外一个账单的. 并且有这个值的账单, 它的类型一定是 报销单 2
    @ColumnInfo(name = "reimburseBillId", defaultValue = "NULL") val reimburseBillId: String?,
    // 是否不计入收支
    @ColumnInfo(
        name = "isNotIncludedInIncomeAndExpenditure",
        defaultValue = "0"
    ) val isNotIncludedInIncomeAndExpenditure: Boolean,
)

@Keep
data class TallyBillDetailDO(
    @Embedded
    val bill: TallyBillDO,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "uid",
        entity = TallyCategoryDO::class,
    )
    val categoryWithGroup: TallyCategoryWithGroupDO?,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "uid",
        entity = TallyBookDO::class,
    )
    val book: TallyBookDO,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "uid",
        entity = TallyAccountDO::class,
    )
    val account: TallyAccountDO,
    @Relation(
        parentColumn = "transferTargetAccountId",
        entityColumn = "uid",
        entity = TallyAccountDO::class,
    )
    val transferTargetAccount: TallyAccountDO?,
    @Relation(
        parentColumn = "uid",
        entityColumn = "uid",
        associateBy = Junction(
            value = TallyBillLabelDO::class,
            parentColumn = "billId",
            entityColumn = "labelId",
        )
    )
    val labelList: List<TallyLabelDO>,
    // 本来报销账单和对应的账单也是一对多的关系. 由于是一张表, 所以怕弄不清, 这里就不建立关系了
)

enum class BillDetailPageQueryType {
    Detail, DayTime, Cost, CostAdjust, Count, Id,
}

class SupportBillDetailPageQueryImpl(
    private val queryType: BillDetailPageQueryType = BillDetailPageQueryType.Detail,
    private val billQueryCondition: BillQueryConditionDTO,
) : SupportSQLiteQuery {

    private val TAG = "SupportSQLiteQuery"

    override fun getSql(): String {

        LogSupport.d(
            tag = TAG,
            content = "-------------------${billQueryCondition.businessLogKey} billPageQuerySql getSql start--------------------------"
        )

        val sb = StringBuffer()
        when (queryType) {
            BillDetailPageQueryType.DayTime -> {
                sb.append("SELECT b.time / 86400000 as day_time FROM ${TallyBillDao.TableName} b")
            }
            BillDetailPageQueryType.Detail -> {
                sb.append("SELECT b.* FROM ${TallyBillDao.TableName} b")
            }
            BillDetailPageQueryType.Cost -> {
                sb.append("SELECT b.cost FROM ${TallyBillDao.TableName} b")
            }
            BillDetailPageQueryType.CostAdjust -> {
                sb.append("SELECT b.costAdjust FROM ${TallyBillDao.TableName} b")
            }
            BillDetailPageQueryType.Count -> {
                sb.append("SELECT count(b.uid) FROM ${TallyBillDao.TableName} b")
            }
            BillDetailPageQueryType.Id -> {
                sb.append("SELECT b.uid FROM ${TallyBillDao.TableName} b")
            }
        }
        sb.append(" LEFT JOIN ${TallyCategoryDao.TableName} c on c.uid = b.categoryId")
        sb.append(" LEFT JOIN ${TallyCategoryGroupDao.TableName} cg on cg.uid = c.groupId")
        // 添加条件
        sb.append(" where b.isDeleted = 0 and b.usage = ${billQueryCondition.billUsage.dbValue}")
        billQueryCondition.billTypes?.let { billTypes ->
            val billTypeCondition: String = billTypes.joinToString { "'${it.dbValue}'" }
            sb.append(" and b.type in (${billTypeCondition})")
        }
        billQueryCondition.cateGroupType?.let { cateGroupType ->
            sb.append(" and cg.type = ${cateGroupType.dbValue}")
        }
        billQueryCondition.costType?.let { costType ->
            when (costType) {
                CostTypeDTO.Income -> {
                    sb.append(" and b.cost > 0")
                }
                CostTypeDTO.Spending -> {
                    sb.append(" and b.cost < 0")
                }
            }
        }
        billQueryCondition.categoryGroupIdList?.let { categoryGroupIdList ->
            val categoryGroupCondition: String = categoryGroupIdList.joinToString { "'$it'" }
            sb.append(" and cg.uid in ($categoryGroupCondition)")
        }
        billQueryCondition.categoryIdList?.let { categoryIdList ->
            val categoryCondition: String = categoryIdList.joinToString { "'$it'" }
            sb.append(" and c.uid in ($categoryCondition)")
        }
        billQueryCondition.bookIdList?.let { bookIdList ->
            val bookCondition: String = bookIdList.joinToString { "'$it'" }
            sb.append(" and b.bookId in ($bookCondition)")
        }
        billQueryCondition.billIdList?.let { billIdList ->
            val billIdCondition: String = billIdList.joinToString { "'$it'" }
            sb.append(" and b.uid in ($billIdCondition)")
        }
        billQueryCondition.reimburseType?.let { billQueryCondition ->
            sb.append(" and b.reimburseType = ${billQueryCondition.dbValue}")
        }
        billQueryCondition.reimburseBillIdList?.let { reimburseBillIdList ->
            val reimburseBillIdCondition: String = reimburseBillIdList.joinToString { "'$it'" }
            sb.append(" and b.reimburseBillId in ($reimburseBillIdCondition)")
        }
        billQueryCondition.accountIdList?.let { accountIdList ->
            val accountCondition: String = accountIdList.joinToString { "'$it'" }
            sb.append(" and b.accountId in ($accountCondition)")
        }
        billQueryCondition.accountAboutIdList?.let { accountAboutIdList ->
            val accountAboutCondition: String = accountAboutIdList.joinToString { "'$it'" }
            sb.append(" and (b.accountId in ($accountAboutCondition) or b.transferTargetAccountId in ($accountAboutCondition))")
        }
        billQueryCondition.searchQueryInfo?.let { searchQueryInfo ->
            val searchQuerySqlList = mutableListOf<String>()
            searchQueryInfo.categoryIdList?.let { searchCategoryIdList ->
                val categoryIdListCondition = searchCategoryIdList.joinToString { "'$it'" }
                searchQuerySqlList.add("c.uid in ($categoryIdListCondition)")
            }
            searchQueryInfo.bookIdList?.let { bookIdList ->
                val bookIdListCondition = bookIdList.joinToString { "'$it'" }
                searchQuerySqlList.add("b.bookId in ($bookIdListCondition)")
            }
            searchQueryInfo.aboutAccountIdList?.let { aboutAccountIdList ->
                val aboutAccountIdListCondition = aboutAccountIdList.joinToString { "'$it'" }
                searchQuerySqlList.add("b.accountId in ($aboutAccountIdListCondition) or b.transferTargetAccountId in ($aboutAccountIdListCondition)")
            }
            searchQueryInfo.billIdList?.let { searchBillIdList ->
                val labelIdListCondition = searchBillIdList.joinToString { "'$it'" }
                searchQuerySqlList.add("b.uid in ($labelIdListCondition)")
            }
            if (!searchQueryInfo.noteKey.isNullOrEmpty()) {
                searchQuerySqlList.add("b.note LIKE '%${searchQueryInfo.noteKey}%' ")
            }
            if (searchQuerySqlList.isNotEmpty()) {
                sb.append(" and (")
                sb.append(
                    searchQuerySqlList.joinToString(separator = " or ")
                )
                sb.append(")")
            }
        }
        billQueryCondition.startTime?.let {
            sb.append(" and time >= $it")
        }
        billQueryCondition.endTime?.let {
            sb.append(" and time <= $it")
        }
        when (queryType) {
            BillDetailPageQueryType.Cost, BillDetailPageQueryType.CostAdjust -> {
                sb.append(" and isNotIncludedInIncomeAndExpenditure = 0")
            }
            else -> {}
        }
        when (queryType) {
            BillDetailPageQueryType.DayTime -> {
                // day_time 这个是上面 sql 查询的别名
                sb.append(" group by day_time")
                sb.append(" order by day_time desc")
            }
            BillDetailPageQueryType.Detail -> {
                sb.append(" order by time desc")
            }
            else -> {}
        }
        billQueryCondition.pageInfo?.let { pageInfo ->
            sb.append(" limit ${pageInfo.pageStartIndex}, ${pageInfo.pageSize}")
        }

        return sb.toString().apply {
            LogSupport.d(
                tag = TAG,
                content = "-------------------${billQueryCondition.businessLogKey} billPageQuerySql getSql end--------------------------"
            )
            LogSupport.d(
                tag = TAG,
                content = "${billQueryCondition.businessLogKey} billPageQuerySql = $this"
            )
        }
    }

    override fun bindTo(statement: SupportSQLiteProgram?) {
    }

    override fun getArgCount(): Int {
        return 0
    }

}

@Dao
interface TallyBillDao {

    companion object {
        const val TableName = "tally_bill"
        const val SelectById = "SELECT * FROM $TableName where isDeleted = 0 and uid=:uid"
    }

    @Transaction
    @Query("UPDATE $TableName SET categoryId = :newCategoryId where categoryId = :oldCategoryId")
    suspend fun categoryIdBillTransfer(oldCategoryId: String, newCategoryId: String)

    @Transaction
    @Query("UPDATE $TableName SET bookId = :newBookId where bookId = :oldBookId")
    suspend fun bookIdBillTransfer(oldBookId: String, newBookId: String)

    @Transaction
    @Query("UPDATE $TableName SET accountId = :newAccountId where accountId = :oldAccountId")
    suspend fun accountIdBillTransfer(oldAccountId: String, newAccountId: String)

    @Transaction
    @Query("UPDATE $TableName SET transferTargetAccountId = :newAccountId where transferTargetAccountId = :oldAccountId")
    suspend fun transferTargetAccountIdBillTransfer(oldAccountId: String, newAccountId: String)

    @Query("SELECT * FROM $TableName where isDeleted = 0")
    suspend fun getAll(): List<TallyBillDO>

    @Query(SelectById)
    suspend fun getById(uid: String): TallyBillDO?

    @Query(SelectById)
    suspend fun getDetailById(uid: String): TallyBillDetailDO?

    @Query("SELECT uid FROM $TableName where accountId =:accountId or transferTargetAccountId =:accountId")
    suspend fun getIdListAboutAccountId(accountId: String): List<String>

    @Query("SELECT uid FROM $TableName where bookId = :bookId")
    suspend fun getIdListByBookId(bookId: String): List<String>

    @Query("SELECT uid FROM $TableName where categoryId = :categoryId")
    suspend fun getIdListByCategoryId(categoryId: String): List<String>

    @Query("SELECT uid FROM $TableName where reimburseBillId in (:reimburseBillIds)")
    suspend fun getIdListByReimburseBillIds(reimburseBillIds: List<String>): List<String>

    @Query("SELECT reimburseBillId FROM $TableName where uid in (:ids) and reimburseBillId is not null")
    suspend fun getReimburseBillIdListByIds(ids: List<String>): List<String>

    @Insert
    suspend fun insert(value: TallyBillDO)

    @Insert
    suspend fun insertList(value: List<TallyBillDO>)

    @Query("DELETE FROM $TableName where uid in (:targetList)")
    suspend fun deleteByIds(targetList: List<String>)

    @Delete
    suspend fun deleteList(targetList: List<TallyBillDO>)

    @Update
    suspend fun update(target: TallyBillDO)

    @Query("SELECT COUNT(*) FROM $TableName where accountId =:accountId or transferTargetAccountId =:accountId")
    suspend fun getCountAboutAccountId(accountId: String): Int

    @Query("SELECT cost FROM $TableName where isDeleted = 0 and accountId =:accountId")
    suspend fun getAllCostByAccountId(accountId: String): List<Long>

    @Query("SELECT cost FROM $TableName where isDeleted = 0 and transferTargetAccountId =:accountId")
    suspend fun getAllCostByTransferTargetAccountId(accountId: String): List<Long>

    @Query("SELECT count(b.uid) FROM $TableName b where isDeleted = 0 and bookId=:bookId")
    suspend fun getCountByBookId(
        bookId: String,
    ): Int

    // 查询所有的时间, 然后按照每天分组, 然后分页
    @RawQuery
    suspend fun getDayTimeList(
        rawSqlQuery: SupportBillDetailPageQueryImpl
    ): List<Long>

    @RawQuery
    suspend fun queryPageBillDetail(
        condition: SupportBillDetailPageQueryImpl,
    ): List<TallyBillDetailDO>

    @RawQuery
    suspend fun queryBillIdList(
        condition: SupportBillDetailPageQueryImpl,
    ): List<String>

    @RawQuery
    suspend fun getCostList(
        condition: SupportBillDetailPageQueryImpl,
    ): List<Long>

    @RawQuery
    suspend fun getCount(
        condition: SupportBillDetailPageQueryImpl,
    ): Int

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    @Query("SELECT count(*) FROM $TableName where isDeleted = 0")
    fun subscribeCount(): Flow<Long>

}
