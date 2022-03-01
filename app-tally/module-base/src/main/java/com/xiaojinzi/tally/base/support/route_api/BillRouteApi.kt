package com.xiaojinzi.tally.base.support.route_api

import android.content.Context
import androidx.annotation.UiContext
import com.xiaojinzi.component.anno.ParameterAnno
import com.xiaojinzi.component.anno.router.HostAndPathAnno
import com.xiaojinzi.component.anno.router.RouterApiAnno
import com.xiaojinzi.component.impl.Call
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.tally.base.TallyRouterConfig

val billRouterApi: BillRouteApi = Router.withApi(BillRouteApi::class.java)

@RouterApiAnno()
interface BillRouteApi {

    /**
     * 去账单创建的界面
     */
    @HostAndPathAnno(TallyRouterConfig.TALLY_BILL_CREATE)
    fun toBillCreateForCall(
        @UiContext context: Context,
        @ParameterAnno("time") time: Long? = null,
        @ParameterAnno("cost") cost: Float? = null,
        @ParameterAnno("isTransfer") isTransfer: Boolean = false,
        @ParameterAnno("billId") billId: String? = null,
        @ParameterAnno("reimbursementBillId") reimbursementBillId: String? = null,
        @ParameterAnno("categoryId") categoryId: String? = null,
        @ParameterAnno("accountId") accountId: String? = null,
        @ParameterAnno("outAccountId") outAccountId: String? = null,
        @ParameterAnno("inAccountId") inAccountId: String? = null,
        @ParameterAnno("bookId") bookId: String? = null,
        @ParameterAnno("note") note: String? = null,
    ): Call

}