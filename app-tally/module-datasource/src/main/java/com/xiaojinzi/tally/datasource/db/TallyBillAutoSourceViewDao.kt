package com.xiaojinzi.tally.datasource.db

import androidx.annotation.Keep
import androidx.room.*
import com.xiaojinzi.module.base.support.generateUniqueStr
import com.xiaojinzi.tally.datasource.data.IDatabaseName

@Entity(
    tableName = TallyBillAutoSourceViewDao.TableName,
    indices = [
        Index(
            value = ["uid"],
            unique = true
        ),
    ]
)
data class TallyBillAutoSourceViewDO(
    // 全宇宙唯一的 string
    @PrimaryKey()
    @ColumnInfo(name = "uid")
    val uid: String = generateUniqueStr(),
    // 创建的时间
    @ColumnInfo(name = "createTime") val createTime: Long = System.currentTimeMillis(),
    // 修改的时间
    @ColumnInfo(name = "modifyTime") val modifyTime: Long = System.currentTimeMillis(),
    // 来源的 App
    @ColumnInfo(name = "sourceAppId") val sourceAppId: String,
    /**
     * 来源的界面 [AutoBillSourceViewType]
     */
    @ColumnInfo(name = "sourceViewType") val sourceViewType: Int,
    // 名称的内置 index
    @ColumnInfo(name = "nameInnerIndex") override val nameInnerIndex: Int? = null,
    // 名称
    @ColumnInfo(name = "name") override val name: String? = null,
    // 账户 ID
    @ColumnInfo(name = "accountId") val accountId: String?,
    // 类别 ID
    @ColumnInfo(name = "categoryId") val categoryId: String?,
): IDatabaseName

@Keep
data class TallyBillAutoSourceViewDetailDO(
    @Embedded
    val core: TallyBillAutoSourceViewDO,
    @Relation(
        parentColumn = "sourceAppId",
        entityColumn = "uid",
        entity = TallyBillAutoSourceAppDO::class,
    )
    val sourceApp: TallyBillAutoSourceAppDO,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "uid",
        entity = TallyCategoryDO::class,
    )
    val cate: TallyCategoryDO?,
)

@Dao
interface TallyBillAutoSourceViewDao {

    companion object {
        const val TableName = "tally_bill_auto_source_view"
        const val SELECT_ALL =
            "select * from ${TallyBillAutoSourceViewDao.TableName}"
        const val SELECT_BY_ID =
            "select * from ${TallyBillAutoSourceViewDao.TableName} where uid = :uid"
        const val SELECT_BY_SOURCE_VIEW_TYPE =
            "select * from ${TallyBillAutoSourceViewDao.TableName} where sourceViewType = :sourceViewType"
    }

    @Insert
    suspend fun insert(target: TallyBillAutoSourceViewDO)

    @Insert
    suspend fun insertList(targetList: List<TallyBillAutoSourceViewDO>)

    @Delete
    suspend fun delete(target: TallyBillAutoSourceViewDO)

    @Update
    suspend fun update(target: TallyBillAutoSourceViewDO): Int

    @Query(SELECT_ALL)
    suspend fun getAllDetail(): List<TallyBillAutoSourceViewDetailDO>

    @Query(SELECT_BY_ID)
    suspend fun getById(uid: String): TallyBillAutoSourceViewDO?

    @Query(SELECT_BY_ID)
    suspend fun getDetailById(uid: String): TallyBillAutoSourceViewDetailDO?

    @Query(SELECT_BY_SOURCE_VIEW_TYPE)
    suspend fun getDetailById(sourceViewType: Int): TallyBillAutoSourceViewDetailDO?

}