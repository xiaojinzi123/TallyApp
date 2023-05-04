package com.xiaojinzi.tally.datasource.db

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Keep
@Entity(
    tableName = TallyBillStatDao.TableName,
    indices = [
        Index(
            name = "index_tally_bill_stat_rid",
            value = ["rid"],
            unique = true,
        ),
        Index(
            name = "index_tally_bill_stat_categoryId_time",
            value = ["categoryId", "time"],
            unique = false
        )]
)
data class TallyBillStatDO(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rid")
    val rid: Long = 0,
    //类别
    @ColumnInfo(name = "categoryId")
    val categoryId: String,
    //时间
    @ColumnInfo(name = "time")
    val time: Long,
    //记录次数
    @ColumnInfo(name = "useCount", defaultValue = "1")
    val useCount: Long,
    @ColumnInfo(name = "type")
    val type: Int,
)

@Dao
interface TallyBillStatDao {
    companion object {
        const val TableName = "tally_bill_stat"
    }

    @Insert
    suspend fun insert(data: TallyBillStatDO)

    @Insert
    suspend fun insertList(list: List<TallyBillStatDO>)

    @Delete
    suspend fun delete(data: TallyBillStatDO)

    @Query(value = "DELETE FROM $TableName WHERE categoryId = :categoryId")
    suspend fun deleteByCategory(categoryId: String)

    @Query(value = "UPDATE $TableName SET useCount = useCount +1 WHERE rid = :rid AND type=:type")
    suspend fun incrementCount(rid: Long, type: Int)

    @Query(value = "SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyBillStatDO>

    @Query(
        value = "SELECT * FROM `$TableName` WHERE categoryId = :categoryId " +
                "AND strftime('%H',datetime(`time`/1000,'unixepoch'),'localtime') = strftime('%H',datetime(:time/1000,'unixepoch'),'localtime') " +
                "AND `type` = :type"
    )
    suspend fun findCategoryRecord(categoryId: String, time: Long, type: Int): TallyBillStatDO?

    @Query(
        value = "SELECT *, max(`useCount`) from `$TableName` " +
                "WHERE strftime('%H',datetime(`time`/1000,'unixepoch'),'localtime') = strftime('%H',datetime(:time/1000,'unixepoch'),'localtime') " +
                "AND `type` = :type"
    )
    suspend fun getMaxCategory(time: Long = System.currentTimeMillis(), type: Int): TallyBillStatDO?

    @Query(
        value = "SELECT tc.* from $TableName tbs, tally_category tc WHERE tbs.categoryId = tc.uid " +
                "AND strftime('%H',datetime(tbs.time/1000,'unixepoch'),'localtime') = strftime('%H',datetime(:time/1000,'unixepoch'),'localtime') " +
                "AND tbs.type = :type ORDER By tbs.useCount DESC, tbs.time DESC LIMIT 1"
    )
    suspend fun getCategoryList(
        time: Long = System.currentTimeMillis(),
        type: Int
    ): List<TallyCategoryDO>?
}