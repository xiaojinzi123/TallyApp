package com.xiaojinzi.tally.bill.module.bill_auto.view

import com.xiaojinzi.component.error.RouterRuntimeException
import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.tally.base.support.autoBillService
import com.xiaojinzi.tally.base.support.tallyAppToast
import com.xiaojinzi.tally.bill.R

class AutoBillCheckInterceptor: RouterInterceptor {

    override fun intercept(chain: RouterInterceptor.Chain) {
        if (autoBillService == null) {
            chain.callback().onError(RouterRuntimeException("not support"))
            tallyAppToast(contentRsd = R.string.res_str_err_tip10)
        } else {
            chain.proceed(chain.request())
        }
    }

}