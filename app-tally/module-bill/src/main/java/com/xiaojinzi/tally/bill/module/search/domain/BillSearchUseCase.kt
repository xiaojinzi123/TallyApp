package com.xiaojinzi.tally.bill.module.search.domain

import android.content.Context
import androidx.annotation.Keep
import androidx.paging.PagingData
import com.xiaojinzi.module.base.support.commonRouteResultService
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.ktx.addOrRemove
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.orNull
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.support.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Keep
private data class SearchKeyCondition(
    val categoryIdList: List<String>? = null,
    val accountIdList: List<String>? = null,
    val bookIdList: List<String>? = null,
    val billIdList: List<String>? = null,
)

enum class HavePictureType {
    All, No, Have
}

interface BillSearchUseCase : BaseUseCase {

    /**
     * 搜索的文本
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val searchContentObservableDTO: MutableSharedStateFlow<String>

    /**
     * 开始时间
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val startTimeObservableDTO: MutableSharedStateFlow<Long?>

    /**
     * 结束时间
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val endTimeObservableDTO: MutableSharedStateFlow<Long?>

    /**
     * 是否展示条件的视图
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isShowConditionObservableDTO: MutableSharedStateFlow<Boolean>

    /**
     * 图片条件
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val pictureTypeConditionObservableDTO: MutableSharedStateFlow<HavePictureType>

    /**
     * 最近搜索的历史
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val recentlySearchKeyListObservable: Flow<List<String>>

    /**
     * 所有的标签
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val allLabelListObservableDTO: Flow<List<TallyLabelDTO>>

    /**
     * 选中的标签的 Id 的集合
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectedLabelIdListObservableDTO: Flow<List<String>>

    /**
     * 所有的账本
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val allBookListObservableDTO: Flow<List<TallyBookDTO>>

    /**
     * 选中的账本的 Id 的集合
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectedBookIdListObservableDTO: Flow<List<String>>

    /**
     * 所有的账户
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val allAccountListObservableDTO: Flow<List<TallyAccountDTO>>

    /**
     * 选中的账户的 Id 的集合
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectedAccountIdListObservableDTO: Flow<List<String>>

    /**
     * 所有的类别组
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val allCategoryGroupListObservableDTO: Flow<List<TallyCategoryGroupDTO>>

    /**
     * 选中的类别组的 Id 的集合
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val selectedCategoryGroupIdListObservableDTO: Flow<List<String>>

    /**
     * 搜索结果
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val searchResultBillListObservableDTO: Flow<PagingData<BillDetailDayDTO>>

    /**
     * 搜索结果的数量
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val searchResultBillListCountObservableDTO: Flow<Int>

    /**
     * 选择时间段
     */
    fun selectTimeInterval(context: Context)

    /**
     * 反转标签的 Id 选择
     */
    fun toggleLabelIdSelect(id: String)

    /**
     * 反转账本的 Id 选择
     */
    fun toggleBookIdSelect(id: String)

    /**
     * 反转账户的 Id 选择
     */
    fun toggleAccountIdSelect(id: String)

    /**
     * 反转类别组的 Id 选择
     */
    fun toggleCategoryGroupIdSelect(id: String)

    /**
     * 重置条件
     */
    fun resetCondition()

    /**
     * 条件完成
     */
    fun conditionComplete()

    /**
     * 开始搜索
     */
    fun doSearch()

}

class BillSearchUseCaseImpl : BaseUseCaseImpl(), BillSearchUseCase {

    override val searchContentObservableDTO = MutableSharedStateFlow<String>(initValue = "")

    override val startTimeObservableDTO = MutableSharedStateFlow<Long?>(initValue = null)

    override val endTimeObservableDTO = MutableSharedStateFlow<Long?>(initValue = null)

    private val realSearchContentObservableFlow = searchContentObservableDTO.map {
        // 空和空字符串都处理成 null
        if (it.isNullOrEmpty()) {
            null
        } else {
            it
        }
    }

