package com.xiaojinzi.tally.datasource.db

import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr

@Entity(
    tableName = TallyImageDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyImageDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = createTime,
    // 远程地址
    @ColumnInfo(name = "url") val url: String,
    // 远程地址
    @ColumnInfo(name = "key1") val key1: String? = null,
)

/**
 * 账单的账户的表的数据库操作对象
 */
@Dao
interface TallyImageDao {

    companion object {
        const val TableName = "tally_image"
    }

    @Insert
    suspend fun insert(target: TallyImageDO)

    @Insert
    suspend fun insertList(targetList: List<TallyImageDO>)

    @Query("DELETE FROM $TableName where key1 = :key1")
    suspend fun deleteByKey1(key1: String): Int

    @Delete
    suspend fun delete(target: TallyImageDO)

    @Query("DELETE FROM $TableName where uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyImageDO>

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getById(uid: String): TallyImageDO?

    @Query("SELECT * FROM $TableName where key1=:key1")
    suspend fun getByKey1(key1: String): List<TallyImageDO>

    @Update
    suspend fun update(target: TallyImageDO): Int

}
