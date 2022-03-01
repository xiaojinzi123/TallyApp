package com.xiaojinzi.lib.res.dto


/**
 * 所有的值定义之后就不可以被改变了 !!!!!!!!!!!!
 */
enum class AutoBillSourceAppType(
    val dbValue: Int,
) {
    // 支付宝
    Alipay(dbValue = 1),

    // 微信的账单详情
    WeChat(dbValue = 2),

    // 云闪付
    YSF(dbValue = 3),

    ;

    companion object {
        fun fromValue(value: Int): AutoBillSourceAppType {
            return when (value) {
                Alipay.dbValue -> Alipay
                WeChat.dbValue -> WeChat
                YSF.dbValue -> YSF
                else -> error("Not support")
            }
        }
    }
}