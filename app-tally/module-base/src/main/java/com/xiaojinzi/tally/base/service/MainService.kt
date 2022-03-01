package com.xiaojinzi.tally.base.service

import androidx.annotation.IntRange
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.annotation.HotObservable
import kotlinx.coroutines.flow.Flow

enum class TabType(val tabIndex: Int) {
    Home(tabIndex = 0), Calendar(tabIndex = 1), Statistical(tabIndex = 2), My(tabIndex = 3);
    companion object {
        fun fromIndex(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) index: Int): TabType {
            return when (index) {
                0 -> Home
                1 -> Calendar
                2 -> Statistical
                3 -> My
                else -> notSupportError()
            }
        }
    }
}

interface MainService {

    /**
     * 主界面的 tab 的控制
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val tabObservable: MutableSharedStateFlow<TabType>

    /**
     * 是否显示祝贺的弹框
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isCongratulationObservableDTO: Flow<Boolean>

    /**
     * 标记已经祝贺了
     */
    fun flagCongratulation()

}