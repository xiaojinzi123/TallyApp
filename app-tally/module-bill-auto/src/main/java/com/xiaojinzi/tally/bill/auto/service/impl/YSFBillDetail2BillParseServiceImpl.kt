package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.lib.res.dto.NameOfYSFBillDetail2
import com.xiaojinzi.module.base.support.getRootParent
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService
import java.text.SimpleDateFormat
import java.util.*

/**
 * 消息-交易详情
 */
@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfYSFBillDetail2],
)
class YSFBillDetail2BillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.YSF_BILL_Detail2

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .getRootParent()
                .getChild(0)
                .getChild(2)
            var costSpendingStr: String? = null
            var timeStr: String? = null
            val childCount = contentNode.childCount
            for (index in 0 until childCount) {
                val childNode = contentNode.getChild(index)
                when (childNode.text?.toString()) {
                    "时间" -> {
                        timeStr = contentNode.getChild(index + 1).text.toString()
                    }
                    "支出金额" -> {
                        costSpendingStr = contentNode.getChild(index + 1).text.toString()
                    }
                }
            }
            val cost = if (costSpendingStr != null) {
                -costSpendingStr.substring(startIndex = 0, costSpendingStr.lastIndex).toFloat()
            } else {
                notSupportError()
            }
            val time = timeStr?.let {
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    Locale.getDefault()
                ).parse(it)?.time
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