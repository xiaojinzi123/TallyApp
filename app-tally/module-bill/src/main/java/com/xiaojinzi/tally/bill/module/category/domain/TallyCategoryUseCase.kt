package com.xiaojinzi.tally.bill.module.category.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.getActivity
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.base.support.tallyCategoryService
import com.xiaojinzi.tally.base.support.tallyService
import com.xiaojinzi.tally.bill.module.category.view.BillCategoryTabType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface TallyCategoryUseCase : BaseUseCase {

    /**
     * 是否是返回数据的
     */
    val isReturnDataData: MutableInitOnceData<Boolean>

    /**
     * tab 初始化
     */
    val initTabTypeData: MutableInitOnceData<BillCategoryTabType>

    /**
     * tab 选中的情况
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val tabSelectIndexTypeObservableDTO: MutableSharedStateFlow<BillCategoryTabType>

    /**
     * 要展示的数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val dataObservableDTO: MutableSharedStateFlow<List<Pair<TallyCategoryGroupDTO, List<TallyCategoryDTO>>>>

    /**
     * 选择的类别 ID
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectCategoryIdObservableDTO: MutableSharedStateFlow<String?>

    /**
     * 类别选中的事件
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    val categorySelectEvent: Flow<TallyCategoryDTO?>

    /**
     * 条目的点击
     */
    fun onCateItemClick(context: Context, categoryId: String)

    /**
     * 添加更多的类别
     */
    fun onAddMoreCategoryGroup(context: Context, cateGroupId: String)

    /**
     * 添加更多的类别
     */
    fun onAddMoreCategory(context: Context, cateGroupId: String)

}

class TallyCategoryUseCaseImpl : BaseUseCaseImpl(), TallyCategoryUseCase {

    override val isReturnDataData = MutableInitOnceData<Boolean>()

    override val initTabTypeData = MutableInitOnceData<BillCategoryTabType>().apply {
        this.value = BillCategoryTabType.Spending
    }

    override val tabSelectIndexTypeObservableDTO =
        MutableSharedStateFlow(initValue = initTabTypeData.value)

    override val dataObservableDTO = tallyService
        .subscribeWithDataBaseChanged {
            tallyCategoryService.getAllTallyCategoryGroups()
        }
        .map {
            it.map { group ->
                Pair(
                    first = group,
                    second = tallyCategoryService.getTallyCategoriesByGroupId(
                        groupId = group.uid
                    )
                )
            }
        }
        .sharedStateIn(scope = scope)

    override val selectCategoryIdObservableDTO = MutableSharedStateFlow<String?>(null)

    override val categorySelectEvent = combine(
        dataObservableDTO.map { groupList ->
            groupList.flatMap { it.second }
        },
        selectCategoryIdObservableDTO
    ) { categoryList, categoryId ->
        categoryList.find { it.uid == categoryId }
    }

    override fun onCateItemClick(context: Context, categoryId: String) {
        scope.launch {
            selectCategoryIdObservableDTO.value = categoryId
            if (isReturnDataData.value) { // 如果是返回数据的
                context.getActivity()?.let { act ->
                    val needReturnCategory = categorySelectEvent.filterNotNull().first()
                    act.setResult(
                        Activity.RESULT_OK,
                        Intent().apply {
                            this.putExtra("data", needReturnCategory)
                        }
                    )
                    act.finish()
                }
            } else {
                Router.with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_CREATE)
                    .putString("cateId", categoryId)
                    .forward()
            }

        }
    }

    override fun onAddMoreCategoryGroup(context: Context, cateGroupId: String) {
        Router.with(context)
            .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_GROUP_CREATE)
            .putString("cateGroupId", cateGroupId)
            .forward()
    }

    override fun onAddMoreCategory(context: Context, cateGroupId: String) {
        Router.with(context)
            .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_CREATE)
            .putString("cateGroupId", cateGroupId)
            .forward()
    }

}