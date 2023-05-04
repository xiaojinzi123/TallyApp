package com.xiaojinzi.tally.datasource.db

import androidx.room.Room
import com.xiaojinzi.support.ktx.app

const val DATA_BASE_UPGRADE = "dataBaseUpgrade"

val tableNamesOfAccount = listOf(
    TallyAccountDao.TableName,
    TallyAccountTypeDao.TableName,
)

val totalTableNames = listOf(
    TallyAccountDao.TableName,
    TallyAccountTypeDao.TableName,
    TallyBillAutoSourceAppDao.TableName,
    TallyBillAutoSourceViewDao.TableName,
    TallyBillDao.TableName,
    TallyBillLabelDao.TableName,
    TallyBookDao.TableName,
    TallyCategoryDao.TableName,
    TallyCategoryGroupDao.TableName,
    TallyLabelDao.TableName,
    TallyImageDao.TableName,
    TallyBudgetDao.TableName,
    TallyBillStatDao.TableName,
)

val dbTally by lazy {
    Room
        .databaseBuilder(
            app,
            TallyDatabase::class.java, "tally"
        )
        .addMigrations(
            Migration1_2(),
            Migration2_3(),
            Migration3_4(),
            Migration4_5(),
            Migration5_6(),
            Migration6_7(),
        )
        .fallbackToDestructiveMigration()
        .build()
}