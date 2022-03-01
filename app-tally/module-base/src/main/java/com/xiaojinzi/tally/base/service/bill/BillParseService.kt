package com.xiaojinzi.tally.base.service.bill

import android.os.Parcelable
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.Keep
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.lib.res.dto.AutoBillSourceViewType
import kotlinx.parcelize.Parcelize

typealias ParseSourceType = AutoBillSourceViewType

@Keep
@Parcelize
data class BillParseResultDTO(
    val sourceType: AutoBillSourceViewType,
    // 这个值的正负的意思就是理解的意思 >0 表示收入 <0 表示支出
    val cost: Float,
    val time: Long? = null,
    val note: String = "",
    val orderNumber: String? = null,
) : Parcelable

interface BillParseService {

    companion object {
        const val Tag = "BillCreateAccessibilityService"
    }

    /**
     * 解析出账单信息
     */
    fun parse(info: AccessibilityNodeInfo): BillParseResultDTO?

}

/**
 * 扩展基础接口, 是真正的实现类的接口
 */
interface SubBillParseService : BillParseService {

    companion object {

        val nameList = listOf(
            ParseSourceType.Alipay_BILL_Detail,
            ParseSourceType.Alipay_Pay_Success,
            ParseSourceType.Alipay_Transfer_Success,
            ParseSourceType.WeChat_BILL_Detail,
            ParseSourceType.WeChat_Pay_Success,
            ParseSourceType.YSF_BILL_Detail1,
            ParseSourceType.YSF_BILL_Detail2,
            ParseSourceType.YSF_BILL_Detail3,
            ParseSourceType.YSF_Pay_Success,
            ParseSourceType.YSF_Transfer_Success,
        ).map { it.sourceName }

    }

    /**
     * 数据源的类型
     */
    val sourceType: ParseSourceType

}

@ServiceAnno(BillParseService::class)
class BillParseServiceImpl : BillParseService {

    override fun parse(info: AccessibilityNodeInfo): BillParseResultDTO? {
        return SubBillParseService
            .nameList
            .mapNotNull {
                ServiceManager.get(SubBillParseService::class.java, it)
            }
            .firstNotNullOfOrNull {
                it.parse(info = info)
            }
    }

}

