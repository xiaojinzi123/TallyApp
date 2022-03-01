package com.xiaojinzi.tally.base.service.setting

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.dbPersistenceNonNull
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.support.DBCommonKeys

/**
 * 统计相关的 Setting
 */
interface StatisticalSettingService {

    /**
     * 账单创建界面输入时振动
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val vibrateDuringInputObservableDTO: MutableSharedStateFlow<Boolean>

    /**
     * 类别费用统计的百分比进度条的颜色是否跟随饼状图的
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val cateCostProgressPercentColorIsFollowPieChartObservableDTO: MutableSharedStateFlow<Boolean>

    /**
     * 类别费用统计的百分比进度条的优化
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val cateCostProgressPercentOptimizeObservableDTO: MutableSharedStateFlow<Boolean>

}

@ServiceAnno(StatisticalSettingService::class)
class StatisticalSettingServiceImpl : StatisticalSettingService {

    override val vibrateDuringInputObservableDTO = MutableSharedStateFlow<Boolean>()
        .dbPersistenceNonNull(
            key = DBCommonKeys.vibrateDuringInput,
            def = true,
        )

    override val cateCostProgressPercentColorIsFollowPieChartObservableDTO = MutableSharedStateFlow<Boolean>()
        .dbPersistenceNonNull(
            key = DBCommonKeys.cateCostProgressPercentColorIsFollowPieChart,
            def = false,
        )

    override val cateCostProgressPercentOptimizeObservableDTO = MutableSharedStateFlow<Boolean>()
        .dbPersistenceNonNull(
            key = DBCommonKeys.cateCostProgressPercentOptimize,
            def = true,
        )

}