    private val billIdListListFromSearchKeyObservableDTO = realSearchContentObservableFlow.map {
        it?.let { targetKeyword ->
            val labelIdList = tallyLabelService
                .getAll()
                .asSequence()
                .filter {
                    val labelName = it.name.nameOfApp
                    labelName.contains(other = targetKeyword)
                }
                .map { it.uid }
                .toList()
            // labelId 换成相关联的 billId 的集合
            val billIdList: List<String> =
                tallyBillLabelService.getBillIdListByLabelIds(labelIds = labelIdList)
            billIdList
        }
    }

    private val categoryIdListFromSearchKeyObservableDTO = realSearchContentObservableFlow.map {
        it?.let { targetKeyword ->
            val cateIdList = tallyCategoryService
                .getAllTallyCategoryDetails()
                .asSequence()
                .filter {
                    val targetCateName =
                        it.category.name ?: app.getString(it.category.nameRsd!!)
                    val targetCateGroupName =
                        it.group.name ?: app.getString(it.group.nameRsd!!)
                    val b1 = targetCateGroupName.contains(other = targetKeyword)
                    val b2 = targetCateName.contains(other = targetKeyword)
                    b1 || b2
                }
                .map { it.category.uid }
                .toList()
            cateIdList
        }
    }

    private val accountIdListFromSearchKeyObservableDTO = realSearchContentObservableFlow.map {
        it?.let { targetKeyword ->
            val idList = tallyAccountService
                .getAll()
                .asSequence()
                .filter {
                    it.getStringItemVO().nameOfApp.contains(other = targetKeyword)
                }
                .map { it.uid }
                .toList()
            idList
        }
    }

    private val bookIdListFromSearchKeyObservableDTO = realSearchContentObservableFlow.map {
        it?.let { targetKeyword ->
            val idList = tallyBookService
                .getAll()
                .asSequence()
                .filter {
                    it.nameItem.nameOfApp.contains(other = targetKeyword)
                }
                .map { it.uid }
                .toList()
            idList
        }
    }

    override val isShowConditionObservableDTO = MutableSharedStateFlow(initValue = false)

    override val pictureTypeConditionObservableDTO =
        MutableSharedStateFlow(initValue = HavePictureType.All)

    override val recentlySearchKeyListObservable =
        MutableSharedStateFlow<List<String>>(initValue = emptyList())

    override val allLabelListObservableDTO =
        tallyService.subscribeWithDataBaseChanged { tallyLabelService.getAll() }

    override val selectedLabelIdListObservableDTO =
        MutableSharedStateFlow<List<String>>(initValue = emptyList())

    override val allBookListObservableDTO =
        tallyService.subscribeWithDataBaseChanged { tallyBookService.getAll() }

    override val selectedBookIdListObservableDTO =
        MutableSharedStateFlow<List<String>>(initValue = emptyList())

    override val allAccountListObservableDTO =
        tallyService.subscribeWithDataBaseChanged { tallyAccountService.getAll() }

    override val selectedAccountIdListObservableDTO =
        MutableSharedStateFlow<List<String>>(initValue = emptyList())

    override val allCategoryGroupListObservableDTO =
        tallyService.subscribeWithDataBaseChanged { tallyCategoryService.getAllTallyCategoryGroups() }

    override val selectedCategoryGroupIdListObservableDTO =
        MutableSharedStateFlow<List<String>>(initValue = emptyList())

    private val searchFlagObservableDTO = MutableSharedStateFlow(initValue = Unit)

    private val searchKeyConditionObjectObservableDTO = combine(
        categoryIdListFromSearchKeyObservableDTO,
        accountIdListFromSearchKeyObservableDTO,
        bookIdListFromSearchKeyObservableDTO,
        billIdListListFromSearchKeyObservableDTO,
    ) { categoryIdList, accountIdList, bookIdList, billIdList ->
        SearchKeyCondition(
            categoryIdList = categoryIdList?.orNull(),
            accountIdList = accountIdList?.orNull(),
            bookIdList = bookIdList?.orNull(),
            billIdList = billIdList?.orNull(),
        )
    }

