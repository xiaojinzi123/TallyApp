package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.getRootParent
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.BillParseService
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService
import com.xiaojinzi.lib.res.dto.NameOfAlipayBillDetail
import java.text.SimpleDateFormat
import java.util.*

@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfAlipayBillDetail],
)
class AlipayBillDetailBillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.Alipay_BILL_Detail

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            // 页面的 12 层
            val contentNode = info
                .getRootParent()
                .findAccessibilityNodeInfosByText("账单详情")
                .first()
                .parent
                .getChild(2)
                .getChild(0)
                .getChild(0)
                .getChild(0)
            // 固定位置取钱的字符串
            val costStr = contentNode.getChild(2).text.toString()
            var noteStr: String? = null
            var targetAccountStr: String? = null
            var timeStr: String? = null
            var orderStr: String? = null
            // 循环子节点判断 name
            for (index in 0 until contentNode.childCount) {
                val tempChild = contentNode.getChild(index)
                when (tempChild.text?.toString()) {
                    "转账备注" -> {
                        noteStr =
                            contentNode.getChild(index + 1).getChild(0).text?.toString()
                    }
                    "对方账户" -> {
                        targetAccountStr =
                            contentNode.getChild(index + 1).getChild(0).text?.toString()
                    }
                    "创建时间" -> {
                        timeStr = contentNode.getChild(index + 1).getChild(0).text?.toString()
                    }
                    "订单号" -> {
                        orderStr = contentNode.getChild(index + 1).getChild(0).text?.toString()
                    }
                }
            }
            val cost = try {
                costStr.toFloat()
            } catch (e: Exception) {
                0f
            }
            val time = try {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(
                    timeStr ?: ""
                )?.time
            } catch (e: Exception) {
                null
            }
            BillParseResultDTO(
                sourceType = sourceType,
                cost = cost,
                time = time,
                note = noteStr ?: "",
                orderNumber = orderStr,
            )
        } catch (e: Exception) {
            LogSupport.d(tag = BillParseService.Tag, content = "支付宝的详情页面获取失败了：${e.message}")
            null
        }
    }
}