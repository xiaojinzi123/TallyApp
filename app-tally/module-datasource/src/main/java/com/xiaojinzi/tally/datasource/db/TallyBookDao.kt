package com.xiaojinzi.tally.datasource.db

import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.tally.datasource.data.IDatabaseName
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = TallyBookDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyBookDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long,
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = System.currentTimeMillis(),
    // 是否是默认的账本
    @ColumnInfo(name = "isDefault") val isDefault: Boolean,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int? = null,
    // 名称
    @ColumnInfo(name = "name") override val name: String? = null,
): IDatabaseName

/**
 * 账本的表的数据库操作对象
 */
@Dao
interface TallyBookDao {

    companion object {
        const val TableName = "tally_book"
    }

    @Insert
    suspend fun insertList(targetList: List<TallyBookDO>)

    @Update
    suspend fun update(target: TallyBookDO): Int

    @Transaction
    @Query("Delete from $TableName where uid=:uid")
    suspend fun deleteByUid(uid: String)

    @Query("SELECT * FROM $TableName where isDefault=1")
    suspend fun getDefault(): TallyBookDO?

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getByUid(uid: String): TallyBookDO?

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyBookDO>

    @Query("SELECT * FROM $TableName where isDefault=1")
    fun subscribeDefault(): Flow<TallyBookDO>

    @Query("SELECT * FROM $TableName order by uid")
    fun subscribeAll(): Flow<List<TallyBookDO>>

}
