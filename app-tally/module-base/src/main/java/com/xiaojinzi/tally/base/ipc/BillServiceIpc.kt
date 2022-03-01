package com.xiaojinzi.tally.base.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.tally.base.IBill
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.rx2.awaitFirst
import kotlinx.coroutines.withContext

object BillServiceIpc {

    private val event = PublishSubject.create<Unit>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            billServiceBinder = IBill.Stub.asInterface(service)
            event.onNext(Unit)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            billServiceBinder = null
            event.onNext(Unit)
        }
    }

    private var billServiceBinder: IBill? = null

    @Synchronized
    private suspend fun tryConnection() {
        withContext(context = Dispatchers.Main) {
            if (billServiceBinder != null) {
                return@withContext
            }
            val eventDeferred = async(Dispatchers.IO) { event.awaitFirst() }
            app.bindService(
                Intent().apply {
                    this.`package` = app.packageName
                    this.action = "com.xiaojinzi.tally.bill.ipc.BillService"
                },
                serviceConnection,
                Context.BIND_AUTO_CREATE,
            )
            eventDeferred.await()
        }
    }

    suspend fun requiredBillServiceIPC(): IBill {
        tryConnection()
        return billServiceBinder!!
    }

}