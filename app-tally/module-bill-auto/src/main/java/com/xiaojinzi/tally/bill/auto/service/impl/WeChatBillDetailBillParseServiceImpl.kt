package com.xiaojinzi.tally.bill.auto.service.impl

import android.view.accessibility.AccessibilityNodeInfo
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.getRootParent
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.ParseSourceType
import com.xiaojinzi.tally.base.service.bill.SubBillParseService
import com.xiaojinzi.lib.res.dto.NameOfWeChatBillDetail
import java.text.SimpleDateFormat
import java.util.*

@ServiceAnno(
    SubBillParseService::class,
    name = [NameOfWeChatBillDetail],
)
class WeChatBillDetailBillParseServiceImpl : SubBillParseService {

    override val sourceType: ParseSourceType = ParseSourceType.WeChat_BILL_Detail

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return try {
            val contentNode = info
                .getRootParent()
                .getChild(0)
                .getChild(0)
                .getChild(0)
                .getChild(0)
                .getChild(0)
                .getChild(0)
                .getChild(0)
            val costStr = contentNode.getChild(0).text.toString()
            val tempIndex1 = costStr.indexOf(string = "收入")
            val tempIndex2 = costStr.indexOf(string = "支出")
            val tempIndex3 = costStr.indexOf(string = "元")
            val cost = if(tempIndex3 > -1) {
                when {
                    tempIndex1 > -1 -> {
                        costStr.substring(startIndex = tempIndex1 + 2, endIndex = tempIndex3).toFloat()
                    }
                    tempIndex2 > -1 -> {
                        -costStr.substring(startIndex = tempIndex2 + 2, endIndex = tempIndex3).toFloat()
                    }
                    else -> {
                        0f
                    }
                }
            } else {
                0f
            } // 2021年12月6日 22:23:41
            var timeStr: String? = null
            var orderStr: String? = null
            for (index in 0..contentNode.childCount) {
                try {
                    val child = contentNode.getChild(index)
                    if ("转账时间" == child.getChild(0).text.toString()) {
                        timeStr = child.getChild(1).text.toString()
                    } else if ("转账单号" == child.getChild(0).text.toString()) {
                        orderStr = child.getChild(1).text.toString()
                    }
                } catch (ignore: Exception) {
                    // empty
                }
            }
            val time = try {
                timeStr?.let {
                    SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).parse(it)?.time
                }
            } catch (e: Exception) {
                null
            }
            if (cost == 0f) {
                null
            } else {
                BillParseResultDTO(
                    sourceType = sourceType,
                    cost = cost,
                    time = time,
                )
            }
        } catch (e: Exception) {
            null
        }
    }

}