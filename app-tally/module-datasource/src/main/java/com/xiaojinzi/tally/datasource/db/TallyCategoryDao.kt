package com.xiaojinzi.tally.datasource.db

import androidx.annotation.CheckResult
import androidx.annotation.Keep
import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.datasource.data.IDatabaseName
import kotlinx.coroutines.flow.Flow

@Keep
@Entity(
    tableName = TallyCategoryDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyCategoryDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 外检 groupId
    @ColumnInfo(name = "groupId") val groupId: String,
    // 如果是内置的是不允许删除的
    @ColumnInfo(name = "isBuiltIn") val isBuiltIn: Boolean = false,
    // icon 必须是 app 内选择的一个 icon. 值是对应的 index
    @ColumnInfo(name = "iconInnerIndex") val iconInnerIndex: Int,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int?,
    // 名称
    @ColumnInfo(name = "name") override val name: String?,
): IDatabaseName

@Keep
data class TallyCategoryWithGroupDO(
    @Embedded
    val category: TallyCategoryDO,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "uid"
    )
    val group: TallyCategoryGroupDO
)

@Dao
interface TallyCategoryDao {

    companion object {
        const val TableName = "tally_category"
    }

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyCategoryDO>

    @Query("SELECT * FROM $TableName")
    suspend fun getAllWithGroup(): List<TallyCategoryWithGroupDO>

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getByUid(uid: String): TallyCategoryDO?

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getDetailByUid(uid: String): TallyCategoryWithGroupDO?

    @Query("SELECT * FROM $TableName where groupId=:groupId")
    suspend fun getByGroupId(groupId: String): List<TallyCategoryDO>

    @Insert
    suspend fun insert(value: TallyCategoryDO)

    @Insert
    suspend fun insertList(values: List<TallyCategoryDO>)

    @Insert
    @CheckResult
    suspend fun insertListGetIds(values: List<TallyCategoryDO>)

    @Update
    suspend fun update(target: TallyCategoryDO): Int

    @Query("DELETE FROM $TableName where uid=:uid")
    suspend fun deleteById(uid: String)

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    @Query("SELECT count(*) FROM $TableName")
    fun subscribeCount(): Flow<Long>

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    @Query("SELECT * FROM $TableName")
    fun subscribeAll(): Flow<List<TallyCategoryDO>>

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    @Query("SELECT c.* FROM $TableName c left join ${TallyCategoryGroupDao.TableName} cg on cg.uid = c.groupId where cg.type =:type")
    fun subscribeCategoriesByGroupType(type: Int): Flow<List<TallyCategoryDO>>

}