    private val searchConditionObjectObservableDTO = com.xiaojinzi.support.ktx.combine(
        selectedLabelIdListObservableDTO,
        selectedBookIdListObservableDTO,
        selectedAccountIdListObservableDTO,
        selectedCategoryGroupIdListObservableDTO,
        realSearchContentObservableFlow,
        searchKeyConditionObjectObservableDTO,
    ) { labelIdList, bookIdList, accountList, cateGroupList, keyword, searchKeyConditionObject ->
        // 先根据 labelIdList 查询出相关的账单的 BillIdList
        val billIdList: List<String> =
            tallyBillLabelService.getBillIdListByLabelIds(labelIds = labelIdList)
        BillQueryConditionDTO(
            bookIdList = bookIdList.orNull(),
            billIdList = billIdList.orNull(),
            accountAboutIdList = accountList.orNull(),
            categoryGroupIdList = cateGroupList.orNull(),
            searchQueryInfo = SearchQueryConditionDTO(
                billIdList = searchKeyConditionObject.billIdList?.orNull(),
                categoryIdList = searchKeyConditionObject.categoryIdList?.orNull(),
                bookIdList = searchKeyConditionObject.bookIdList?.orNull(),
                aboutAccountIdList = searchKeyConditionObject.accountIdList?.orNull(),
                noteKey = keyword,
            )
        )
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override val searchResultBillListObservableDTO = combine(
        tallyService.dataBaseChangedObservable,
        searchFlagObservableDTO
    ) { _, _ ->
        searchConditionObjectObservableDTO.first()
    }.flatMapLatest { queryCondition ->
        tallyBillDetailQueryPagingService.subscribeCommonPageBillDetailObservable(
            billQueryCondition = queryCondition,
        )
    }
        .flowOn(context = Dispatchers.IO)

    override val searchResultBillListCountObservableDTO = combine(
        tallyService.dataBaseChangedObservable,
        searchFlagObservableDTO
    ) { _, _ ->
        searchConditionObjectObservableDTO.first()
    }.map { queryCondition ->
        tallyBillService.getCountByCondition(
            conditionBill = queryCondition,
        )
    }

    override fun selectTimeInterval(context: Context) {
        scope.launch (ErrorIgnoreContext){
            val currentTime = System.currentTimeMillis()
            val startTime = commonRouteResultService.selectDateTime(
                context = context,
                dateTime = currentTime,
                defTime = currentTime,
            )
            delay(600)
            val endTime = commonRouteResultService.selectDateTime(
                context = context,
                dateTime = currentTime,
                defTime = currentTime,
            )
            startTimeObservableDTO.value = startTime
            endTimeObservableDTO.value = endTime
        }
    }

    override fun toggleLabelIdSelect(id: String) {
        selectedLabelIdListObservableDTO.value = selectedLabelIdListObservableDTO.value.run {
            this.addOrRemove(element = id)
        }
    }

    override fun toggleBookIdSelect(id: String) {
        selectedBookIdListObservableDTO.value = selectedBookIdListObservableDTO.value.run {
            this.addOrRemove(element = id)
        }
    }

    override fun toggleAccountIdSelect(id: String) {
        selectedAccountIdListObservableDTO.value = selectedAccountIdListObservableDTO.value.run {
            this.addOrRemove(element = id)
        }
    }

    override fun toggleCategoryGroupIdSelect(id: String) {
        selectedCategoryGroupIdListObservableDTO.value =
            selectedCategoryGroupIdListObservableDTO.value.run {
                this.addOrRemove(element = id)
            }
    }

    override fun resetCondition() {
        selectedLabelIdListObservableDTO.value = emptyList()
        selectedBookIdListObservableDTO.value = emptyList()
        selectedAccountIdListObservableDTO.value = emptyList()
        selectedCategoryGroupIdListObservableDTO.value = emptyList()
    }

    override fun conditionComplete() {
        doSearch()
    }

    override fun doSearch() {
        isShowConditionObservableDTO.value = false
        searchFlagObservableDTO.tryEmit(value = Unit)
    }

}
