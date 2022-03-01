package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService
import com.xiaojinzi.lib.res.dto.NameOfAlipayBillPaySuccess

@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfAlipayBillPaySuccess],
)
class AlipayBillPaySuccessBillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.Alipay_Pay_Success

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .findAccessibilityNodeInfosByText("支付成功")
                .first()
                .parent
            val cost = contentNode.getChild(1).text.toString().toFloat()
            BillParseResultDTO(
                sourceType = sourceType,
                cost = -cost,
            )
        } catch (e: Exception) {
            null
        }
    }

}