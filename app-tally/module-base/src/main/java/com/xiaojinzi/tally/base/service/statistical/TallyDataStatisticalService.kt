package com.xiaojinzi.tally.base.service.statistical

import com.xiaojinzi.component.anno.ServiceAnno


interface TallyDataStatisticalService : TallyCostDataStatisticalService {
}

@ServiceAnno(TallyDataStatisticalService::class)
class TallyDataStatisticalServiceImpl(
    private val tallyCostDataStatisticalService: TallyCostDataStatisticalService = TallyCostDataStatisticalServiceImpl(),
) : TallyDataStatisticalService,
    TallyCostDataStatisticalService by tallyCostDataStatisticalService {
}