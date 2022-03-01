package com.xiaojinzi.tally.bill.module.cycle_bill_task.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.ktx.getActivity
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillUsageDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ViewModelLayer
interface CycleBillTaskUseCase: BaseUseCase {

    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val allBillTaskObservableDTO: Flow<List<TallyBillDetailDTO>>

    /**
     * 选择 Item
     */
    fun selectItem(context: Context, index: Int)

}

@ViewModelLayer
class CycleBillTaskUseCaseImpl: BaseUseCaseImpl(), CycleBillTaskUseCase {

    override val allBillTaskObservableDTO = tallyService
        .dataBaseChangedObservable
        .map {
            tallyBillService.getBillListByCondition(
                condition = BillQueryConditionDTO(
                    billUsage = TallyBillUsageDTO.CycleTask,
                )
            )
        }

    override fun selectItem(context: Context, index: Int) {
        scope.launch (ErrorIgnoreContext){

            val billId = allBillTaskObservableDTO.first()[index].bill.uid

            context.getActivity()?.run {
                this.setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        this.putExtra("billId", billId)
                    }
                )
            }
            context.tryFinishActivity()

        }
    }

}