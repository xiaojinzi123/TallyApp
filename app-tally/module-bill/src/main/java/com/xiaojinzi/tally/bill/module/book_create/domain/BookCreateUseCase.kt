package com.xiaojinzi.tally.bill.module.book_create.domain

import android.content.Context
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.TallyBookDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBookInsertDTO
import com.xiaojinzi.tally.base.support.tallyBookService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface BookCreateUseCase : BaseUseCase {

    /**
     * 初始化的账本 ID
     */
    val initBookId: MutableInitOnceData<String?>

    /**
     * 初始化的账本对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val initBookObservableDTO: Flow<TallyBookDTO?>

    /**
     * 是否是更新账本
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isUpdateBookObservableDTO: Flow<Boolean>

    /**
     * 账本的名字
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val nameObservableDTO: MutableSharedStateFlow<String>

    /**
     * 是否名称合法
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isNameFormatCorrectObservableDTO: Flow<Boolean>

    /**
     * 是否可继续
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val canNextObservableDTO: Flow<Boolean>

    /**
     * 创建新的账本
     */
    fun addOrUpdateBook(context: Context)

}

class BookCreateUseCaseImpl : BaseUseCaseImpl(), BookCreateUseCase {

    override val initBookId = MutableInitOnceData<String?>()

    override val initBookObservableDTO = initBookId.valueStateFlow
        .map { bookId ->
            bookId?.let {
                tallyBookService.getBookById(uid = it)
            }
        }
        .onEach { bookDTO ->
            bookDTO?.let {
                nameObservableDTO.value = it.nameItem.nameOfApp
            }
        }
        .sharedStateIn(scope = scope)

    override val isUpdateBookObservableDTO = initBookObservableDTO.map { it != null }

    override val nameObservableDTO = MutableSharedStateFlow(initValue = "")

    override val isNameFormatCorrectObservableDTO = nameObservableDTO
        .map {
            it.isNotEmpty()
        }

    override val canNextObservableDTO = isNameFormatCorrectObservableDTO

    override fun addOrUpdateBook(context: Context) {
        scope.launch {
            val targetBook = initBookId.value?.let {
                tallyBookService.getBookById(uid = it)
            }
            val targetName = nameObservableDTO.value.trim()
            if (targetBook == null) {
                tallyBookService.insert(
                    target = TallyBookInsertDTO(
                        name = targetName,
                    )
                )
            } else {
                tallyBookService.update(target = targetBook.copy(
                    name = targetName,
                ))
            }
            context.tryFinishActivity()
        }
    }

}
