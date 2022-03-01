package com.xiaojinzi.lib.res.vo

/**
 * 视图状态
 */
enum class ViewState {
    Normal, Loading, Success, Error;
    fun isBusy(): Boolean {
        return this in listOf(Loading)
    }
    fun toUIString(): String {
        return when(this) {
            Normal -> "Normal"
            Loading -> "Loading"
            Success -> "Success"
            Error -> "Error"
        }
    }
}