package com.xiaojinzi.tally.bill.module.cate_create.domain

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.module.base.domain.IconSelectUseCase
import com.xiaojinzi.module.base.domain.IconSelectUseCaseImpl
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.module.base.support.tryFinishActivity
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryWithGroupDTO
import com.xiaojinzi.module.base.support.ResData
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.support.*
import com.xiaojinzi.tally.bill.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ViewModelLayer
interface CateCreateUseCase : BaseUseCase {

    /**
     * 选择 Icon 的逻辑类
     */
    val iconSelectUseCase: IconSelectUseCase

    /**
     * 初始化的类别组Id
     */
    val cateGroupIdInitData: MutableInitOnceData<String?>

    /**
     * 初始化的类别 Id
     */
    val cateIdInitData: MutableInitOnceData<String?>

    /**
     * 初始化的类别对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val initCateDataObservableDTO: Flow<TallyCategoryWithGroupDTO?>

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
     * 创建类别
     */
    fun newCate(context: Context)

    /**
     * 删除
     */
    fun deleteCate(context: Context)

}

@ViewModelLayer
class CateCreateUseCaseImpl : BaseUseCaseImpl(), CateCreateUseCase {

    override val iconSelectUseCase: IconSelectUseCase = IconSelectUseCaseImpl()

    override val cateGroupIdInitData = MutableInitOnceData<String?>()

    override val cateIdInitData = MutableInitOnceData<String?>()

    override val initCateDataObservableDTO = cateIdInitData
        .valueStateFlow
        .map {
            it?.run {
                tallyCategoryService.getTallyCategoryDetailById(id = this)
            }
        }
        .onEach {
            it?.run {
                nameObservableDTO.value = this.category.getStringItemVO().nameOfApp
                iconSelectUseCase.initIconData.value = this.category.iconRsd
            }
        }
        .sharedStateIn(scope = scope)

    override val nameObservableDTO = MutableSharedStateFlow(initValue = "")

    override val isNameFormatCorrectObservableDTO = nameObservableDTO.map { it.trim().isNotEmpty() }

    override val isUpdateObservableDTO = initCateDataObservableDTO
        .map {
            it != null
        }

    override val canNextObservableDTO = combine(
        isNameFormatCorrectObservableDTO,
        iconSelectUseCase.iconRsdNullableObservableDTO
    ) { isNameFormatCorrect, iconSelectRsd ->
        isNameFormatCorrect && (iconSelectRsd != null)
    }

    override fun newCate(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val iconRsd = iconSelectUseCase.iconRsdNullableObservableDTO.value ?: return@launch
            val name = nameObservableDTO.value.trim()
            val targetCateGDTO = initCateDataObservableDTO.value
            if (targetCateGDTO == null) {
                tallyCategoryService.insertTallyCategory(
                    target = TallyCategoryInsertDTO(
                        groupId = cateGroupIdInitData.value!!,
                        iconIndex = ResData.getIconRsdIndex(rsdId = iconRsd),
                        name = name,
                    )
                )
            } else {
                tallyCategoryService.updateTallyCategory(
                    target = targetCateGDTO.category.copy(
                        iconRsd = iconRsd,
                        name = name,
                    )
                )
            }
            context.tryFinishActivity()
        }
    }

    override fun deleteCate(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            initCateDataObservableDTO.value?.let { cate ->
                val categoryId = cate.category.uid
                // 先查询这个类别下面是否有账单
                val count = tallyBillService.getCountByCondition(
                    conditionBill = BillQueryConditionDTO(
                        categoryIdList = listOf(categoryId)
                    )
                )
                if (count > 0) {
                    // 第一步, 先弹出弹框, 确定是删除相关账单, 还是转移
                    val action = suspendCoroutine<Int> { cot ->
                        MaterialAlertDialogBuilder(context)
                            .setMessage(R.string.res_str_tip4)
                            .setOnCancelListener {
                                cot.resume(value = -1)
                            }
                            .setNegativeButton(R.string.res_str_delete_category_and_bill) { dialog, _ ->
                                cot.resume(value = 0)
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.res_str_transfer_and_delete) { dialog, _ ->
                                cot.resume(value = 1)
                                dialog.dismiss()
                            }
                            .show()
                    }
                    if (action == -1) { // 表示取消了
                        return@launch
                    }
                    // 第二步根据用户的选择, 是否需要进行账单转移
                    when (action) {
                        0 -> { // 删除相关的账单, 并且删除这个类别
                            tallyBillService.deleteByCategoryId(categoryId = categoryId)
                            tallyCategoryService.deleteCateById(uid = categoryId)
                            context.tryFinishActivity()
                        }
                        1 -> {
                            // 选择一个新的类别
                            val newCategoryId =
                                tallyRouteResultService.selectCategory(context = context)
                            if (newCategoryId == categoryId) {
                                tallyAppToast(content = app.getString(R.string.res_str_err_tip6))
                            } else {
                                // 类别转移
                                tallyBillService.categoryBillTransfer(
                                    oldCategoryId = categoryId,
                                    newCategoryId = newCategoryId,
                                )
                                tallyCategoryService.deleteCateById(uid = categoryId)
                                context.tryFinishActivity()
                            }
                        }
                        else -> notSupportError()
                    }
                } else {
                    tallyBillService.deleteByCategoryId(categoryId = categoryId)
                    tallyCategoryService.deleteCateById(uid = categoryId)
                    context.tryFinishActivity()
                }
            }
        }
    }

    override fun destroy() {
        super.destroy()
        iconSelectUseCase.destroy()
    }


}