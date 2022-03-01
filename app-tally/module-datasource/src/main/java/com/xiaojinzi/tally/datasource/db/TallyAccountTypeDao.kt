package com.xiaojinzi.tally.datasource.db

import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.tally.datasource.data.IDatabaseName
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = TallyAccountTypeDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyAccountTypeDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = createTime,
    // 用于排序, 值越大, 优先级越高
    @ColumnInfo(name = "order") val order: Int,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int?,
    // 名称
    @ColumnInfo(name = "name") override val name: String?,
): IDatabaseName

/**
 * 账单的账户的表的数据库操作对象
 */
@Dao
interface TallyAccountTypeDao {

    companion object {
        const val TableName = "tally_account_type"
    }

    @Insert
    suspend fun insert(target: TallyAccountTypeDO)

    @Insert
    suspend fun insertList(targetList: List<TallyAccountTypeDO>)

    @Update
    suspend fun update(target: TallyAccountTypeDO): Int

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyAccountTypeDO>

    @Query("SELECT * FROM $TableName where uid =:uid")
    suspend fun getByUid(uid: String): TallyAccountTypeDO?

    @Query("SELECT * FROM $TableName")
    fun subscribeAll(): Flow<List<TallyAccountTypeDO>>


}
