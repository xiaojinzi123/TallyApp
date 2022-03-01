package com.xiaojinzi.tally.bill.module.cate_group_create.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.UiContext
import com.xiaojinzi.module.base.domain.IconSelectUseCase
import com.xiaojinzi.module.base.domain.IconSelectUseCaseImpl
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getActivity
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupTypeDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryInsertDTO
import com.xiaojinzi.tally.base.support.tallyCategoryService
import com.xiaojinzi.tally.bill.R
import com.xiaojinzi.module.base.support.ResData
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.tally.base.support.tallyAppToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * 1. 承载了创建的需求
 * 2. 承载了更新的需求
 */
interface CateGroupCreateUseCase : BaseUseCase {

    /**
     * 选择 Icon 的逻辑类
     */
    val iconSelectUseCase: IconSelectUseCase

    /**
     * 类别组的 Id
     */
    val cateGroupIdInitData: MutableInitOnceData<String?>

    /**
     * 初始化的类别组的类别
     */
    val cateGroupTypeInitDate: MutableInitOnceData<TallyCategoryGroupTypeDTO>

    /**
     * 更新的时候对的目标对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val cateGroupInitObservableDTO: Flow<TallyCategoryGroupDTO?>

    /**
     * 用户输入的名称
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val nameObservableDTO: MutableSharedStateFlow<String>

    /**
     * 是否名称符合要求
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isNameFormatCorrectObservableDTO: Flow<Boolean>

    /**
     * 是否是更新
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isUpdateObservableDTO: Flow<Boolean>

    /**
     * 是否可以下一步
     */
    val canNextObservableDTO: Flow<Boolean>

    /**
     * 创建类别组, 默认就会带一个其他的子类别
     */
    fun newCateGroup(context: Context)

    /**
     * 删除类别组
     */
    fun deleteCateGroup(@UiContext context: Context)

}

class CateGroupCreateUseCaseImpl : BaseUseCaseImpl(), CateGroupCreateUseCase {

    override val iconSelectUseCase: IconSelectUseCase = IconSelectUseCaseImpl()

    override val cateGroupIdInitData = MutableInitOnceData<String?>()

    override val cateGroupTypeInitDate = MutableInitOnceData<TallyCategoryGroupTypeDTO>()

    override val cateGroupInitObservableDTO = cateGroupIdInitData
        .valueStateFlow
        .map { cateGroupId ->
            cateGroupId?.run {
                tallyCategoryService.getTallyCategoryGroupById(id = this)
            }
        }
        .onEach { cateGroup ->
            // 进行初始化
            if (cateGroup != null) {
                iconSelectUseCase.initIconData.value = cateGroup.iconRsd
                nameObservableDTO.value = cateGroup.getStringItemVO().nameOfApp
            }
        }
        .sharedStateIn(scope = scope)

    override val nameObservableDTO = MutableSharedStateFlow(initValue = "")

    override val isNameFormatCorrectObservableDTO = nameObservableDTO.map { it.trim().isNotEmpty() }

    override val isUpdateObservableDTO = cateGroupInitObservableDTO
        .map {
            it != null
        }

    override val canNextObservableDTO = combine(
        isNameFormatCorrectObservableDTO,
        iconSelectUseCase.iconRsdNullableObservableDTO
    ) { isNameFormatCorrect, iconSelectRsd ->
        isNameFormatCorrect && (iconSelectRsd != null)
    }

    override fun newCateGroup(context: Context) {
        scope.launch {
            val iconRsd = iconSelectUseCase.iconRsdNullableObservableDTO.value ?: return@launch
            val name = nameObservableDTO.value.trim()
            val targetCateGroupDTO = cateGroupInitObservableDTO.value
            if (targetCateGroupDTO == null) {
                val cateGroup = tallyCategoryService.insertTallyCategoryGroup(
                    target = TallyCategoryGroupInsertDTO(
                        type = cateGroupTypeInitDate.value,
                        iconInnerIndex = ResData.getIconRsdIndex(rsdId = iconRsd),
                        name = name,
                    )
                )
                // 插入一个默认的类别
                tallyCategoryService.insertTallyCategory(
                    target = TallyCategoryInsertDTO(
                        groupId = cateGroup.uid,
                        isBuiltIn = true,
                        iconIndex = ResData.getIconRsdIndex(rsdId = R.drawable.res_question_mark1),
                        nameIndex = ResData.getNameRsdIndex(rsdId = R.string.res_str_other),
                    )
                )
                context.getActivity()?.let { act ->
                    act.setResult(
                        Activity.RESULT_OK,
                        Intent().apply {
                            this.putExtra("cateGroupId", cateGroup.uid)
                        }
                    )
                }
            } else {
                tallyCategoryService.updateTallyCategoryGroup(
                    targetCateGroupDTO.copy(
                        iconRsd = iconRsd,
                        name = name,
                    )
                )
            }
            // 结束界面
            context.tryFinishActivity()
        }
    }

    override fun deleteCateGroup(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            cateGroupInitObservableDTO.value?.let { cateGroup ->
                val categoryList =
                    tallyCategoryService.getTallyCategoriesByGroupId(groupId = cateGroup.uid)
                if (categoryList.isNullOrEmpty()) {
                    tallyCategoryService.deleteCateGroupById(uid = cateGroup.uid)
                    context.tryFinishActivity()
                } else {
                    tallyAppToast(content = app.getString(R.string.res_str_err_tip7))
                }
            }
        }
    }

    override fun destroy() {
        super.destroy()
        iconSelectUseCase.destroy()
    }

}
