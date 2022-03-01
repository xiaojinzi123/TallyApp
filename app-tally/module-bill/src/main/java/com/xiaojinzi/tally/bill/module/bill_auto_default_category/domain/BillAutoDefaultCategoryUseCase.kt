package com.xiaojinzi.tally.bill.module.bill_auto_default_category.domain

import android.content.Context
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceViewDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO
import com.xiaojinzi.tally.base.support.tallyBillAutoSourceViewService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ViewModelLayer
interface BillAutoDefaultCategoryUseCase: BaseUseCase {

    /**
     * 显示的列表
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dataListObservableDTO: Flow<List<TallyBillAutoSourceViewDetailDTO>>

    /**
     * 选择账户
     */
    fun selectCategory(context: Context, uid: String)

}

@ViewModelLayer
class BillAutoDefaultCategoryUseCaseImpl: BaseUseCaseImpl(), BillAutoDefaultCategoryUseCase {

    override val dataListObservableDTO = tallyService.dataBaseChangedObservable
        .map { tallyBillAutoSourceViewService.getAllDetail() }
        .sharedStateIn(scope = scope)

    override fun selectCategory(context: Context, uid: String) {
        scope.launch(ErrorIgnoreContext) {
            val targetCategory = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_CATEGORY)
                .putBoolean("isReturnData", true)
                .requestCodeRandom()
                .intentAwait()
                .getParcelableExtra<TallyCategoryDTO>("data")?: return@launch
            val targetItem = dataListObservableDTO.value.find { it.core.uid == uid }?:return@launch
            tallyBillAutoSourceViewService.update(
                target = targetItem.core.copy(
                    categoryId = targetCategory.uid
                )
            )
        }
    }

}