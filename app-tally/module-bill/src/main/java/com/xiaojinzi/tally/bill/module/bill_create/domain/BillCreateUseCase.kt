package com.xiaojinzi.tally.bill.module.bill_create.domain

import android.content.Context
import androidx.annotation.Keep
import androidx.annotation.UiContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.component.withHostAndPath
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.domain.CostUseCase
import com.xiaojinzi.module.base.domain.CostUseCaseImpl
import com.xiaojinzi.module.base.support.*
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.init.AppInstance
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.newUUid
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.DialogConfirmResult
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.support.*
import com.xiaojinzi.tally.bill.R
import com.xiaojinzi.tally.bill.module.bill_create.view.BillCreateTabType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.properties.Delegates

@Keep
data class ImageDTO(
    val uid: String = newUUid(),
    // 当没有上传成功的时候, 就是 null 的
    val url: String? = null,
    // 本地文件, 如果有就可以显示一下先
    val localFile: File? = null,
) {
    init {
        if (url.isNullOrEmpty() && localFile == null) {
            notSupportError("ImageDTO is invalid")
        }
    }
}

enum class CostType {

    /**
     * 相对的, 就是 cost 值的正负不代表收入或者支出
     * 100 在配上支出类别就是支出 100, 配上收入类别就是收入 100
     * -100 在配上支出类别就是收入 100, 配上收入类别就是支出 100
     */
    Relative,

    /**
     * 绝对的, 就是 cost 的值的正负代表的就是收入或者支出
     * 100 在配上支出类别就是收入 100, 配上收入类别就是收入 100
     * -100 在配上支出类别就是支出 100, 配上收入类别就是支出 100
     */
    Absolute

}

interface BillCreateUseCase : BaseUseCase {

    companion object {
        const val Tag: String = "BillCreateUseCase"
    }

    /**
     * 费用的计算相关
     */
    val costUseCase: CostUseCase

    /**
     * 转账相关的逻辑
     */
    val billCreateTransferUseCase: BillCreateTransferUseCase

    /**
     * 报销相关的逻辑
     */
    val billCreateReimbursementUseCase: BillCreateReimbursementUseCase

    /**
     * 账单的用途
     */
    val billUsageInitData: MutableInitOnceData<TallyBillUsageDTO>

    /**
     * cost 的类型初始化的数据
     */
    val costTypeInitData: MutableInitOnceData<CostType>

    /**
     * tab 初始化的数据
     */
    val tabIndexInitData: MutableInitOnceData<BillCreateTabType>

    /**
     * 是否必须选择一个账户
     */
    val isMustSelectAccountInitData: MutableInitOnceData<Boolean>

    /**
     * 类别的初始化 Id
     */
    val categoryInitData: MutableInitOnceData<String?>

    /**
     * 账户的初始化 Id
     */
    val accountInitData: MutableInitOnceData<String?>

    /**
     * 账本的初始化 Id
     */
    val bookInitData: MutableInitOnceData<String?>

    /**
     * 账单 ID 的初始化
     */
    val billIdInitData: MutableInitOnceData<String?>

    /**
     * 是否是报销的类型
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isReimbursementTypeObservableDTO: SharedStateFlow<Boolean>

    /**
     * 初始化的账单对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val billInitDataObservableDTO: Flow<TallyBillDetailDTO?>

    /**
     * 初始化的类别的对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val categoryInitDataObservableDTO: Flow<TallyCategoryWithGroupDTO?>

    /**
     * 是否是更新账单
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val isUpdateBillObservableDTO: SharedStateFlow<Boolean>

    /**
     * 收入的类别
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val inComeCategoryListObservableDTO: Flow<List<Pair<TallyCategoryGroupDTO, List<TallyCategoryDTO>>>>

    /**
     * 支出的类别
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val spendingCategoryListObservableDTO: Flow<List<Pair<TallyCategoryGroupDTO, List<TallyCategoryDTO>>>>

    /**
     * 标题的选中
     * @see BillCreateTabType
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectTabTypeObservableVO: MutableSharedStateFlow<BillCreateTabType>

    /**
     * 类别组的选择
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val groupSelectObservableDTO: MutableSharedStateFlow<TallyCategoryGroupDTO?>

    /**
     * 类别的选择
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val categorySelectObservableDTO: MutableSharedStateFlow<TallyCategoryDTO?>

    /**
     * null 表示使用当前时间
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val timeObservableDTO: MutableSharedStateFlow<Long?>

    /**
     * 备注的信息
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val noteStrObservableDTO: MutableSharedStateFlow<String?>

    /**
     * 报销的类型
     * [ReimburseType]
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val reimburseTypeObservableDTO: MutableSharedStateFlow<ReimburseType>

    /**
     * 是否不计入收支
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isNotIncludedInIncomeAndExpenditureObservableDTO: MutableSharedStateFlow<Boolean>

    /**
     * 选择好的标签的集合
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val selectedLabelListObservableDTO: MutableSharedStateFlow<List<TallyLabelDTO>>

    /**
     * 选择好的账户, 如果为空, 那就记在默认账户上
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectedAccountObservableDTO: MutableSharedStateFlow<TallyAccountDTO>

    /**
     * 选择好的账本
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectedBookObservableDTO: MutableSharedStateFlow<TallyBookDTO?>

    /**
     * 选择好的图片
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val selectedImageObservableDTO: MutableSharedStateFlow<List<ImageDTO>>

    /**
     * 是否显示图片上传的 Dialog
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val isShowImageUploadDialogObservableDTO: MutableSharedStateFlow<Boolean>

    /**
     * 当显示的了上传图片的 Dialog 的时候会显示这个进度
     * []0-100]
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val imageUploadProgressObservableDTO: MutableSharedStateFlow<Float>

    /**
     * 是否能下一步的错误提示 string 的 resId
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val canNextTipObservableDTO: Flow<Int?>

    /**
     * 是否能下一步的视图
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val canNextObservableDTO: Flow<Boolean>

    /**
     * 初始化数据
     */
    fun initData(
        usage: TallyBillUsageDTO = TallyBillUsageDTO.Nothing,
        tabIndex: BillCreateTabType = BillCreateTabType.Spending,
        costType: CostType = CostType.Relative,
        billId: String? = null,
        time: Long? = null,
        reimbursementBillId: String? = null,
        // 默认是 false, false 会采用默认的账户
        isMustSelectAccount: Boolean = false,
        categoryId: String? = null,
        // 这个 cost 的正负表示的意思是：>0 支出 <0 收入
        cost: Float? = null,
        accountId: String? = null,
        outAccountId: String? = null,
        inAccountId: String? = null,
        bookId: String? = null,
        note: String? = null,
    )

