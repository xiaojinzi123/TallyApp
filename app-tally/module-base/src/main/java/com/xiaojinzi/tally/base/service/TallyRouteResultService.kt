package com.xiaojinzi.tally.base.service

import android.content.Context
import androidx.annotation.UiContext
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.intentAwait
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryDTO

interface TallyRouteResultService {

    /**
     * 选择一个类别
     */
    suspend fun selectCategory(@UiContext context: Context): String

}

@ServiceAnno(TallyRouteResultService::class)
class TallyRouteResultServiceImpl: TallyRouteResultService {

    override suspend fun selectCategory(context: Context): String {
        return Router.with(context)
            .hostAndPath(TallyRouterConfig.TALLY_CATEGORY)
            .putBoolean("isReturnData", true)
            .requestCodeRandom()
            .intentAwait()
            .getParcelableExtra<TallyCategoryDTO>("data")!!
            .uid
    }

}