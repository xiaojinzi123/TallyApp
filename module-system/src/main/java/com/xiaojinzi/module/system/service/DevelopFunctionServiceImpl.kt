package com.xiaojinzi.module.system.service

import com.xiaojinzi.component.ComponentActivityStack
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.develop.DevelopFunctionService
import com.xiaojinzi.module.base.service.develop.DevelopGroupConfig
import com.xiaojinzi.module.base.service.develop.DevelopItemCheckBoxConfig
import com.xiaojinzi.module.base.service.develop.DevelopItemInfoViewConfig
import com.xiaojinzi.module.base.support.developDateTimeFieldService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.map

@ServiceAnno(DevelopFunctionService::class, name = [DevelopFunctionService.ImplName_System])
class DevelopFunctionServiceImpl : DevelopFunctionService {

    @DelicateCoroutinesApi
    override val developMainGroup: List<DevelopGroupConfig> = listOf(
        DevelopGroupConfig(
            groupName = "DateTime",
            actionName = "选择时间",
            action = {
                ComponentActivityStack
                    .topActivity
                    ?.let { act ->
                        developDateTimeFieldService.toChooseDateTime(context = act)
                    }
            },
            items = listOf(
                DevelopItemInfoViewConfig(
                    content = "Year",
                    observable = developDateTimeFieldService
                        .yearObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "Month",
                    observable = developDateTimeFieldService
                        .monthObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "DayOfWeek",
                    observable = developDateTimeFieldService
                        .dayOfWeekObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "DayOfWeekInMonth",
                    observable = developDateTimeFieldService
                        .dayOfWeekInMonthObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "DayOfMonth",
                    observable = developDateTimeFieldService
                        .dayOfMonthObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "DayOfYear",
                    observable = developDateTimeFieldService
                        .dayOfYearObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "Hour",
                    observable = developDateTimeFieldService
                        .hourObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "HourOfDay",
                    observable = developDateTimeFieldService
                        .hourOfDayObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "Minute",
                    observable = developDateTimeFieldService
                        .minuteObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "Second",
                    observable = developDateTimeFieldService
                        .secondObservableDTO
                        .map { it.toString() }
                ),
                DevelopItemInfoViewConfig(
                    content = "Millisecond",
                    observable = developDateTimeFieldService
                        .millisecondObservableDTO
                        .map { it.toString() }
                ),
            )
        )
    )

}