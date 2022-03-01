package com.xiaojinzi.tally.interceptor

import com.xiaojinzi.component.anno.GlobalInterceptorAnno
import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.module.base.R

@GlobalInterceptorAnno
class ActivityStartAnimInterceptor: RouterInterceptor {

    @Throws(Exception::class)
    override
    fun intercept(chain: RouterInterceptor.Chain) {
        val originRequest = chain.request()
        val rawActivity = originRequest.rawActivity
        val originAfterAction = originRequest.afterStartAction
        val request = originRequest
            .toBuilder()
            .afterStartAction {
                originAfterAction?.invoke()
                rawActivity?.overridePendingTransition(R.anim.right_in, R.anim.left_out)
            }
            .build()
        chain.proceed(request)
    }

}