package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.lib.res.dto.NameOfYSFBillDetail1
import com.xiaojinzi.module.base.support.getRootParent
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService
import java.text.SimpleDateFormat
import java.util.*

/**
 * 我的-账单-详情
 */
@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfYSFBillDetail1],
)
class YSFBillDetail1BillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.YSF_BILL_Detail1

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .getRootParent()
                .getChild(2)
                .getChild(0)
                .getChild(0)
                .getChild(0)
                .getChild(0)
                .getChild(0)
            val costStr = contentNode.getChild(1).text
            val cost = costStr.substring(startIndex = 0, endIndex = costStr.length - 1).toFloat()
            val time = try {
                val allStr = contentNode.getChild(2).text
                val timeStartIndex = allStr.indexOf("订单时间")
                val timeEndIndex = allStr.indexOf("交易流水号")
                if (timeStartIndex > -1 && timeEndIndex > -1) {
                    val timeStr: String = allStr.substring(startIndex = timeStartIndex + 4, timeEndIndex)
                    SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).parse(timeStr)?.time
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
            BillParseResultDTO(
                sourceType = sourceType,
                cost = -cost,
                time = time,
            )
        } catch (e: Exception) {
            null
        }
    }

}