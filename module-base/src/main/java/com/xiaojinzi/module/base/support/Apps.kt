package com.xiaojinzi.module.base.support

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.xiaojinzi.support.ktx.app

/**
 * 打点, develop 状态稍作延迟用
 */
suspend inline fun dotDelay() {
    developService?.dotDelay()
}

object Apps {

    /**
     * 去应用商店
     */
    @Throws(Exception::class)
    fun toMarket(packageName: String? = null) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (!packageName.isNullOrEmpty()) {
            intent.setPackage(packageName)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("market://details?id=" + app.packageName)
        app.startActivity(intent)
    }

    /**
     * 去应用商店, 忽略异常
     */
    fun toMarketIgnoreException() {
        try {
            toMarket()
        } catch (e: Exception) {
            Log.d("App", e.message ?: "")
            // ignore
        }
    }

}

