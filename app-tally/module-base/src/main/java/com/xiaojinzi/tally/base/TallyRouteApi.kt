package com.xiaojinzi.tally.base

import com.xiaojinzi.component.anno.router.HostAndPathAnno
import com.xiaojinzi.component.anno.router.RouterApiAnno

@RouterApiAnno
interface TallyRouteApi {

    @HostAndPathAnno(TallyRouterConfig.TALLY_LABEL_LIST)
    suspend fun toLabelList()

}