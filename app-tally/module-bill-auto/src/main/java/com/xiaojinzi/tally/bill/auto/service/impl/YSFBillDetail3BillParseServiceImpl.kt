package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.lib.res.dto.NameOfYSFBillDetail3
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
    name = [NameOfYSFBillDetail3],
)
class YSFBillDetail3BillParseServiceImpl : SubBillParseService {

    private val numberList = (48..57).map { it.toChar() } + listOf('-', '.')

    override val sourceType: ParseSourceType = ParseSourceType.YSF_BILL_Detail3

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .getRootParent()
                .getChild(0)
                .getChild(3)
            var costSpendingStr: String = contentNode.getChild(0).text.toString()
                .filter { itemChar -> itemChar in numberList }
            var timeStr: String? = null
            val childCount = contentNode.childCount
            for (index in 0 until childCount) {
                val childNode = contentNode.getChild(index)
                when (childNode.text?.toString()) {
                    "创建时间" -> {
                        timeStr = contentNode.getChild(index + 1).text.toString()
                    }
                }
            }
            val cost = costSpendingStr
                .substring(startIndex = 0, costSpendingStr.lastIndex)
                .toFloat()
            val time = timeStr?.let {
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).parse(it)?.time
            }
            BillParseResultDTO(
                sourceType = sourceType,
                cost = cost,
                time = time,
            )
        } catch (e: Exception) {
            null
        }
    }

}