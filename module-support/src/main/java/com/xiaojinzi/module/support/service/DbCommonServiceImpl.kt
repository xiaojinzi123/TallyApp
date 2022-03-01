package com.xiaojinzi.module.support.service

import androidx.annotation.Keep
import androidx.room.*
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.DbCommonService
import com.xiaojinzi.support.ktx.app

@Keep
@Entity(
    tableName = DBCommonDao.TableName,
    indices = [
        Index(value = ["key"], unique = true)
    ]
)
data class DbCommonDO(
    @PrimaryKey(autoGenerate = true) var _id: Long? = null,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "intValue") val intValue: Int? = null,
    @ColumnInfo(name = "longValue") val longValue: Long? = null,
    @ColumnInfo(name = "floatValue") val floatValue: Float? = null,
    @ColumnInfo(name = "doubleValue") val doubleValue: Double? = null,
    @ColumnInfo(name = "boolValue") val boolValue: Boolean? = null,
    @ColumnInfo(name = "stringValue") val stringValue: String? = null
)

@Dao
interface DBCommonDao {

    companion object {
        const val TableName = "db_common"
    }

    @Query("SELECT * FROM $TableName")
    suspend fun getAll(): List<DbCommonDO>

    @Query("SELECT * FROM $TableName WHERE `key`=:key")
    suspend fun getByKey(key: String): DbCommonDO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateForKey(target: DbCommonDO)

    @Delete
    suspend fun delete(target: DbCommonDO)

    /**
     * 删除所有
     */
    @Query("DELETE FROM $TableName")
    suspend fun deleteAll()

}

/**
 * 升级版本会删除全部数据
 */
@Database(entities = [DbCommonDO::class], version = 1)
abstract class DbCommonDatabase : RoomDatabase() {
    abstract fun dBCommonDao(): DBCommonDao
}

@ServiceAnno(DbCommonService::class)
class DbCommonServiceImpl : DbCommonService {

    private val dbCommonDatabase = Room
        .databaseBuilder(app, DbCommonDatabase::class.java, "dbCommon.db")
        .fallbackToDestructiveMigration()
        .build()

    override suspend fun saveString(key: String, value: String?) {
        dbCommonDatabase
            .dBCommonDao()
            .insertOrUpdateForKey(
                target = DbCommonDO(key = key, stringValue = value)
            )
    }

    override suspend fun saveInt(key: String, value: Int?) {
        dbCommonDatabase
            .dBCommonDao()
            .insertOrUpdateForKey(
                target = DbCommonDO(key = key, intValue = value)
            )
    }

    override suspend fun saveLong(key: String, value: Long?) {
        dbCommonDatabase
            .dBCommonDao()
            .insertOrUpdateForKey(
                target = DbCommonDO(key = key, longValue = value)
            )
    }

    override suspend fun saveFloat(key: String, value: Float?) {
        dbCommonDatabase
            .dBCommonDao()
            .insertOrUpdateForKey(
                target = DbCommonDO(key = key, floatValue = value)
            )
    }

    override suspend fun saveDouble(key: String, value: Double?) {
        dbCommonDatabase
            .dBCommonDao()
            .insertOrUpdateForKey(
                target = DbCommonDO(key = key, doubleValue = value)
            )
    }

    override suspend fun saveBoolean(key: String, value: Boolean?) {
        dbCommonDatabase
            .dBCommonDao()
            .insertOrUpdateForKey(
                target = DbCommonDO(key = key, boolValue = value)
            )
    }

    override suspend fun getString(
        key: String,
    ): String? {
        return dbCommonDatabase
            .dBCommonDao()
            .getByKey(key = key)
            ?.stringValue
    }

    override suspend fun getInt(
        key: String,
    ): Int? {
        return dbCommonDatabase
            .dBCommonDao()
            .getByKey(key = key)
            ?.intValue
    }

    override suspend fun getLong(
        key: String,
    ): Long? {
        return dbCommonDatabase
            .dBCommonDao()
            .getByKey(key = key)
            ?.longValue
    }

    override suspend fun getFloat(
        key: String,
    ): Float? {
        return dbCommonDatabase
            .dBCommonDao()
            .getByKey(key = key)
            ?.floatValue
    }

    override suspend fun getDouble(
        key: String,
    ): Double? {
        return dbCommonDatabase
            .dBCommonDao()
            .getByKey(key = key)
            ?.doubleValue
    }

    override suspend fun getBoolean(
        key: String,
    ): Boolean? {
        return dbCommonDatabase
            .dBCommonDao()
            .getByKey(key = key)
            ?.boolValue
    }



}