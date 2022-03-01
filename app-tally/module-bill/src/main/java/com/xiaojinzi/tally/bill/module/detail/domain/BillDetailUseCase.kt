package com.xiaojinzi.tally.bill.module.detail.domain

import android.content.Context
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.component.withHostAndPath
import com.xiaojinzi.module.base.RouterConfig
import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillTypeDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO
import com.xiaojinzi.tally.base.support.tallyBillLabelService
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface BillDetailUseCase : BaseUseCase {

    /**
     * 初始化的账单 ID
     */
    val billDetailIdData: MutableInitOnceData<String>

    /**
     * 账单详情
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val billDetailObservable: Flow<TallyBillDetailDTO?>

    /**
     * 关联的报销单
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val reimburseBillDetailListObservable: Flow<List<TallyBillDetailDTO>?>

    /**
     * 去选择类别
     */
    fun toChooseCategory(context: Context)

    /**
     * 去选择标签
     */
    fun toChooseLabel(context: Context)

    /**
     * 去选择标签
     */
    fun toPickDateTime(context: Context)

    /**
     * 去填写备注
     */
    fun toFillNote(context: Context)

    /**
     * 去选择账单
     */
    fun toChooseBook(context: Context)

    /**
     * 去编辑账单
     */
    fun toBillEdit(context: Context)

    /**
     * 去子报销单账单列表
     */
    fun toSubReimbursementBillList(context: Context)

    /**
     * 删除
     */
    fun deleteBillDetail()

}

class BillDetailUseCaseImpl : BaseUseCaseImpl(), BillDetailUseCase {

    override val billDetailIdData = MutableInitOnceData<String>()

    override val billDetailObservable = combine(
        billDetailIdData.valueStateFlow,
        tallyService.dataBaseChangedObservable
    ) { billId, _ ->
        tallyBillService.getDetailById(id = billId)
    }.sharedStateIn(scope = scope)

    override val reimburseBillDetailListObservable = billDetailObservable
        .map { billDetail ->
            billDetail?.let {
                tallyBillService.getBillListByCondition(
                    condition = BillQueryConditionDTO(
                        billTypes = listOf(TallyBillTypeDTO.Reimbursement),
                        reimburseBillIdList = listOf(
                            billDetail.bill.uid,
                        )
                    )
                )
            }
        }

    override fun toChooseCategory(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val targetBill = billDetailObservable.value ?: return@launch
            val targetCate = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_CATEGORY)
                .putBoolean("isReturnData", true)
                .requestCodeRandom()
                .intentAwait()
                .getParcelableExtra<TallyCategoryDTO>("data") ?: return@launch
            tallyBillService.update(target = targetBill.bill.copy(categoryId = targetCate.uid))
        }
    }

    override fun toChooseLabel(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            val billDetail = billDetailObservable.value ?: return@launch
            val labelIdList = billDetail.labelList.map { it.uid }
            val targetIntent = Router.with(context)
                .hostAndPath(TallyRouterConfig.TALLY_LABEL_LIST)
                .putBoolean("isReturnData", true)
                .putStringArray("selectIdList", labelIdList.toTypedArray())
                .requestCodeRandom()
                .intentAwait()
            val labelIdListResult = targetIntent.getStringArrayExtra("labelIds")!!
            tallyBillLabelService.updateLabelList(
                billId = billDetail.bill.uid,
                labelIdList = labelIdListResult.toList()
            )
        }
    }

    override fun toPickDateTime(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            billDetailObservable.value?.let { targetBillDetail ->
                val targetDateTime = Router.with(context)
                    .hostAndPath(RouterConfig.SYSTEM_DATE_TIME_PICKER)
                    .putLong("dateTime", targetBillDetail.bill.time)
                    .requestCodeRandom()
                    .intentAwait()
                    .getLongExtra("dateTime", System.currentTimeMillis())
                tallyBillService.update(target = targetBillDetail.bill.copy(time = targetDateTime))
            }
        }
    }

    override fun toFillNote(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            billDetailObservable.value?.let { targetBillDetail ->
                val content = Router.with(context)
                    .hostAndPath(TallyRouterConfig.TALLY_NOTE)
                    .putString("data", targetBillDetail.bill.note)
                    .requestCodeRandom()
                    .intentAwait()
                    .getStringExtra("data") ?: ""
                tallyBillService.update(target = targetBillDetail.bill.copy(note = content))
            }
        }
    }

    override fun toChooseBook(context: Context) {
        scope.launch(ErrorIgnoreContext) {
            billDetailObservable.value?.let { targetBillDetail ->
                val selectIdList = context
                    .withHostAndPath(hostAndPath = TallyRouterConfig.TALLY_BILL_BOOK_SELECT)
                    .putBoolean("isSingleSelect", true)
                    .putStringArray("selectIdList", listOf(targetBillDetail.book.uid).toTypedArray())
                    .requestCodeRandom()
                    .intentAwait()
                    .getStringArrayExtra("data")
                    ?.toList() ?: emptyList()
                // 选择好的账单 ID
                val targetResultBookId = selectIdList.first()
                tallyBillService.update(
                    target = targetBillDetail.bill.copy(
                        bookId = targetResultBookId
                    )
                )
            }
        }
    }

    override fun toBillEdit(context: Context) {
        Router.with(context)
            .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
            .putString("billId", billDetailIdData.value)
            .forward()
    }

    override fun toSubReimbursementBillList(context: Context) {
        Router.with(context)
            .hostAndPath(TallyRouterConfig.TALLY_SUB_REIMBURSEMENT_BILL_LIST)
            .putString("billId", billDetailIdData.value)
            .forward()
    }

    override fun deleteBillDetail() {
        billDetailObservable.value?.let { targetBillDetail ->
            scope.launch(ErrorIgnoreContext) {
                tallyBillService.deleteById(id = targetBillDetail.bill.uid)
            }
        }
    }

}