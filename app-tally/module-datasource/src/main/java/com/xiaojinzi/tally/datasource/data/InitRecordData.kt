package com.xiaojinzi.tally.datasource.data

import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelService
import com.xiaojinzi.tally.base.service.datasource.ValueLong
import kotlin.random.Random

const val DayTimeTimestamp = 24 * 60 * 60 * 1000L

@Keep
data class TestTallyBillInsertDTO(
    // 创建时间
    val createTime: Long,
    // 花销. 当用户设置 100 的值, 实际会存100。 放大了一百倍存. 方便计算
    @ValueLong
    val cost: Long,
    // 备注
    val note: String?,
)

val endTime = System.currentTimeMillis() + 90 * DayTimeTimestamp
val startTime = (endTime - (2 * 365 * DayTimeTimestamp))

val testBillList = (1..1000)
    .map {
        TestTallyBillInsertDTO(
            createTime = Random.nextLong(from = startTime, until = endTime),
            cost = Random.nextLong(from = -30000, until = 30000),
            note = "测试备注$it"
        )
    }

val testLabelList = (1..16)
    .map {
        TallyLabelInsertDTO(
            colorInnerIndex = TallyLabelService.labelInnerColorList.indices.random(),
            name = "测试标签$it".toStringItemDTO()
        )
    }