package com.xiaojinzi.tally.bill.ipc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import androidx.annotation.WorkerThread
import com.xiaojinzi.component.ComponentActivityStack
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.lib.res.dto.AutoBillSourceAppType
import com.xiaojinzi.lib.res.dto.AutoBillSourceViewType
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.IBill
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyAccountDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO
import com.xiaojinzi.tally.base.support.TallyLogKeyword
import com.xiaojinzi.tally.base.support.tallyBillAutoSourceAppService
import com.xiaojinzi.tally.base.support.tallyBillAutoSourceViewService
import kotlinx.coroutines.runBlocking

/**
 * 用于处理其他进程的消息
 */
class BillService : Service() {

    private val binder = object : IBill.Stub() {

        override fun addBill(
            appSourceType: Int,
            cost: Float,
            time: Long,
            note: String?,
            orderNumber: String?
        ) {
            LogSupport.d(
                content = "接受到了 addBill 方法的调用了",
                keywords = arrayOf(TallyLogKeyword.AUTO_BILL, TallyLogKeyword.AUTO_BILL_IPC_SERVICE)
            )
            if (ComponentActivityStack.isEmpty) {
                Router.with()
                    .hostAndPath(TallyRouterConfig.TALLY_HOME_MAIN)
                    .forward()
            }
            Router.with()
                .hostAndPath(TallyRouterConfig.TALLY_BILL_CREATE)
                .query("time", time)
                .query("cost", cost)
                .apply {
                    if (!note.isNullOrEmpty()) {
                        this.query("note", note)
                    }
                }
                .forward()
        }

        @WorkerThread
        override fun getDefaultAccount(sourceViewType: Int): TallyAccountDTO? {
            return runBlocking {
                tallyBillAutoSourceAppService
                    .getDetailByType(
                        sourceType = AutoBillSourceAppType.fromValue(value = sourceViewType)
                    )
                    ?.account
            }
        }

        @WorkerThread
        override fun getDefaultCategory(sourceViewType: Int): TallyCategoryDTO? {
            return runBlocking {
                tallyBillAutoSourceViewService
                    .getDetailByType(
                        type = AutoBillSourceViewType.fromValue(value = sourceViewType)
                    )
                    ?.category
            }
        }

    }

    override fun onBind(intent: Intent?): IBinder {
        LogSupport.d(
            tag = "BillService", content = "ProcessId = ${Process.myPid()}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL, TallyLogKeyword.AUTO_BILL_IPC_SERVICE)
        )
        return binder
    }

}