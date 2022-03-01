package com.xiaojinzi.tally.bill.module.bill_create.domain

import com.xiaojinzi.module.base.support.MutableInitOnceData
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.tally.base.service.datasource.BillQueryConditionDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillTypeDTO
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface BillCreateReimbursementUseCase : BaseUseCase {

    /**
     * 报销账单 ID
     */
    val reimbursementBillIdInitData: MutableInitOnceData<String?>

    /**
     * 初始化的报销的账单对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val reimbursementBillInitDataObservableDTO: SharedStateFlow<TallyBillDetailDTO?>

    /**
     * 已报销金额显示的差值
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val reimbursedBillCostOffsetObservableDTO: MutableSharedStateFlow<Float>

    /**
     * 报销的总额
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val reimburseTotalCostObservableDTO: Flow<Float?>

    /**
     * 已报销的
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val reimbursedCostObservableDTO: Flow<Float?>

    /**
     * 剩余报销的
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val reimburseRestCostObservableDTO: Flow<Float?>

}

class BillCreateReimbursementUseCaseImpl : BaseUseCaseImpl(), BillCreateReimbursementUseCase {

    override val reimbursementBillIdInitData = MutableInitOnceData<String?>()

    override val reimbursementBillInitDataObservableDTO = combine(
        reimbursementBillIdInitData.valueStateFlow,
        tallyService.dataBaseChangedObservable,
    ) { billId, _ ->
        billId?.run {
            tallyBillService.getDetailById(id = billId)
        }
    }
        .sharedStateIn(scope = scope)

    override val reimbursedBillCostOffsetObservableDTO = MutableSharedStateFlow(initValue = 0f)

    override val reimburseTotalCostObservableDTO = reimbursementBillInitDataObservableDTO
        .map { billDTO ->
            billDTO?.let {
                -(it.bill.cost.tallyCostAdapter())
            }
        }

    override val reimbursedCostObservableDTO = combine(
        reimbursementBillInitDataObservableDTO,
        reimbursedBillCostOffsetObservableDTO,
    ) { reimbursementBill, reimbursedBillCostOffset ->
        reimbursementBill?.let {
            tallyBillService.getBillCostByCondition(
                condition = BillQueryConditionDTO(
                    billTypes = listOf(
                        TallyBillTypeDTO.Reimbursement,
                    ),
                    reimburseBillIdList = listOf(
                        reimbursementBill.bill.uid,
                    )
                )
            ).tallyCostAdapter() + reimbursedBillCostOffset
        }
    }

    override val reimburseRestCostObservableDTO = combine(
        reimburseTotalCostObservableDTO, reimbursedCostObservableDTO,
    ) { reimburseTotalCost, reimbursedCost ->
        reimburseTotalCost?.let {
            reimbursedCost?.let {
                reimburseTotalCost - reimbursedCost
            }
        }
    }

}