    /**
     * 尝试选中类别组
     */
    fun selectCateGroup(groupId: String?)

    /**
     * 尝试选中类别
     */
    fun selectCate(categoryId: String?)

    /**
     * 添加或者更新一个账单
     */
    fun addOrUpdateBill(@UiContext context: Context)

    /**
     * 去选择标签
     */
    fun toChooseLabelList(@UiContext context: Context)

    /**
     * 去选择类别
     */
    fun toChooseCategory(@UiContext context: Context)

    /**
     * 去选择账户
     */
    fun toChooseAccount(@UiContext context: Context)

    /**
     * 去选择账本
     */
    fun toChooseBook(@UiContext context: Context)

    /**
     * 删除标签
     */
    fun deleteLabel(labelId: String)

    /**
     * 删除图片
     */
    fun deleteImage(uid: String)

    /**
     * 去写备注
     */
    fun toFillNote(@UiContext context: Context)

    /**
     * 去选择图片
     */
    fun toChooseImage(@UiContext context: Context)

    /**
     * 去创建类别对象
     */
    fun toCreateCate(@UiContext context: Context, groupId: String)

    /**
     * 去更新类别对象
     */
    fun toUpdateCate(@UiContext context: Context, cateId: String)

    /**
     * 去创建类别组对象
     */
    fun toCreateCateGroup(@UiContext context: Context)

    /**
     * 去更新类别组对象
     */
    fun toUpdateCateGroup(@UiContext context: Context, cateGroupId: String)

    /**
     * 去选择报销类型
     */
    fun toChooseReimburseType(@UiContext context: Context)

}

