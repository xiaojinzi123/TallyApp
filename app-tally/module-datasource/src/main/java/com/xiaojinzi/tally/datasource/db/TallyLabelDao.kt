package com.xiaojinzi.tally.datasource.db

import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.datasource.data.IDatabaseName
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = TallyLabelDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyLabelDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = createTime,
    // 名称的内置 index
    @ColumnInfo(name = "colorInnerIndex") val colorInnerIndex: Int,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int? = null,
    // 名称
    @ColumnInfo(name = "name") override val name: String? = null,
): IDatabaseName

@Dao
interface TallyLabelDao {

    companion object {
        const val TableName = "tally_label"
    }

    @Query("SELECT * FROM $TableName order by createTime desc")
    suspend fun getAll(): List<TallyLabelDO>

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getById(uid: String): TallyLabelDO?

    @Insert
    suspend fun insert(target: TallyLabelDO)

    @Insert
    suspend fun insertList(target: List<TallyLabelDO>)

    @Delete
    suspend fun deleteList(target: List<TallyLabelDO>)

    @Transaction
    @Query("DELETE from $TableName where uid in (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Update
    suspend fun update(target: TallyLabelDO): Int

    /**
     * 订阅查询所有的标签
     */
    @Query("SELECT * FROM $TableName order by createTime desc")
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    fun subscribeAll(): Flow<List<TallyLabelDO>>


}
