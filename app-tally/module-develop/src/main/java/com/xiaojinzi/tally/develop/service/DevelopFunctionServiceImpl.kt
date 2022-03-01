package com.xiaojinzi.tally.develop.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.develop.DevelopFunctionService
import com.xiaojinzi.module.base.service.develop.DevelopGroupConfig
import com.xiaojinzi.tally.develop.developMainGroupsData

@ServiceAnno(DevelopFunctionService::class, name = [DevelopFunctionService.ImplName_Tally])
class DevelopFunctionServiceImpl: DevelopFunctionService {

    override val order: Int = 3

    override val developMainGroup: List<DevelopGroupConfig>
        get() = developMainGroupsData

}