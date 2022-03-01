package com.xiaojinzi.tally.bill.module.cycle_bill.domain

import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import kotlinx.coroutines.flow.Flow

@ViewModelLayer
interface CycleBillUseCase : BaseUseCase {



}

@ViewModelLayer
class CycleBillUseCaseImpl : BaseUseCaseImpl(), CycleBillUseCase {


}