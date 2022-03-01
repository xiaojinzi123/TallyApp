package com.xiaojinzi.tally.home.module.label.domain

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.TallyLabelDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelService
import com.xiaojinzi.tally.base.support.tallyAppToast
import com.xiaojinzi.tally.base.support.tallyLabelService
import com.xiaojinzi.tally.home.R
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface LabelCreateUseCase : BaseUseCase {

    /**
     * 初始化的标签 Id
     */
    val labelIdInitData: MutableInitOnceData<String?>

    /**
     * 初始化的标签数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val initLabelDataObservableDTO: SharedStateFlow<TallyLabelDTO?>

    /**
     * 颜色选择的下标
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val colorSelectIndexObservableDTO: MutableSharedStateFlow<Int>

    /**
     * 名称
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val nameObservableDTO: MutableSharedStateFlow<StringItemDTO>

    /**
     * 添加或者更新
     */
    fun addOrUpdate(context: Context)

}

class LabelCreateUseCaseImpl : BaseUseCaseImpl(), LabelCreateUseCase {

    override val labelIdInitData = MutableInitOnceData<String?>()

    override val initLabelDataObservableDTO: SharedStateFlow<TallyLabelDTO?> =
        labelIdInitData.valueStateFlow
            .map {
                it?.run {
                    tallyLabelService.getById(id = this)
                }
            }
            .onEach { labelDTO ->
                labelDTO?.let { targetLabel ->
                    val index = TallyLabelService
                        .labelInnerColorList
                        .indexOfFirst {
                            it.toArgb() == targetLabel.colorInt
                        }
                    if (index > -1) {
                        colorSelectIndexObservableDTO.value = index
                    } else {
                        colorSelectIndexObservableDTO.value = 0
                    }
                    nameObservableDTO.value = targetLabel.name
                }
            }
            .sharedStateIn(scope = scope)

    override val colorSelectIndexObservableDTO = MutableSharedStateFlow(initValue = 0)

    override val nameObservableDTO = MutableSharedStateFlow(initValue = "".toStringItemDTO())

    override fun addOrUpdate(context: Context) {
        scope.launch {
            val name = nameObservableDTO.value
            if (name.isEmpty) {
                tallyAppToast(contentRsd = R.string.res_str_can_not_be_empty)
            } else {
                val targetNeedUpdateLabel = initLabelDataObservableDTO.value
                if(targetNeedUpdateLabel == null) {
                    tallyLabelService.insert(
                        target = TallyLabelInsertDTO(
                            colorInnerIndex = colorSelectIndexObservableDTO.value,
                            name = nameObservableDTO.value,
                        )
                    )
                } else {
                    tallyLabelService.update(
                        target = targetNeedUpdateLabel.copy(
                            colorInt = TallyLabelService.labelInnerColorList[colorSelectIndexObservableDTO.value].toArgb(),
                            name = nameObservableDTO.value,
                        )
                    )
                }
                context.tryFinishActivity()
            }
        }
    }

}
