package com.xiaojinzi.tally.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.AppInfoAdapterService
import com.xiaojinzi.tally.R

@ServiceAnno(AppInfoAdapterService::class)
class AppInfoAdapterServiceImpl_tally : AppInfoAdapterService {

    override val appIconRsd: Int
        get() = R.drawable.ic_launcher

    override val appIconBackgroundColorRsd: Int
        get() = R.color.ic_launcher_background

}