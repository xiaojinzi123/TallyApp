package com.xiaojinzi.tally.home.module.note.view

import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.home.module.note.domain.NoteUseCase
import com.xiaojinzi.tally.home.module.note.domain.NoteUseCaseImpl

@ViewLayer
class NoteViewModel(
    private val useCase: NoteUseCase = NoteUseCaseImpl(),
): BaseViewModel(), NoteUseCase by useCase {
}