package com.xiaojinzi.module.support.module.fill_in_number.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.module.base.domain.CostUseCase
import com.xiaojinzi.module.base.domain.CostUseCaseImpl
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.ktx.getActivity
import kotlinx.coroutines.launch

@ViewModelLayer
interface FillInNumberUseCase : BaseUseCase {

    val costUseCase: CostUseCase

    /**
     * 完成了
     */
    fun onComplete(context: Context)

}

@ViewModelLayer
class FillInNumberUseCaseImpl(
    override val costUseCase: CostUseCase = CostUseCaseImpl()
) : BaseUseCaseImpl(), FillInNumberUseCase {

    override fun onComplete(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            context.getActivity()?.let { act ->
                act.setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        this.putExtra(
                            "data",
                            costUseCase.calculateResult(
                                target = costUseCase.costStrObservableDTO.value.strValue
                            ),
                        )
                    }
                )
                act.finish()
            }
        }
    }

    override fun destroy() {
        super.destroy()
        costUseCase.destroy()
    }

}