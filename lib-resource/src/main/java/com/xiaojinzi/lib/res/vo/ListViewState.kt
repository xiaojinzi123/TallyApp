package com.xiaojinzi.lib.res.vo

/**
 * 列表视图状态
 */
enum class ListViewState {
    Normal, Refresh, LoadMore, LoadMoreFail, LoadMoreEnd;

    fun isBusy(): Boolean {
        return this in listOf(Refresh, LoadMore)
    }

    fun toUIString(): String {
        return when(this) {
            Normal -> "Normal"
            Refresh -> "Refresh"
            LoadMore -> "LoadMore"
            LoadMoreFail -> "LoadMoreFail"
            LoadMoreEnd -> "LoadMoreEnd"
        }
    }
}