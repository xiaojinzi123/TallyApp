package com.xiaojinzi.tally.develop

import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.service.develop.DevelopGroupConfig
import com.xiaojinzi.module.base.service.develop.DevelopItemActionConfig
import com.xiaojinzi.module.base.service.develop.DevelopItemCheckBoxConfig
import com.xiaojinzi.module.base.service.develop.DevelopItemInfoViewConfig
import com.xiaojinzi.module.base.support.buglyService
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyCategoryService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

val counter = AtomicInteger(0)

val developMainGroupsData = listOf(
    DevelopGroupConfig(
        groupName = "tally",
        items = listOf(
            DevelopItemActionConfig(
                content = "去数据统计界面",
                action = {
                    Router.with(it)
                        .hostAndPath(TallyRouterConfig.TALLY_DEVELOP_DATA_STATISTICAL)
                        .forward()
                }
            ),
            DevelopItemActionConfig(
                content = "testJavaCrash",
                action = {
                    AppScope.launch {
                        buglyService?.testJavaCrash()
                    }
                }
            ),
            DevelopItemCheckBoxConfig(
                content = "测试的 CheckBox",
                developKey = "测试的 Key",
            ),
            DevelopItemInfoViewConfig(
                content = "类别组表信息",
                observable = tallyCategoryService
                    .categoryGroupCountObservable
                    .map { count ->
                        "总个数$count, 更新标记：${counter.incrementAndGet()}"
                    }
            ),
            DevelopItemInfoViewConfig(
                content = "类别表信息",
                observable = tallyCategoryService
                    .categoryCountObservable
                    .map { count ->
                        "总个数$count, 更新标记：${counter.incrementAndGet()}"
                    }
            ),
            DevelopItemInfoViewConfig(
                content = "账单记录表信息",
                observable = tallyBillService
                    .billCountObservable
                    .map { count ->
                        "总个数$count, 更新标记：${counter.incrementAndGet()}"
                    }
            ),
        )
    )
)