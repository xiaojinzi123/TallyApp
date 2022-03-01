package com.xiaojinzi.tally.datasource.db

import androidx.annotation.Keep
import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.tally.datasource.data.IDatabaseName

@Entity(
    tableName = TallyBillAutoSourceAppDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyBillAutoSourceAppDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = System.currentTimeMillis(),
    /**
     * 来源的界面 [AutoBillSourceAppType]
     */
    @ColumnInfo(name = "sourceAppType") val sourceAppType: Int,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int? = null,
    // 名称
    @ColumnInfo(name = "name") override val name: String? = null,
    // 账户 ID
    @ColumnInfo(name = "accountId") val accountId: String? = null,
): IDatabaseName

@Keep
data class TallyBillAutoSourceAppDetailDO(
    @Embedded
    val core: TallyBillAutoSourceAppDO,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "uid",
        entity = TallyAccountDO::class,
    )
    val account: TallyAccountDO?,
)

@Dao
interface TallyBillAutoSourceAppDao {

    companion object {
        const val TableName = "tally_bill_auto_source_app"
        const val SELECT_ALL =
            "select * from $TableName"
        const val SELECT_BY_ID =
            "select * from $TableName where uid = :uid"
        const val SELECT_BY_SOURCE_TYPE =
            "select * from $TableName where sourceAppType = :sourceAppType"
    }

    @Insert
    suspend fun insert(target: TallyBillAutoSourceAppDO)

    @Insert
    suspend fun insertList(targetList: List<TallyBillAutoSourceAppDO>)

    @Delete
    suspend fun delete(target: TallyBillAutoSourceAppDO)

    @Update
    suspend fun update(target: TallyBillAutoSourceAppDO): Int

    @Query(SELECT_ALL)
    suspend fun getAll(): List<TallyBillAutoSourceAppDO>

    @Query(SELECT_ALL)
    suspend fun getAllDetail(): List<TallyBillAutoSourceAppDetailDO>

    @Query(SELECT_BY_ID)
    suspend fun getById(uid: String): TallyBillAutoSourceAppDO?

    @Query(SELECT_BY_ID)
    suspend fun getDetailById(uid: String): TallyBillAutoSourceAppDetailDO?

    @Query(SELECT_BY_SOURCE_TYPE)
    suspend fun getDetailBySourceType(sourceAppType: Int): TallyBillAutoSourceAppDetailDO?

}