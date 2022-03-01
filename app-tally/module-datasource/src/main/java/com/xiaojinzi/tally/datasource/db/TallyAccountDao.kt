package com.xiaojinzi.tally.datasource.db

import androidx.annotation.Keep
import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.tally.datasource.data.IDatabaseName
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = TallyAccountDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyAccountDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = createTime,
    // 账户类型的 Id. 比如资金账户、信贷账户、充值账户、理财账户
    @ColumnInfo(name = "typeId") val typeId: String,
    // 是否是默认的
    @ColumnInfo(name = "isDefault")  val isDefault: Boolean,
    // icon 必须是 app 内选择的一个 icon. 值是对应的 index
    @ColumnInfo(name = "iconInnerIndex") val iconInnerIndex: Int,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int? = null,
    // 名称
    @ColumnInfo(name = "name") override val name: String? = null,
    // 初始余额
    @ColumnInfo(name = "initialBalance") val initialBalance: Long,
    // 当前余额, 这个值是被动更新的
    @ColumnInfo(name = "balance") val balance: Long,
): IDatabaseName

@Keep
data class TallyAccountWithTypeDO(
    @Embedded
    val account: TallyAccountDO,
    @Relation(
        parentColumn = "typeId",
        entityColumn = "uid"
    )
    val accountType: TallyAccountTypeDO,
)

/**
 * 账单的账户的表的数据库操作对象
 */
@Dao
interface TallyAccountDao {

    companion object {
        const val TableName = "tally_account"
    }

    @Insert
    suspend fun insert(target: TallyAccountDO)

    @Insert
    suspend fun insertList(targetList: List<TallyAccountDO>)

    @Delete
    suspend fun delete(target: TallyAccountDO)

    @Query("DELETE FROM $TableName where uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyAccountDO>

    @Query("SELECT * FROM $TableName where isDefault=1")
    suspend fun getDefault(): List<TallyAccountDO>

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getById(uid: String): TallyAccountDO?

    @Update
    suspend fun update(target: TallyAccountDO): Int

    @Query("SELECT * FROM $TableName")
    fun subscribeAll(): Flow<List<TallyAccountDO>>

    @Query("SELECT * FROM $TableName")
    fun subscribeAllWithType(): Flow<List<TallyAccountWithTypeDO>>

    @Query("SELECT * FROM $TableName where isDefault=1")
    fun subscribeDefault(): Flow<List<TallyAccountDO>>

}
