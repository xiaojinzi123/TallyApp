package com.xiaojinzi.tally.home.module.label.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getFragmentActivity
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.support.tallyLabelService
import com.xiaojinzi.tally.home.module.label.view.LabelItemVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface LabelListUseCase : BaseUseCase {

    /**
     * 是否返回数据
     */
    val isReturnDataInitData: MutableInitOnceData<Boolean>

    /**
     * 所有的标签
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val labelListVO: Flow<List<LabelItemVO>>

    /**
     * 初始化标签的选择
     */
    fun initLabelSelect(idList: List<String>)

    /**
     * 翻转选择
     */
    fun toggleLabelSelect(id: String)

    /**
     * 删除已经选择的
     */
    fun deleteSelectLabel()

    /**
     * 完成选择
     */
    fun completeSelect(context: Context)

    /**
     * 去编辑
     */
    fun toEdit(context: Context, id: String)

}

class LabelListUseCaseImpl : BaseUseCaseImpl(), LabelListUseCase {

    override val isReturnDataInitData = MutableInitOnceData<Boolean>()

    /**
     * 如果没有就表示没选中
     */
    private val labelIdSelectListObservableVO =
        MutableSharedStateFlow<MutableMap<String, Boolean>>(initValue = mutableMapOf())

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    override val labelListVO: Flow<List<LabelItemVO>> =
        combine(
            tallyLabelService
                .subscribeAll()
                .map { list ->
                    list.map { labelItem ->
                        LabelItemVO(
                            labelId = labelItem.uid,
                            content = labelItem.name,
                            color = labelItem.colorInt,
                        )
                    }
                },
            labelIdSelectListObservableVO
        ) { list, selectIdMap ->
            list.map {
                it.copy(isSelect = selectIdMap[it.labelId] == true)
            }
        }
            .flowOn(context = Dispatchers.IO)

    override fun initLabelSelect(idList: List<String>) {
        labelIdSelectListObservableVO.value = labelIdSelectListObservableVO.value.apply {
            idList.forEach { labelId ->
                this[labelId] = true
            }
        }
    }

    override fun toggleLabelSelect(id: String) {
        val map = labelIdSelectListObservableVO.value
        val targetValue: Boolean? = map[id]
        if (targetValue == null) {
            map[id] = true
        } else {
            map[id] = !targetValue
        }
        labelIdSelectListObservableVO.value = map
    }

    override fun deleteSelectLabel() {
        scope.launch {
            val targetDeleteIdList = labelIdSelectListObservableVO
                .value
                .filter { it.value }
                .keys
                .toList()
            // 删除完毕之后, 删除一下选择 map 里面的数据
            tallyLabelService.deleteByIds(ids = targetDeleteIdList)
            labelIdSelectListObservableVO.value = labelIdSelectListObservableVO
                .value
                .apply {
                    this.clear()
                }
        }
    }

    override fun completeSelect(context: Context) {
        context.getFragmentActivity()?.let { fragmentActivity ->
            val labelIdList = labelIdSelectListObservableVO.value
                .filter { it.value }
                .map { it.key }
                .toList()
            fragmentActivity.setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    this.putExtra("labelIds", labelIdList.toTypedArray())
                }
            )
            fragmentActivity.finish()
        }
    }

    override fun toEdit(context: Context, id: String) {
        Router.with(context)
            .hostAndPath(hostAndPath = TallyRouterConfig.TALLY_LABEL_CREATE)
            .putString("labelId", id)
            .forward()
    }

}