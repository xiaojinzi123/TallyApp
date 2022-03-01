package com.xiaojinzi.tally.home.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.spPersistenceNonNull
import com.xiaojinzi.tally.base.service.MainService
import com.xiaojinzi.tally.base.service.TabType
import com.xiaojinzi.tally.base.support.tallyBillService
import kotlinx.coroutines.flow.combine

@ServiceAnno(MainService::class)
class MainServiceImpl : MainService {

    override val tabObservable = MutableSharedStateFlow(initValue = TabType.Home)

    private val isShowCongratulationObservableDTO = MutableSharedStateFlow<Boolean>()
        .spPersistenceNonNull(
            key = "isShowCongratulation",
            def = false,
        )

    override val isCongratulationObservableDTO = combine(
        isShowCongratulationObservableDTO,
        tallyBillService.billCountObservable,
    ) { isShowCongratulation, billCount ->
        !isShowCongratulation && (billCount == 1L)
    }

    override fun flagCongratulation() {
        isShowCongratulationObservableDTO.value = true
    }

}