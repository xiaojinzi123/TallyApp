package com.xiaojinzi.lib.res.dto

const val NameOfAlipayBillDetail = "alipayBillDetail"
const val NameOfAlipayBillPaySuccess = "alipayBillPaySuccess"
const val NameOfAlipayBillTransferSuccess = "alipayBillTransferSuccess"
const val NameOfWeChatBillDetail = "wechatBillDetail"
const val NameOfWeChatBillPaySuccess = "weChatBillPaySuccess"
const val NameOfYSFBillDetail1 = "ysfBillDetail1"
const val NameOfYSFBillDetail2 = "ysfBillDetail2"
const val NameOfYSFBillDetail3 = "ysfBillDetail3"
const val NameOfYSFBillPaySuccess = "ysfBillPaySuccess"
const val NameOfYSFBillTransferSuccess = "ysfBillTransferSuccess"

/**
 * 所有的值定义之后就不可以被改变了 !!!!!!!!!!!!
 */
enum class AutoBillSourceViewType(
    val sourceAppType: AutoBillSourceAppType,
    val dbValue: Int,
    val sourceName: String
) {
    // 支付宝的账单详情
    Alipay_BILL_Detail(
        sourceAppType = AutoBillSourceAppType.Alipay,
        dbValue = 1,
        sourceName = NameOfAlipayBillDetail
    ),

    // 支付宝的支付成功
    Alipay_Pay_Success(
        sourceAppType = AutoBillSourceAppType.Alipay,
        dbValue = 2,
        sourceName = NameOfAlipayBillPaySuccess
    ),

    // 支付宝的转账成功
    Alipay_Transfer_Success(
        sourceAppType = AutoBillSourceAppType.Alipay,
        dbValue = 3,
        sourceName = NameOfAlipayBillTransferSuccess
    ),

    // 微信的账单详情
    WeChat_BILL_Detail(
        sourceAppType = AutoBillSourceAppType.WeChat,
        dbValue = 200,
        sourceName = NameOfWeChatBillDetail
    ),

    // 微信的支付成功
    WeChat_Pay_Success(
        sourceAppType = AutoBillSourceAppType.WeChat,
        dbValue = 202,
        sourceName = NameOfWeChatBillPaySuccess
    ),

    // 云闪付的账单详情
    YSF_BILL_Detail1(
        sourceAppType = AutoBillSourceAppType.YSF,
        dbValue = 300,
        sourceName = NameOfYSFBillDetail1
    ),

    // 云闪付的消息-交易详情
    YSF_BILL_Detail2(
        sourceAppType = AutoBillSourceAppType.YSF,
        dbValue = 301,
        sourceName = NameOfYSFBillDetail2
    ),

    // 云闪付的消息-账单详情
    YSF_BILL_Detail3(
        sourceAppType = AutoBillSourceAppType.YSF,
        dbValue = 302,
        sourceName = NameOfYSFBillDetail3
    ),

    // 云闪付的支付成功
    YSF_Pay_Success(
        sourceAppType = AutoBillSourceAppType.YSF,
        dbValue = 310,
        sourceName = NameOfYSFBillPaySuccess
    ),

    // 云闪付的转账成功
    YSF_Transfer_Success(
        sourceAppType = AutoBillSourceAppType.YSF,
        dbValue = 320,
        sourceName = NameOfYSFBillTransferSuccess
    ),

    ;

    companion object {
        fun fromValue(value: Int): AutoBillSourceViewType {
            return when (value) {
                Alipay_BILL_Detail.dbValue -> Alipay_BILL_Detail
                Alipay_Pay_Success.dbValue -> Alipay_Pay_Success
                Alipay_Transfer_Success.dbValue -> Alipay_Transfer_Success
                WeChat_BILL_Detail.dbValue -> WeChat_BILL_Detail
                WeChat_Pay_Success.dbValue -> WeChat_Pay_Success
                YSF_BILL_Detail1.dbValue -> YSF_BILL_Detail1
                YSF_BILL_Detail2.dbValue -> YSF_BILL_Detail2
                YSF_BILL_Detail3.dbValue -> YSF_BILL_Detail3
                YSF_Pay_Success.dbValue -> YSF_Pay_Success
                YSF_Transfer_Success.dbValue -> YSF_Transfer_Success
                else -> error("Not support")
            }
        }
    }
}