@FlowPreview
class BillCreateUseCaseImpl(
    override val costUseCase: CostUseCase = CostUseCaseImpl(),
    override val billCreateTransferUseCase: BillCreateTransferUseCase = BillCreateTransferUseCaseImpl(),
    override val billCreateReimbursementUseCase: BillCreateReimbursementUseCase = BillCreateReimbursementUseCaseImpl(),
) : BaseUseCaseImpl(), BillCreateUseCase {

    override val billUsageInitData = MutableInitOnceData<TallyBillUsageDTO>()

    override val costTypeInitData = MutableInitOnceData<CostType>()

    override val tabIndexInitData = MutableInitOnceData<BillCreateTabType>()

    override val isMustSelectAccountInitData = MutableInitOnceData<Boolean>()

    override val categoryInitData = MutableInitOnceData<String?>()

    override val accountInitData = MutableInitOnceData<String?>()

    override val bookInitData = MutableInitOnceData<String?>()

    override val billIdInitData = MutableInitOnceData<String?>()

    override val isReimbursementTypeObservableDTO =
        billCreateReimbursementUseCase.reimbursementBillIdInitData
            .valueStateFlow
            .map { it != null }
            .sharedStateIn(
                scope = scope,
                initValue = false,
            )

    override val billInitDataObservableDTO = billIdInitData
        .valueStateFlow
        .map { billId ->
            billId?.run {
                tallyBillService.getDetailById(id = billId)
            }
        }
        .sharedStateIn(scope = scope)

    override val categoryInitDataObservableDTO = categoryInitData
        .valueStateFlow
        .map { cateId ->
            if (cateId == null && rememberBillTypeService.autoInferType.value) {
                val type = tabIndexInitData.value.index
                rememberBillTypeService.getRecordByTime(type = type)?.apply {
                    return@map tallyCategoryService.getTallyCategoryDetailById(id = uid)
                }

            }
            cateId?.run {
                tallyCategoryService.getTallyCategoryDetailById(id = this)
            }
        }

    override val isUpdateBillObservableDTO = billInitDataObservableDTO
        .map {
            it != null
        }
        .sharedStateIn(scope = scope)

    override val inComeCategoryListObservableDTO = tallyCategoryService
        .groupWithCategoryListObservable
        .map { list ->
            list.filter { it.first.type == TallyCategoryGroupTypeDTO.Income }
        }

    override val spendingCategoryListObservableDTO = tallyCategoryService
        .groupWithCategoryListObservable
        .map { list ->
            list.filter { it.first.type == TallyCategoryGroupTypeDTO.Spending }
        }

    override val selectTabTypeObservableVO: MutableSharedStateFlow<BillCreateTabType> =
        tabIndexInitData
            .valueStateFlow
            .sharedStateIn(scope = scope)

    /**
     * 类别组的选择
     */
    override val groupSelectObservableDTO = categoryInitDataObservableDTO
        .map { it?.group }
        .flatMapConcat { cateGroup ->
            if (cateGroup == null) {
                spendingCategoryListObservableDTO
                    .filter { it.isNotEmpty() }
                    .map {
                        val currTabType = selectTabTypeObservableVO.first()
                        if (currTabType == BillCreateTabType.Spending) {
                            it.first().first
                        } else {
                            null
                        }
                    }
            } else {
                flowOf(cateGroup)
            }
        }
        .sharedStateIn(
            scope = scope,
            isTakeOne = true
        )

    /**
     * 类别的选择
     */
    override val categorySelectObservableDTO = categoryInitDataObservableDTO
        .map { it?.category }
        .sharedStateIn(initValue = null, scope = scope)

    /**
     * null 表示使用当前时间
     */
    override val timeObservableDTO = MutableSharedStateFlow<Long?>(initValue = null)

    /**
     * 备注的信息
     */
    override val noteStrObservableDTO = MutableSharedStateFlow<String?>(initValue = null)

    override val reimburseTypeObservableDTO =
        MutableSharedStateFlow(initValue = ReimburseType.NoReimburse)

    override val isNotIncludedInIncomeAndExpenditureObservableDTO =
        MutableSharedStateFlow(initValue = false)

    override val selectedLabelListObservableDTO =
        MutableSharedStateFlow<List<TallyLabelDTO>>(initValue = emptyList())

    override val selectedAccountObservableDTO = accountInitData
        .valueStateFlow
        .map { accountId ->
            accountId?.run {
                tallyAccountService.getByUid(uid = this)
            } ?: tallyAccountService.getDefaultAccount()
        }
        .sharedStateIn(scope = scope)

    override val selectedBookObservableDTO: MutableSharedStateFlow<TallyBookDTO?> = bookInitData
        .valueStateFlow
        .map { bookId ->
            bookId?.run {
                tallyBookService.getBookById(uid = this)
            } ?: tallyBookService.getDefaultBook()
        }
        .sharedStateIn(scope = scope)

    override val selectedImageObservableDTO =
        MutableSharedStateFlow<List<ImageDTO>>(initValue = emptyList())

    override val isShowImageUploadDialogObservableDTO =
        MutableSharedStateFlow(initValue = false)

    override val imageUploadProgressObservableDTO =
        MutableSharedStateFlow(initValue = 0f)

    override val canNextTipObservableDTO = com.xiaojinzi.support.ktx.combine(
        selectTabTypeObservableVO,
        costUseCase.costIsCorrectFormatObservableDTO,
        selectedAccountObservableDTO,
        categorySelectObservableDTO,
        billCreateTransferUseCase.selectedOutAccountObservableDTO,
        billCreateTransferUseCase.selectedInAccountObservableDTO,
    ) { tabIndex, isCostMathMatch, selectedAccount, selectCategory, selectOutTransferAccount, selectInTransferAccount ->
        LogSupport.d(
            tag = BillCreateUseCase.Tag,
            content = "tabIndex=$tabIndex",
            keywords = arrayOf(
                TallyLogKeyword.BILL_CREATE_CAN_NEXT
            )
        )
        LogSupport.d(
            tag = BillCreateUseCase.Tag,
            content = "isCostMathMatch=$isCostMathMatch",
            keywords = arrayOf(
                TallyLogKeyword.BILL_CREATE_CAN_NEXT
            )
        )
        LogSupport.d(
            tag = BillCreateUseCase.Tag,
            content = "selectCategory=$selectCategory",
            keywords = arrayOf(
                TallyLogKeyword.BILL_CREATE_CAN_NEXT
            )
        )
        LogSupport.d(
            tag = BillCreateUseCase.Tag,
            content = "selectOutTransferAccount=$selectOutTransferAccount",
            keywords = arrayOf(
                TallyLogKeyword.BILL_CREATE_CAN_NEXT
            )
        )
        LogSupport.d(
            tag = BillCreateUseCase.Tag,
            content = "selectInTransferAccount=$selectInTransferAccount",
            keywords = arrayOf(
                TallyLogKeyword.BILL_CREATE_CAN_NEXT
            )
        )
        val errorTipRsd: Int? = if (isCostMathMatch) {
            // 如果是报销的类型
            if (isReimbursementTypeObservableDTO.value) {
                null
            } else {
                when (tabIndex) {
                    BillCreateTabType.Spending, BillCreateTabType.Income -> {
                        if (selectCategory == null) {
                            R.string.res_str_err_tip3
                        } else {
                            if (selectedAccount == null && isMustSelectAccountInitData.value) {
                                R.string.res_str_err_tip4
                            } else {
                                null
                            }
                        }
                    }

                    BillCreateTabType.Transfer -> {
                        if (selectInTransferAccount == null || selectOutTransferAccount == null) {
                            R.string.res_str_err_tip5
                        } else {
                            null
                        }
                    }
                }
            }
        } else {
            R.string.res_str_err_tip2
        }
        errorTipRsd
    }
        .flowOn(context = Dispatchers.IO)
        .sharedStateIn(scope = scope, initValue = R.string.res_str_err_tip0)

    /**
     * 是否可以继续下去
     */
    override val canNextObservableDTO = canNextTipObservableDTO
        .map { it == null }
        .sharedStateIn(scope = scope, initValue = false)

    override fun initData(
        usage: TallyBillUsageDTO,
        tabIndex: BillCreateTabType,
        costType: CostType,
        billId: String?,
        time: Long?,
        reimbursementBillId: String?,
        isMustSelectAccount: Boolean,
        categoryId: String?,
        cost: Float?,
        accountId: String?,
        outAccountId: String?,
        inAccountId: String?,
        bookId: String?,
        note: String?
    ) {
        billUsageInitData.value = usage
        costTypeInitData.value = costType
        categoryInitData.value = categoryId
        isMustSelectAccountInitData.value = isMustSelectAccount
        if (billId == null) {
            tabIndexInitData.value = tabIndex
            timeObservableDTO.value = time
            costUseCase.costAppend(
                target = cost?.toString() ?: "",
                isRemoveDefaultInput = true,
            )
            accountInitData.value = accountId
            bookInitData.value = bookId
            noteStrObservableDTO.value = note
            billCreateTransferUseCase.initData(
                outAccountId = outAccountId,
                inAccountId = inAccountId,
            )
            billIdInitData.value = null
            billCreateReimbursementUseCase.reimbursementBillIdInitData.value = reimbursementBillId
        } else {
            billIdInitData.value = billId
        }
    }

    override fun selectCateGroup(groupId: String?) {
        val targetCateGroup = groupId?.run {
            tallyCategoryService
                .categoryGroupListObservable
                .value
                .find { it.uid == this }
        }
        if (targetCateGroup != groupSelectObservableDTO.value) {
            // 如果选择了不同的类别组, 那么取消类别的选择
            selectCate(categoryId = null)
        }
        groupSelectObservableDTO.value = targetCateGroup
    }

    override fun selectCate(categoryId: String?) {
        categorySelectObservableDTO.value = categoryId?.run {
            tallyCategoryService
                .categoryListObservable
                .value
                .find { it.uid == this }
        }
    }

    override fun addOrUpdateBill(@UiContext context: Context) {
        scope.launch(ErrorIgnoreContext) {
            if (!canNextObservableDTO.value) {
                canNextTipObservableDTO.value?.let {
                    tallyAppToast(contentRsd = it)
                }
                return@launch
            }
            // 作用
            val usage = billUsageInitData.value
            // 是否是更新
            val isUpdateBill = isUpdateBillObservableDTO.value
            // 是否是转账
            val isTransfer = selectTabTypeObservableVO.value == BillCreateTabType.Transfer
            // 是否是报销类型的账单
            val isReimbursementType = isReimbursementTypeObservableDTO.value
            // 选择的类别 Id
            val categoryId: String? = categorySelectObservableDTO.value?.uid
            // 计算钱的值, 有可能负数
            val cost =
                costUseCase.calculateResult(target = costUseCase.costStrObservableDTO.value.strValue)
            // 获取类别的对象
            var category: TallyCategoryDTO? = categoryId?.run {
                tallyCategoryService.getTallyCategoryById(id = this)
            }
            val accountId: String? = selectedAccountObservableDTO.value.uid
            val bookId = selectedBookObservableDTO.value?.uid
            val isNotIncludedInIncomeAndExpenditure = if (isTransfer) {
                false
            } else {
                isNotIncludedInIncomeAndExpenditureObservableDTO.value
            }
            // 是否是支出
            val isSpending = when {
                isReimbursementType || isTransfer -> true
                else -> {
                    // 必须要查询到
                    val cateGroup =
                        tallyCategoryService.getTallyCategoryGroupById(id = category!!.groupId)!!
                    when (cateGroup.type) {
                        TallyCategoryGroupTypeDTO.Spending -> true
                        else -> false
                    }
                }
            }
            val targetAccount: TallyAccountDTO = when {
                isReimbursementType -> {
                    tallyAccountService.getByUid(
                        uid = billCreateReimbursementUseCase.reimbursementBillInitDataObservableDTO.value!!.bill.accountId
                    )!!
                }

                isTransfer -> {
                    tallyAccountService.getByUid(
                        uid = billCreateTransferUseCase.selectedOutAccountObservableDTO.value!!.uid
                    )!!
                }

                else -> {
                    if (accountId == null) {
                        tallyAccountService.getDefaultAccount()
                    } else {
                        tallyAccountService.getByUid(
                            uid = accountId
                        )!!
                    }
                }
            }
            var transferTargetAccount = when {
                isTransfer -> {
                    tallyAccountService.getByUid(
                        uid = billCreateTransferUseCase.selectedInAccountObservableDTO.value!!.uid
                    )!!
                }

                else -> {
                    null
                }
            }

            val targetBook: TallyBookDTO? = when {
                isReimbursementType -> {
                    tallyBookService.getBookById(
                        uid = billCreateReimbursementUseCase.reimbursementBillInitDataObservableDTO.value!!.bill.bookId
                    )
                }

                else -> {
                    if (bookId == null) {
                        tallyBookService.getDefaultBook()
                    } else {
                        tallyBookService.getBookById(
                            uid = bookId
                        )
                    }
                }
            }

            Assert.assertNotNull(value = targetAccount)
            Assert.assertNotNull(value = targetBook)

            val realCost = when {
                isReimbursementType -> {
                    cost
                }

                else -> {
                    when (costTypeInitData.value) {
                        CostType.Relative -> {
                            if (isSpending) {
                                -cost
                            } else {
                                cost
                            }
                        }

                        CostType.Absolute -> {
                            cost
                        }
                    }
                }
            }

            // 做最后值的确认
            when {
                isReimbursementType -> {
                    Assert.assertNotNull(value = billCreateReimbursementUseCase.reimbursementBillInitDataObservableDTO.value)
                }

                isTransfer -> {
                    // 转账这个值不需要
                    category = null
                    Assert.assertNotNull(value = transferTargetAccount)
                }

                else -> {
                    // 普通记账不需要这个字段
                    transferTargetAccount = null
                    Assert.assertNotNull(value = category)
                }
            }

            // 做图片的上传
            val imageDTOList = selectedImageObservableDTO.value
            val totalImageSize = imageDTOList.size

            // 图片个数控制为 3 个
            if (totalImageSize > 3) {
                tallyAppToast(content = app.getString(R.string.res_str_err_tip9))
                return@launch
            }

            var completeCount by Delegates.observable(initialValue = 0) { _, _, newValue ->
                imageUploadProgressObservableDTO.value =
                    (100f * (newValue.toFloat() / totalImageSize)).coerceIn(
                        minimumValue = 0f, maximumValue = 100f
                    )
            }
            // 需要上传的图片列表
            val needUploadList = imageDTOList.filter { it.url == null }
            // 拿到服务器的图片地址
            val imageUrlList = if (needUploadList.isNotEmpty()) {
                isShowImageUploadDialogObservableDTO.value = true
                try {
                    val resultList = imageDTOList
                        .map { item ->
                            val result =
                                item.url ?: if (AppInstance.isDebug) {
                                    TallyService.testImageList.random()
                                } else {
                                    imageUploadService.upload(file = item.localFile!!)
                                }
                            completeCount++
                            result
                        }
                    // .awaitAll()
                    // 给 UI 那边留一个动画的时间
                    delay(200)
                    resultList
                } catch (e: Exception) {
                    tallyAppToast(content = app.getString(R.string.res_str_err_tip8))
                    return@launch
                } finally {
                    isShowImageUploadDialogObservableDTO.value = false
                }
            } else {
                imageDTOList.mapNotNull { it.url }
            }

            val targetBillType = when {
                isTransfer -> {
                    TallyBillTypeDTO.Transfer
                }

                isReimbursementType -> {
                    TallyBillTypeDTO.Reimbursement
                }

                else -> {
                    TallyBillTypeDTO.Normal
                }
            }
            val labelIdList = selectedLabelListObservableDTO.value.map { it.uid }
            val targetReimburseBillId =
                billCreateReimbursementUseCase.reimbursementBillInitDataObservableDTO.value?.bill?.uid
            val reimburseType = when {
                isTransfer || isReimbursementType -> ReimburseType.NoReimburse
                else -> {
                    reimburseTypeObservableDTO.value
                }
            }
            // 报销剩余的钱
            val reimburseRestCost =
                billCreateReimbursementUseCase.reimburseRestCostObservableDTO.first()
            var isFinish = true
            // 开启一个事务
            tallyService.withTransaction {
                if (isUpdateBill) {
                    billInitDataObservableDTO.value?.let { billDetailDto ->
                        val billDto = tallyBillService.getById(id = billDetailDto.bill.uid)!!
                        if (targetBillType in listOf(TallyBillTypeDTO.Transfer)) { // 如果目标类型是转账, 提示删除关联的报销单
                            val reimburseBillIdList = tallyBillService.getBillIdListByCondition(
                                condition = BillQueryConditionDTO(
                                    reimburseBillIdList = listOf(billDto.uid)
                                )
                            )
                            if (reimburseBillIdList.isNotEmpty()) {
                                val dialogConfirmResult = tallyCommonService.confirmDialog(
                                    context = context,
                                    message = R.string.res_str_tip9.toStringItemDTO(),
                                )
                                if (dialogConfirmResult == DialogConfirmResult.Positive) {
                                    tallyBillService.deleteByIds(ids = reimburseBillIdList)
                                } else {
                                    isFinish = false
                                    return@withTransaction
                                }
                            }
                        }
                        tallyBillService
                            .update(
                                target = billDto.copy(
                                    usage = usage,
                                    type = targetBillType,
                                    time = (timeObservableDTO.value) ?: System.currentTimeMillis(),
                                    accountId = targetAccount.uid,
                                    transferTargetAccountId = transferTargetAccount?.uid,
                                    bookId = targetBook!!.uid,
                                    categoryId = category?.uid,
                                    cost = realCost.tallyCostToLong(),
                                    note = noteStrObservableDTO.value,
                                    reimburseType = reimburseType,
                                    reimburseBillId = targetReimburseBillId,
                                    isNotIncludedInIncomeAndExpenditure = isNotIncludedInIncomeAndExpenditure,
                                ),
                                labelIdList = labelIdList,
                                imageUrlList = imageUrlList,
                            )
                    }
                } else // 卡位
                {
                    if (isReimbursementType) { // 是否是报销的类型
                        billCreateReimbursementUseCase.reimbursementBillInitDataObservableDTO.value?.let { reimburseBillDTO ->
                            val billDTO = reimburseBillDTO.bill
                            tallyBillService.insertBill(
                                targetBill = billDTO
                                    .copy(
                                        usage = usage,
                                        type = targetBillType,
                                        time = (timeObservableDTO.value)
                                            ?: System.currentTimeMillis(),
                                        accountId = targetAccount.uid,
                                        cost = realCost.tallyCostToLong(),
                                        note = noteStrObservableDTO.value,
                                        reimburseType = ReimburseType.NoReimburse,
                                        reimburseBillId = targetReimburseBillId,
                                        isNotIncludedInIncomeAndExpenditure = isNotIncludedInIncomeAndExpenditure,
                                    )
                                    .toTallyBillInsertDTO(),
                                labelIdList = labelIdList,
                                imageUrlList = imageUrlList,
                            )
                        }
                    } else {
                        // 新增账单
                        tallyBillService
                            .insertBill(
                                targetBill = TallyBillInsertDTO(
                                    usage = usage,
                                    type = targetBillType,
                                    time = (timeObservableDTO.value) ?: System.currentTimeMillis(),
                                    accountId = targetAccount.uid,
                                    transferTargetAccountId = transferTargetAccount?.uid,
                                    bookId = targetBook!!.uid,
                                    categoryId = category?.uid,
                                    cost = realCost.tallyCostToLong(),
                                    note = noteStrObservableDTO.value,
                                    reimburseType = reimburseType,
                                    reimburseBillId = null,
                                    isNotIncludedInIncomeAndExpenditure = isNotIncludedInIncomeAndExpenditure,
                                ),
                                labelIdList = labelIdList,
                                imageUrlList = imageUrlList,
                            )
                        //插入记录
                        rememberBillTypeService.updateRecordBillType(
                            categoryId = category!!.uid,
                            time = (timeObservableDTO.value) ?: System.currentTimeMillis(),
                            if (isSpending) 0 else 1
                        )

                    }
                }
                // 对原始账单的报销类型进行调整
                if (isReimbursementType) {
                    billCreateReimbursementUseCase.reimbursementBillInitDataObservableDTO.value?.let { reimburseBillDTO ->
                        val billDTO = reimburseBillDTO.bill
                        // 如果现在的值已经大于剩余的报销的值了, 就询问一下是否标记账单的报销类型为 已报销
                        if (realCost >= (reimburseRestCost ?: 0f)) {
                            if (billDTO.reimburseType == ReimburseType.WaitReimburse) {
                                val dialogConfirmResult = tallyCommonService.confirmDialog(
                                    context = context,
                                    message = R.string.res_str_tip7.toStringItemDTO(),
                                )
                                if (dialogConfirmResult == DialogConfirmResult.Positive) {
                                    tallyBillService.update(
                                        target = billDTO.copy(
                                            reimburseType = ReimburseType.Reimbursed,
                                        ),
                                    )
                                }
                            }
                        } else {
                            if (billDTO.reimburseType == ReimburseType.Reimbursed) {
                                val dialogConfirmResult = tallyCommonService.confirmDialog(
                                    context = context,
                                    message = R.string.res_str_tip8.toStringItemDTO(),
                                )
                                if (dialogConfirmResult == DialogConfirmResult.Positive) {
                                    tallyBillService.update(
                                        target = billDTO.copy(
                                            reimburseType = ReimburseType.WaitReimburse,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (isFinish) {
                context.tryFinishActivity()
            }
        }
    }

    override fun toChooseLabelList(@UiContext context: Context) {
        scope.launch(context = ErrorIgnoreContext) {
            val selectLabelIdList = selectedLabelListObservableDTO
                .value
                .map { it.uid }
                .toTypedArray()
            val targetIntent = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_LABEL_LIST)
                .putBoolean("isReturnData", true)
                .putStringArray("selectIdList", selectLabelIdList)
                .requestCodeRandom()
                .intentAwait()
            val labelIdList = targetIntent.getStringArrayExtra("labelIds")!!
            // 选择的标签
            val labelDTOList = tallyLabelService.getByIds(labelIdList.toList())
            selectedLabelListObservableDTO.value = labelDTOList
        }
    }

    override fun toChooseCategory(@UiContext context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val category: TallyCategoryDTO = context
                .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_CATEGORY)
                .putBoolean("isReturnData", true)
                .requestCodeRandom()
                .intentAwait()
                .getParcelableExtra("data") ?: return@launch
            selectCate(categoryId = category.uid)
        }
    }

    override fun toChooseAccount(@UiContext context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val selectIdList: List<String> = context
                .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_ACCOUNT_SELECT)
                .requestCodeRandom()
                .intentAwait()
                .getStringArrayExtra("data")
                ?.toList() ?: emptyList()
            // 选择好的账户
            val targetAccount = tallyAccountService.getByUid(uid = selectIdList.first())
            selectedAccountObservableDTO.value = targetAccount!!
        }
    }

    override fun toChooseBook(@UiContext context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val selectIdList = context
                .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_BILL_BOOK_SELECT)
                .putBoolean("isSingleSelect", true)
                .apply {
                    selectedBookObservableDTO.value?.let {
                        this.putStringArray("selectIdList", listOf(it.uid).toTypedArray())
                    }
                }
                .requestCodeRandom()
                .intentAwait()
                .getStringArrayExtra("data")
                ?.toList() ?: emptyList()
            // 选择好的账单 ID
            val targetResultBookId = selectIdList.first()
            selectedBookObservableDTO.value = tallyBookService.getBookById(uid = targetResultBookId)
        }
    }

    override fun deleteLabel(targetLabelId: String) {
        selectedLabelListObservableDTO.value = selectedLabelListObservableDTO.value
            .filter {
                it.uid != targetLabelId
            }
    }

    override fun deleteImage(uid: String) {
        selectedImageObservableDTO.value = selectedImageObservableDTO
            .value
            .filter {
                it.uid != uid
            }
    }

    override fun toFillNote(@UiContext context: Context) {
        scope.launch(ErrorIgnoreContext) {
            noteStrObservableDTO.value = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_NOTE)
                .putString("data", noteStrObservableDTO.value)
                .requestCodeRandom()
                .intentAwait()
                .getStringExtra("data") ?: ""
        }
    }

    override fun toChooseImage(@UiContext context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val isShowImagePrivacyTip =
                spService.getBool(key = "isShowImagePrivacyTip") ?: true
            if (isShowImagePrivacyTip) {
                // 先同意图片使用的隐私协议
                val action = suspendCoroutine<Int> { cot ->
                    MaterialAlertDialogBuilder(context)
                        .setMessage(R.string.res_str_desc8)
                        .setOnCancelListener {
                            cot.resume(value = -1)
                        }
                        .setNegativeButton(R.string.res_str_disagree) { dialog, _ ->
                            cot.resume(value = 0)
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.res_str_agree) { dialog, _ ->
                            cot.resume(value = 1)
                            dialog.dismiss()
                        }
                        .show()
                }
                if (action == 1) {
                    spService.putBool(key = "isShowImagePrivacyTip", value = false)
                } else {
                    return@launch
                }
            }
            // 在去选择图片
            val oldList = selectedImageObservableDTO.value
            selectedImageObservableDTO.value =
                oldList + imageSelectService
                    .selectMultiple(context = context)
                    .map {
                        ImageDTO(
                            localFile = imageSelectService.copyFileToCacheFromUri(uri = it)
                        )
                    }
        }
    }

    override fun toCreateCate(@UiContext context: Context, groupId: String) {
        scope.launch(ErrorIgnoreContext) {
            val cateId: String? = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_CREATE)
                .putString("cateGroupId", groupId)
                .requestCodeRandom()
                .intentAwait()
                .getStringExtra("cateId")
            cateId?.let {
                selectCate(categoryId = it)
            }
        }
    }

    override fun toUpdateCate(@UiContext context: Context, cateId: String) {
        scope.launch(ErrorIgnoreContext) {
            val cateId: String? = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_CREATE)
                .putString("cateId", cateId)
                .requestCodeRandom()
                .intentAwait()
                .getStringExtra("cateId")
            cateId?.let {
                selectCate(categoryId = it)
            }
        }
    }

    override fun toCreateCateGroup(@UiContext context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val cateGroupId: String? = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_GROUP_CREATE)
                .putSerializable(
                    "cateGroupType",
                    selectTabTypeObservableVO.value.toTallyCategoryGroupType()
                )
                .requestCodeRandom()
                .intentAwait()
                .getStringExtra("cateGroupId")
            cateGroupId?.let {
                selectCateGroup(groupId = it)
            }
        }
    }

    override fun toUpdateCateGroup(@UiContext context: Context, cateGroupId: String) {
        Router.with(context)
            .hostAndPath(TallyRouterConfig.TALLY_CATEGORY_GROUP_CREATE)
            .putString("cateGroupId", cateGroupId)
            .forward()
    }

    override fun toChooseReimburseType(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val selectIndex = commonRouteResultService
                .menuSelect(
                    context = context,
                    items = listOf(
                        R.string.res_str_no_reimbursement,
                        R.string.res_str_reimbursement,
                        R.string.res_str_reimbursed,
                    ).map { it.toStringItemDTO() }
                )
            val reimburseType = when (selectIndex) {
                0 -> ReimburseType.NoReimburse
                1 -> ReimburseType.WaitReimburse
                2 -> ReimburseType.Reimbursed
                else -> notSupportError()
            }
            reimburseTypeObservableDTO.value = reimburseType
        }
    }

    override fun destroy() {
        super.destroy()
        costUseCase.destroy()
        billCreateTransferUseCase.destroy()
        billCreateReimbursementUseCase.destroy()
    }

    init {
        billInitDataObservableDTO
            .filterNotNull()
            .take(1)
            .onEach { billDetail ->
                timeObservableDTO.value = billDetail.bill.time
                noteStrObservableDTO.value = billDetail.bill.note
                reimburseTypeObservableDTO.value = billDetail.bill.reimburseType
                isNotIncludedInIncomeAndExpenditureObservableDTO.value =
                    billDetail.bill.isNotIncludedInIncomeAndExpenditure
                selectedLabelListObservableDTO.value = billDetail.labelList
                // 图片的信息复原
                selectedImageObservableDTO.value = billDetail.imageUrlList
                    .map { url ->
                        ImageDTO(url = url)
                    }
                when (billDetail.bill.type) {
                    TallyBillTypeDTO.Transfer -> {
                        tabIndexInitData.value = BillCreateTabType.Transfer
                        billCreateTransferUseCase.initData(
                            outAccountId = billDetail.bill.accountId,
                            inAccountId = billDetail.bill.transferTargetAccountId,
                        )
                        accountInitData.value = null
                        billCreateReimbursementUseCase.reimbursementBillIdInitData.value = null
                    }

                    TallyBillTypeDTO.Normal, TallyBillTypeDTO.Reimbursement -> {
                        when (billDetail.bill.type) {
                            TallyBillTypeDTO.Reimbursement -> {
                                billCreateReimbursementUseCase.reimbursementBillIdInitData.value =
                                    billDetail.bill.reimburseBillId
                                billCreateReimbursementUseCase.reimbursedBillCostOffsetObservableDTO.value =
                                    -billDetail.bill.cost.tallyCostAdapter()
                                isReimbursementTypeObservableDTO.value = true
                            }

                            else -> {
                                billCreateReimbursementUseCase.reimbursementBillIdInitData.value =
                                    null
                            }
                        }
                        if (billDetail.categoryWithGroup?.group?.type == TallyCategoryGroupTypeDTO.Income) {
                            tabIndexInitData.value = BillCreateTabType.Income
                        } else {
                            tabIndexInitData.value = BillCreateTabType.Spending
                        }
                        billCreateTransferUseCase.initData(
                            inAccountId = billDetail.bill.transferTargetAccountId,
                        )
                        groupSelectObservableDTO.value = billDetail.categoryWithGroup?.group
                        categorySelectObservableDTO.value = billDetail.categoryWithGroup?.category
                        accountInitData.value = billDetail.bill.accountId
                    }
                }
                val costValue = when (billDetail.bill.type) {
                    TallyBillTypeDTO.Normal -> {
                        if (billDetail.categoryWithGroup?.group?.type == TallyCategoryGroupTypeDTO.Income) {
                            billDetail.bill.cost.tallyCostAdapter()
                        } else {
                            -billDetail.bill.cost.tallyCostAdapter()
                        }
                    }

                    TallyBillTypeDTO.Transfer -> -billDetail.bill.cost.tallyCostAdapter()
                    TallyBillTypeDTO.Reimbursement -> billDetail.bill.cost.tallyCostAdapter()
                }
                costUseCase.costAppend(
                    target = costValue.tallyNumberFormat1(),
                    isRemoveDefaultInput = true,
                )
                bookInitData.value = billDetail.bill.bookId
            }
            .launchIn(scope = scope)
        billCreateReimbursementUseCase
            .reimbursementBillInitDataObservableDTO
            .filterNotNull()
            .take(1)
            .onEach { billDetail ->
                tallyAccountService.getByUid(uid = billDetail.bill.accountId)
                    ?.let { targetAccount ->
                        selectedAccountObservableDTO.value = targetAccount
                    }
            }
            .launchIn(scope = scope)

        billCreateReimbursementUseCase
            .reimburseRestCostObservableDTO
            .filterNotNull()
            .take(1)
            .onEach { cost ->
                if (billIdInitData.value.isNullOrEmpty()) {
                    // 默认的金额
                    costUseCase.costAppend(
                        target = cost.toString(),
                        isRemoveDefaultInput = true,
                    )
                }
            }
            .launchIn(scope = scope)
    }

}