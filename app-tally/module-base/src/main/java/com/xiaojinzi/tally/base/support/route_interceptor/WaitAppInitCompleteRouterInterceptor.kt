package com.xiaojinzi.tally.base.support.route_interceptor

import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.tally.base.support.tallyAppService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WaitAppInitCompleteRouterInterceptor: RouterInterceptor {
    override fun intercept(chain: RouterInterceptor.Chain) {
        AppScope.launch {
            // 等待完成
            tallyAppService
                .appTaskInitBehaviorObservableDTO
                .first()
            chain.proceed(chain.request())
        }
    }

}