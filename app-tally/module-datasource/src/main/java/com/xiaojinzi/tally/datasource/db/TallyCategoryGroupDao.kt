package com.xiaojinzi.tally.datasource.db

import androidx.annotation.CheckResult
import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.datasource.data.IDatabaseName
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = TallyCategoryGroupDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyCategoryGroupDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 如果是内置的是不允许删除的
    @ColumnInfo(name = "isBuiltIn") val isBuiltIn: Boolean = false,
    // 类别
    @ColumnInfo(name = "type") val type: Int,
    // icon 必须是 app 内选择的一个 icon. 值是对应的 index
    @ColumnInfo(name = "iconInnerIndex") val iconInnerIndex: Int,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int?,
    // 名称
    @ColumnInfo(name = "name") override val name: String?,
): IDatabaseName

@Dao
interface TallyCategoryGroupDao {

    companion object {
        const val TableName = "tally_category_group"
    }

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyCategoryGroupDO>

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getByUid(uid: String): TallyCategoryGroupDO?

    @Insert
    suspend fun insert(value: TallyCategoryGroupDO)

    @Update
    suspend fun update(value: TallyCategoryGroupDO): Int

    @Insert
    suspend fun insertList(values: List<TallyCategoryGroupDO>)

    @Insert
    @CheckResult
    suspend fun insertListGetIds(values: List<TallyCategoryGroupDO>)

    @Query("DELETE FROM $TableName where uid=:uid")
    suspend fun deleteById(uid: String)

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    @Query("SELECT count(*) FROM $TableName")
    fun subscribeCount(): Flow<Long>

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    @Query("SELECT * FROM $TableName")
    fun subscribeAll(): Flow<List<TallyCategoryGroupDO>>

}
