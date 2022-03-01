package com.xiaojinzi.lib.res.dto

enum class AppSourceType(val value: Int) {
    Alipay(1),
    Wechat(2);
    companion object {
        fun fromValue(value: Int) {
            when(value) {
                1 -> Alipay
                2 -> Wechat
            }
        }
    }
}