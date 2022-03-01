package com.xiaojinzi.tally.datasource.db

import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr

@Entity(
    tableName = TallyBillLabelDao.TableName,
    /*foreignKeys = [
        ForeignKey(
            entity = TallyBillDO::class,
            parentColumns = ["uid"],
            childColumns = ["billId"]
        ),
        ForeignKey(
            entity = TallyLabelDO::class,
            parentColumns = ["uid"],
            childColumns = ["labelId"]
        )
    ],*/
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
        Index(
            name = "billId_labelId",
            value = ["billId", "labelId"],
            unique = true,
        ),
    ]
)
data class TallyBillLabelDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = createTime,
    // 两个外键 ID
    @ColumnInfo(name = "billId") val billId: String,
    @ColumnInfo(name = "labelId") val labelId: String,
)

@Dao
interface TallyBillLabelDao {

    companion object {
        const val TableName = "tally_bill_label"
    }

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<TallyBillLabelDO>

    @Query("SELECT * FROM $TableName where billId=:billId and labelId=:labelId")
    suspend fun getByBillIdAndLabelId(billId: String, labelId: String): List<TallyBillLabelDO>

    @Insert
    suspend fun insert(target: TallyBillLabelDO)

    @Insert
    suspend fun insertList(target: List<TallyBillLabelDO>)

    @Query("SELECT * FROM $TableName where uid=:uid")
    suspend fun getById(uid: String): TallyBillLabelDO

    @Transaction
    @Query("DELETE FROM $TableName where billId in (:billIdList)")
    suspend fun deleteByBillIds(billIdList: List<String>)

    @Transaction
    @Query("DELETE FROM $TableName where labelId in (:labelIdList)")
    suspend fun deleteByLabelIds(labelIdList: List<String>)

    @Transaction
    @Query("SELECT billId FROM $TableName where labelId in (:labelIdList) GROUP by billId")
    suspend fun getBillIdListByLabelIds(labelIdList: List<String>): List<String>

}
