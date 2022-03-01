package com.xiaojinzi.tally.bill.module.book_select.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getActivity
import com.xiaojinzi.tally.base.service.datasource.TallyBookDTO
import com.xiaojinzi.tally.base.support.tallyBookService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SelectType {
    SingleSelect, MultiSelect
}

/**
 * 支持单选和多选
 */
interface BillBookListUseCase : BaseUseCase {

    /**
     * 选择的类型
     */
    val selectTypeInitData: MutableInitOnceData<SelectType>

    /**
     * 初始化选中的 id 集合
     */
    val selectIdListInitData: MutableInitOnceData<List<String>>

    /**
     * 是否是单选
     */
    val isSingleSelect: Boolean

    /**
     * 是否是多选
     */
    val isMultiSelect: Boolean

    /**
     * 账单的集合
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val billBookListObservableDTO: MutableSharedStateFlow<List<TallyBookDTO>>

    /**
     * 选择好的 ID 集合
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val billBookIdSelectObservableDTO: MutableSharedStateFlow<Set<String>>

    /**
     * 是否全选了
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectAllObservableDTO: Flow<Boolean>

    /**
     * 翻转选择
     */
    fun toggleSelect(targetBookId: String)

    /**
     * 全选翻转
     */
    fun toggleAll()

    /**
     * 返回数据
     */
    fun returnData(context: Context)

}

class BillBookListUseCaseImpl : BaseUseCaseImpl(), BillBookListUseCase {

    override val selectTypeInitData = MutableInitOnceData<SelectType>()

    override val selectIdListInitData = MutableInitOnceData<List<String>>().apply {
        val targetFlow = this.valueStateFlow
        scope.launch {
            val initList = targetFlow.first()
            if (isSingleSelect) {
                if (initList.size > 1) {
                    notSupportError()
                }
            }
        }
    }

    override val isSingleSelect: Boolean
        get() = selectTypeInitData.value == SelectType.SingleSelect

    override val isMultiSelect: Boolean
        get() = selectTypeInitData.value == SelectType.MultiSelect

    override val billBookListObservableDTO = tallyBookService
        .allBillBookListObservable
        // .map { it.take(4) }
        .sharedStateIn(initValue = emptyList(), scope = scope)

    override val billBookIdSelectObservableDTO = selectIdListInitData
        .valueStateFlow
        .map {
            it.toSet()
        }
        .sharedStateIn(scope = scope)

    override val selectAllObservableDTO =
        combine(
            billBookListObservableDTO, billBookIdSelectObservableDTO
        ) { listDto, selectSet ->
            listDto.all {
                selectSet.contains(it.uid)
            }
        }
            .flowOn(context = Dispatchers.IO)
            .sharedStateIn(initValue = false, scope = scope)

    override fun toggleSelect(targetBookId: String) {
        billBookIdSelectObservableDTO.value = billBookIdSelectObservableDTO.value
            .toMutableSet()
            .apply {
                if (this.contains(targetBookId)) {
                    when {
                        isSingleSelect -> {
                            this.clear()
                        }
                        isMultiSelect -> {
                            this.remove(targetBookId)
                        }
                        else -> {
                            notSupportError()
                        }
                    }
                } else {
                    when {
                        isSingleSelect -> {
                            this.clear()
                        }
                        isMultiSelect -> {
                        }
                        else -> {
                            notSupportError()
                        }
                    }
                    this.add(targetBookId)
                }
            }
    }

    override fun toggleAll() {
        billBookIdSelectObservableDTO.value = billBookIdSelectObservableDTO.value
            .toMutableSet()
            .apply {
                this.clear()
                if (!selectAllObservableDTO.value) {
                    this.addAll(
                        billBookListObservableDTO.value.map { it.uid }
                    )
                }
            }

    }

    override fun returnData(context: Context) {
        context.getActivity()?.let { act ->
            act.setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    this.putExtra("data", billBookIdSelectObservableDTO.value.toTypedArray())
                }
            )
            act.finish()
        }
    }

}