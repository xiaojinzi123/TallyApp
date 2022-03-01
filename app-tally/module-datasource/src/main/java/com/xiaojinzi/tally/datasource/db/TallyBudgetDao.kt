package com.xiaojinzi.tally.datasource.db

import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr

@Entity(
    tableName = TallyBudgetDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
        Index(
            value = ["month"],
            unique = true
        ),
    ]
)
data class TallyBudgetDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long,
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = System.currentTimeMillis(),
    // 预算的值
    @ColumnInfo(name = "value") val  value: Long,
    // 月份
    @ColumnInfo(name = "month") val  month: String,

)

/**
 * 账单的账户的表的数据库操作对象
 */
@Dao
interface TallyBudgetDao {

    companion object {
        const val TableName = "tally_budget"
    }

    @Insert
    suspend fun insert(target: TallyBudgetDO)

    @Insert
    suspend fun insertList(targetList: List<TallyBudgetDO>)

    @Delete
    suspend fun delete(target: TallyBudgetDO)

    @Query("DELETE FROM $TableName where uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyBudgetDO>

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getById(uid: String): TallyBudgetDO?

    @Query("SELECT * FROM $TableName where month=:month")
    suspend fun getByMonth(month: String): TallyBudgetDO?

    @Query("SELECT * FROM $TableName where month<:month")
    suspend fun getAllBeforeSpecialTime(month: String): List<TallyBudgetDO>

    @Update
    suspend fun update(target: TallyBudgetDO): Int

}
