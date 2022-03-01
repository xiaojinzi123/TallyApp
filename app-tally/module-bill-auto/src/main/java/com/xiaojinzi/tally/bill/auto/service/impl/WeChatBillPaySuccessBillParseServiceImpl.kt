package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.getRootParent
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService
import com.xiaojinzi.lib.res.dto.NameOfWeChatBillPaySuccess

@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfWeChatBillPaySuccess],
)
class WeChatBillPaySuccessBillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.WeChat_Pay_Success

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .getRootParent()
                .findAccessibilityNodeInfosByText("支付成功")
                .first()
                .parent
            val cost = contentNode.getChild(2).text.toString().substring(startIndex = 1).toFloat()
            BillParseResultDTO(
                sourceType = sourceType,
                cost = -cost,
            )
        } catch (e: Exception) {
            null
        }
    }

}