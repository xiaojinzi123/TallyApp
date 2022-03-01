package com.xiaojinzi.tally.bill.module.book_detail.domain

import android.content.Context
import androidx.paging.PagingData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.BillDetailDayDTO
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBookDTO
import com.xiaojinzi.tally.base.support.*
import com.xiaojinzi.tally.bill.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ViewModelLayer
interface BookDetailUseCase : BaseUseCase {

    /**
     * 初始化的账本 ID
     */
    val initBookIdData: MutableInitOnceData<String>

    /**
     * 账本的对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val bookObservableDTO: Flow<TallyBookDTO?>

    /**
     * 这个账本的所有账单
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val bookBillListObservableDTO: Flow<PagingData<BillDetailDayDTO>>

    /**
     * 去创建账单
     */
    fun toCreateBill(context: Context, isTransfer: Boolean)

    /**
     * 删除这个账本
     */
    fun deleteBook(context: Context)

}

@ViewModelLayer
class BookDetailUseCaseImpl : BaseUseCaseImpl(), BookDetailUseCase {

    override val initBookIdData = MutableInitOnceData<String>()

    override val bookObservableDTO = combine(
        initBookIdData.valueStateFlow,
        tallyService.dataBaseChangedObservable,
    ) { bookId, _ ->
        tallyBookService.getBookById(uid = bookId)
    }
        .sharedStateIn(scope = scope)

    @ExperimentalCoroutinesApi
    override val bookBillListObservableDTO = combine(
        initBookIdData.valueStateFlow,
        tallyService.dataBaseChangedObservable,
    ) { bookId, _ ->
        tallyBillDetailQueryPagingService.subscribeCommonPageBillDetailObservable(
            billQueryCondition = BillQueryConditionDTO(
                bookIdList = listOf(bookId),
            )
        )
    }.flatMapLatest { it }

    override fun toCreateBill(context: Context, isTransfer: Boolean) {
        initBookIdData.value.let { bookId ->
            Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                .putBoolean("isTransfer", isTransfer)
                .putString("bookId", bookId)
                .forward()
        }
    }

    override fun deleteBook(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val currentBookDTO = bookObservableDTO.value ?: return@launch
            if (currentBookDTO.isDefault) {
                tallyAppToast(content = context.getString(R.string.res_str_the_default_ledger_can_not_be_deleted))
                return@launch
            }
            // 当前的账本 ID
            val currentBookId = currentBookDTO.uid
            val billCountAboutThisAccount =
                tallyBillService.getCountByBookId(bookId = currentBookId)
            if (billCountAboutThisAccount == 0) {
                // 删除这个账本
                tallyBookService.deleteByUid(uid = currentBookId)
                context.tryFinishActivity()
            } else {
                val action = suspendCoroutine<Int> { cot ->
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.res_str_tip4)
                        .setOnCancelListener {
                            cot.resume(value = -1)
                        }
                        .setNegativeButton(R.string.res_str_delete_ledger_and_bill) { dialog, _ ->
                            cot.resume(value = 0)
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.res_str_transfer_and_delete) { dialog, _ ->
                            cot.resume(value = 1)
                            dialog.dismiss()
                        }
                        .show()
                }
                if (action == -1) {
                    return@launch
                }
                if (action == 0) {
                    // 删除相关账单
                    tallyBillService.deleteByBookId(bookId = currentBookId)
                    // 删除这个账本
                    tallyBookService.deleteByUid(uid = currentBookId)
                } else if (action == 1) {
                    // 进行转移
                    val selectBookId = Router.with(context)
                        .hostAndPath(TallyRouterConfig.TALLY_BILL_BOOK_SELECT)
                        .putStringArray("disableIds", listOf(currentBookId).toTypedArray())
                        .requestCodeRandom()
                        .intentAwait()
                        .getStringArrayExtra("data")!!
                        .first()
                    Assert.assertNotEquals(
                        value1 = selectBookId,
                        value2 = currentBookId,
                    )
                    tallyBillService.bookBillTransfer(
                        oldBookId = currentBookId,
                        newBookId = selectBookId
                    )
                    // 删除这个账本
                    tallyBookService.deleteByUid(uid = currentBookId)
                }
                if (action != -1) {
                    context.tryFinishActivity()
                }
            }
        }
    }

}
