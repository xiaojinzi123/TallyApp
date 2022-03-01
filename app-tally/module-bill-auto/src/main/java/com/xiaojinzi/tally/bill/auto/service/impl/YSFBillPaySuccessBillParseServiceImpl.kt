package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.getRootParent
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService
import com.xiaojinzi.lib.res.dto.NameOfYSFBillPaySuccess

@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfYSFBillPaySuccess],
)
class YSFBillPaySuccessBillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.YSF_Pay_Success

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .getRootParent()
                .getChild(0)
                .getChild(0)
            val cost = contentNode.getChild(0).text.substring(startIndex = 1).toFloat()
            BillParseResultDTO(
                sourceType = sourceType,
                cost = -cost,
            )
        } catch (e: Exception) {
            null
        }
    }

}