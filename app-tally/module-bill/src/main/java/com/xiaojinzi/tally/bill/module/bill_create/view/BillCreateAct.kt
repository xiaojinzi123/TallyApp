package com.xiaojinzi.tally.bill.module.bill_create.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.module.base.view.compose.StateBar
import com.xiaojinzi.support.ktx.initOnceUseViewModel
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyBillUsageDTO
import com.xiaojinzi.tally.base.support.route_interceptor.WaitAppInitCompleteRouterInterceptor
import com.xiaojinzi.tally.base.theme.TallyTheme
import com.xiaojinzi.tally.base.view.BaseTallyActivity
import kotlinx.coroutines.InternalCoroutinesApi

@RouterAnno(
    hostAndPath = TallyRouterConfig.TALLY_BILL_CREATE,
    interceptors = [WaitAppInitCompleteRouterInterceptor::class]
)
class BillCreateAct : BaseTallyActivity<BillCreateViewModel>() {

    @AttrValueAutowiredAnno("usage")
    var usage: TallyBillUsageDTO = TallyBillUsageDTO.Nothing

    @AttrValueAutowiredAnno("time")
    var time: Long? = null

    @AttrValueAutowiredAnno("cost")
    var cost: Float? = null

    @AttrValueAutowiredAnno("isTransfer")
    var isTransfer: Boolean = false

    @AttrValueAutowiredAnno("billId")
    var billId: String? = null

    @AttrValueAutowiredAnno("reimbursementBillId")
    var reimbursementBillId: String? = null

    @AttrValueAutowiredAnno("categoryId")
    var categoryId: String? = null

    @AttrValueAutowiredAnno("accountId")
    var accountId: String? = null

    @AttrValueAutowiredAnno("outAccountId")
    var outAccountId: String? = null

    @AttrValueAutowiredAnno("inAccountId")
    var inAccountId: String? = null

    @AttrValueAutowiredAnno("bookId")
    var bookId: String? = null

    @AttrValueAutowiredAnno("note")
    var note: String? = null

    override fun getViewModelClass(): Class<BillCreateViewModel> {
        return BillCreateViewModel::class.java
    }

    @InternalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val vm = requiredViewModel()
        initOnceUseViewModel {
            vm.initData(
                usage = usage,
                tabIndex = if (isTransfer) {
                    BillCreateTabType.Transfer
                } else {
                    BillCreateTabType.Spending
                },
                billId = billId,
                time = time,
                reimbursementBillId = reimbursementBillId,
                categoryId = categoryId,
                cost = cost,
                accountId = accountId,
                outAccountId = outAccountId,
                inAccountId = inAccountId,
                bookId = bookId,
                note = note,
            )
        }
        setContent {
            TallyTheme {
                StateBar {
                    BillCreateViewWrap()
                }
            }
        }

    }

}