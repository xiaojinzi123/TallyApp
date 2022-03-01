package com.xiaojinzi.tally.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TallyBookDO::class,
        TallyBudgetDO::class,
        TallyImageDO::class,
        TallyCategoryDO::class,
        TallyCategoryGroupDO::class,
        TallyBillAutoSourceAppDO::class,
        TallyBillAutoSourceViewDO::class,
        TallyBillDO::class,
        TallyLabelDO::class,
        TallyBillLabelDO::class,
        TallyAccountDO::class,
        TallyAccountTypeDO::class,
    ],
    views = [
        // TallyMonthlyStatisticalDO::class
    ],
    version = 6,
    exportSchema = false
)
abstract class TallyDatabase : RoomDatabase() {
    abstract fun tallyBookDao(): TallyBookDao
    abstract fun allyBudgetDao(): TallyBudgetDao
    abstract fun tallyImageDao(): TallyImageDao
    abstract fun tallyCategoryDao(): TallyCategoryDao
    abstract fun tallyCategoryGroupDao(): TallyCategoryGroupDao
    abstract fun tallyBillAutoSourceAppDao(): TallyBillAutoSourceAppDao
    abstract fun tallyBillAutoSourceViewDao(): TallyBillAutoSourceViewDao
    abstract fun tallyBillDao(): TallyBillDao
    abstract fun tallyLabelDao(): TallyLabelDao
    abstract fun tallyBillLabelDao(): TallyBillLabelDao
    abstract fun tallyAccountDao(): TallyAccountDao
    abstract fun tallyAccountTypeDao(): TallyAccountTypeDao
    abstract fun tallyMonthlyStatisticalDao(): TallyMonthlyStatisticalDao
}