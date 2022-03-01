package com.xiaojinzi.tally.bill.module.bill_auto_create.domain

import android.content.Context
import com.xiaojinzi.component.forward
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.bill.module.bill_create.domain.BillCreateUseCase
import com.xiaojinzi.tally.bill.module.bill_create.domain.BillCreateUseCaseImpl
import kotlinx.coroutines.launch

@ViewModelLayer
interface AutoBillCreateUseCase : BaseUseCase, BillCreateUseCase {

    /**
     * 去账单创建界面
     */
    fun toBillCreateView(context: Context)

}

@ViewModelLayer
class AutoBillCreateUseCaseImpl(
    private val billCreateUseCase: BillCreateUseCase = BillCreateUseCaseImpl(),
) : BaseUseCaseImpl(), AutoBillCreateUseCase,
    BillCreateUseCase by billCreateUseCase {

    override fun toBillCreateView(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                .apply {
                    timeObservableDTO.value?.let {
                        this.putLong("time", it)
                    }
                }
                .putFloat(
                    "cost", costUseCase.calculateResult(
                        target = costUseCase.costStrObservableDTO.value.strValue
                    )
                )
                .putString("categoryId", categorySelectObservableDTO.value?.uid)
                .putString("accountId", selectedAccountObservableDTO.value?.uid)
                .putString("note", noteStrObservableDTO.value)
                .forward {
                    context.tryFinishActivity()
                }
        }
    }

    override fun destroy() {
        super.destroy()
        billCreateUseCase.destroy()
    }

}