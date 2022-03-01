package com.xiaojinzi.module.base.support

import java.util.*

/**
 * 获取时间戳里面的小时
 */
fun getHourByTimeStamp(timeStamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.HOUR_OF_DAY)
}

/**
 * 获取时间戳里面的小时
 */
fun getHour1ByTimeStamp(timeStamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.HOUR)
}

/**
 * 获取时间戳里面的分钟
 */
fun getMinuteByTimeStamp(timeStamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.MINUTE)
}

/**
 * 获取时间戳里面的秒
 */
fun getSecondByTimeStamp(timeStamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.SECOND)
}

/**
 * 获取是一周的第几天
 */
fun getDayOfWeek(timeStamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.DAY_OF_WEEK)
}

/**
 * 获取是第几个月, 第一个月是 0
 */
fun getMonthByTimeStamp(timeStamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.MONTH)
}

/**
 * 获取是第几年
 */
fun getYearByTimeStamp(timeStamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.YEAR)
}

/**
 * 获取此时间戳的当天的起始时间
 */
fun getDayInterval(timeStamp: Long): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.MILLISECOND] = 0
    val start = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_MONTH, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val end = calendar.timeInMillis
    return Pair(
        first = start,
        second = end,
    )
}

fun isSameDay(timeStamp1: Long, timeStamp2: Long) =
    getDayInterval(timeStamp = timeStamp1) == getDayInterval(timeStamp = timeStamp2)


/**
 * 获取此时间戳的当月的起始时间. 左右都是包含的
 */
fun getMonthInterval(timeStamp: Long, monthOffset: Int = 0): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    calendar[Calendar.DAY_OF_MONTH] = 1
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    calendar.add(Calendar.MONTH, monthOffset)
    val start = calendar.timeInMillis
    // 月份调整到下个月, 然后毫秒值 -1, 这样就定位到这个月的最后一毫秒了
    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val end = calendar.timeInMillis
    return Pair(
        first = start,
        second = end,
    )
}

/**
 * 获取这个时间戳这个月的开始时间
 */
fun getMonthStartTime(timeStamp: Long, monthOffset: Int = 0) =
    getMonthInterval(timeStamp = timeStamp, monthOffset = monthOffset).first

/**
 * 获取此时间戳的当月的起始时间.
 */
fun getYearInterval(timeStamp: Long, yearOffset: Int = 0): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    // 这个不用了, 多余了, 因为下面的 DAY_OF_YEAR 会设置到一年的第一天
    // calendar[Calendar.MONTH] = 0
    calendar[Calendar.DAY_OF_YEAR] = 1
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    calendar.add(Calendar.YEAR, yearOffset)
    val start = calendar.timeInMillis
    // 月份调整到下个月, 然后毫秒值 -1, 这样就定位到这个月的最后一毫秒了
    calendar.add(Calendar.YEAR, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val end = calendar.timeInMillis
    return Pair(
        first = start,
        second = end,
    )
}

/**
 * 获取这个时间戳这个年的开始时间
 */
fun getYearStartTime(timeStamp: Long, yearOffset: Int = 0) =
    getYearInterval(timeStamp = timeStamp, yearOffset = yearOffset).first
