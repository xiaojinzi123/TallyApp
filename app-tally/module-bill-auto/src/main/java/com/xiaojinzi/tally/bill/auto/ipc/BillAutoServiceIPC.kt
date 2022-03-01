package com.xiaojinzi.tally.bill.auto.ipc

import android.app.Service
import android.content.Intent
import android.os.IBinder

class BillAutoServiceIPC: Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}