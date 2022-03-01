package com.xiaojinzi.tally.bill.module.cate_group_create.view

import com.xiaojinzi.component.impl.RouterInterceptor
import com.xiaojinzi.component.support.ParameterSupport
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.tally.base.support.tallyCategoryService
import kotlinx.coroutines.launch

class CateGroupCreateInterceptor : RouterInterceptor {

    override fun intercept(chain: RouterInterceptor.Chain) {

        val request = chain.request()
        val cateGroupId = ParameterSupport.getString(request.bundle, "cateGroupId", null)
        if (cateGroupId == null) {
            chain.proceed(request)
        } else {
            AppScope.launch {
                val cateGroupDTO = tallyCategoryService.getTallyCategoryGroupById(id = cateGroupId)
                if (cateGroupDTO == null) {
                    chain.proceed(request)
                } else {
                    if (cateGroupDTO.isBuiltIn) {
                        chain.callback()
                            .onError(IllegalStateException("Inner CategoryGroup can't be edit"))
                    } else {
                        chain.proceed(request)
                    }
                }
            }
        }

    }

}