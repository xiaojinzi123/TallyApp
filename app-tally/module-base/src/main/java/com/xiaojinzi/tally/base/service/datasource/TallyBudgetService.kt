package com.xiaojinzi.tally.base.service.datasource

import android.content.Context
import androidx.annotation.Keep
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

@Keep
data class TallyBudgetInsertDTO(
    val monthTime: Long,
    // 预算的值
    val value: Long,
)

@Keep
data class TallyBudgetDTO(
    val uid: String,
    val createTime: Long,
    // 月份的时间
    val monthTime: Long,
    // 预算的值
    val value: Long,
)

/**
 * 预算的 Service
 */
interface TallyBudgetService {

    companion object {
        val MONTH_TIME_FORMAT: SimpleDateFormat
            get() = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    }

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isCumulativeBudgetObservableDTO: MutableSharedStateFlow<Boolean>

    /**
     * 每月默认的预算, 可能是 Null 表示没有设置
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val monthlyDefaultBudgetObservableDTO: MutableSharedStateFlow<Long?>

    /**
     * 是否设置了默认预算
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isSetMonthlyDefaultBudgetObservableDTO: Flow<Boolean>

    /**
     * 插入一个预算
     */
    suspend fun insert(target: TallyBudgetInsertDTO)

    /**
     * 删除
     */
    suspend fun delete(target: TallyBudgetDTO)

    /**
     * 插入或者更新数据
     */
    suspend fun insertOrUpdate(target: TallyBudgetInsertDTO)

    /**
     * 根据时间或者这个月的预算
     */
    suspend fun getByMonthTime(monthTime: Long): TallyBudgetDTO?

    /**
     * 获取某个时间点之前的所有预算设置
     */
    suspend fun getAllBeforeSpecialTime(monthTime: Long): List<TallyBudgetDTO>

    /**
     * 获取某个月的时间点之前的预算结余
     * null 表示没有
     */
    suspend fun getBudgetBalanceBeforeSpecialTime(monthTime: Long): Long?

    /**
     * 获取参数对应的月份的预算
     */
    suspend fun getMonthBudgetValue(monthTime: Long): Long?

    /**
     * 获取参数对应的月份的结余预算
     * 月份的预算 + 之前月份的结余
     */
    suspend fun getMonthBudgetBalanceValue(monthTime: Long): Long?

}