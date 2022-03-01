package com.xiaojinzi.tally.datasource.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration1_2 : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `tally_budget` (`uid` TEXT NOT NULL, `createTime` INTEGER NOT NULL, `modifyTime` INTEGER NOT NULL, `value` INTEGER NOT NULL, `month` TEXT NOT NULL, PRIMARY KEY(`uid`))")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tally_budget_uid` ON `tally_budget` (`uid`)")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tally_budget_month` ON `tally_budget` (`month`)")
    }

}

class Migration2_3 : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // 账单表增加一个字段
        database.execSQL("alter table ${TallyBillDao.TableName} add isNotIncludedInIncomeAndExpenditure INTEGER NOT NULL DEFAULT 0")
    }

}

class Migration3_4 : Migration(3, 4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // 账单表增加一个字段
        database.execSQL("alter table ${TallyBillDao.TableName} add costAdjust INTEGER NOT NULL DEFAULT 0")
    }

}

class Migration4_5 : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // 账单表增加一个字段
        database.execSQL("alter table ${TallyBillDao.TableName} add costAdjust INTEGER NOT NULL DEFAULT 0")
        database.execSQL("update ${TallyBillDao.TableName} set costAdjust = cost")

        // 数据迁移
        val billIdList = mutableListOf<String>()
        database
            .query("select distinct t.reimburseBillId from tally_bill t where t.reimburseBillId is not null")
            .use { c ->
                while (c.moveToNext()) {
                    billIdList.add(c.getString(0))
                }
            }

        billIdList
            .map { billId ->
                val billReimburseOffsetCost = database
                    .query("select sum(cost) from ${TallyBillDao.TableName} where reimburseBillId = '$billId'")
                    .use { c ->
                        c.moveToFirst()
                        c.getLong(0)
                    }
                billId to billReimburseOffsetCost
            }
            .forEach { pairItem ->
                database.execSQL("update ${TallyBillDao.TableName} set costAdjust = cost + ${pairItem.second} where uid = '${pairItem.first}'")
            }


    }

}

class Migration5_6 : Migration(5, 6) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // 账单表增加一个字段
        database.execSQL("alter table ${TallyBillDao.TableName} add usage INTEGER NOT NULL DEFAULT 0")
    }

}