package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.lib.res.dto.NameOfYSFBillTransferSuccess
import com.xiaojinzi.module.base.support.getRootParent
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService

@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfYSFBillTransferSuccess],
)
class YSFBillTransferSuccessBillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.YSF_Transfer_Success

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .getRootParent()
                .getChild(0)
                .getChild(2)
            val costStr = contentNode.getChild(0).text
            val cost = -costStr.substring(startIndex = 1).toFloat()
            BillParseResultDTO(
                sourceType = sourceType,
                cost = cost,
            )
        } catch (e: Exception) {
            null
        }
    }

}