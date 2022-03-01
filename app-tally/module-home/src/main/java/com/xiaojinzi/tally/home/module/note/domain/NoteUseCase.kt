package com.xiaojinzi.tally.home.module.note.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.module.base.support.dbPersistence
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getFragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ViewModelLayer
interface NoteUseCase : BaseUseCase {

    companion object {
        const val HISTORY_MAX_COUNT = 10
        const val HISTORY_SPLIT_KEY = "!@#$%^&*()"
    }

    /**
     * 备注的值
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val noteObservableDTO: MutableSharedStateFlow<String>

    /**
     * 历史数据
     */
    val noteHistoryListObservableDTO: Flow<List<String>>

    /**
     * 删除历史
     */
    fun deleteNoteHistory(value: String)

    /**
     * 返回数据
     */
    fun returnData(context: Context)

}

@ViewModelLayer
class NoteUseCaseImpl : BaseUseCaseImpl(), NoteUseCase {

    override val noteObservableDTO = MutableSharedStateFlow(initValue = "")

    private val noteHistoryStrObservableDTO = MutableSharedStateFlow<String?>(initValue = "")
        .dbPersistence(
            scope = scope,
            key = "noteHistory",
            def = null,
        )

    override val noteHistoryListObservableDTO = noteHistoryStrObservableDTO
        .map { noteHistoryStr ->
            noteHistoryStr
                ?.split(NoteUseCase.HISTORY_SPLIT_KEY)
                ?.filter { it.trim().isNotEmpty() }
                ?: emptyList()
        }
        .sharedStateIn(
            scope = scope,
            initValue = emptyList(),
        )

    override fun deleteNoteHistory(value: String) {
        val historyList = noteHistoryListObservableDTO
            .value
            .toMutableList()
            .apply {
                this.remove(element = value)
            }
            .take(n = NoteUseCase.HISTORY_MAX_COUNT)
        noteHistoryStrObservableDTO.value =
            historyList.joinToString(separator = NoteUseCase.HISTORY_SPLIT_KEY)
    }

    override fun returnData(context: Context) {
        val noteValue = noteObservableDTO.value
        val historyList = noteHistoryListObservableDTO
            .value
            .toMutableList()
            .apply {
                this.remove(element = noteValue)
                this.add(index = 0, element = noteValue)
            }
            .take(n = NoteUseCase.HISTORY_MAX_COUNT)
        noteHistoryStrObservableDTO.value =
            historyList.joinToString(separator = NoteUseCase.HISTORY_SPLIT_KEY)
        context.getFragmentActivity()?.let { act ->
            act.setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    this.putExtra("data", noteObservableDTO.value)
                }
            )
            act.finish()
        }
    }

}
