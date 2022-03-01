package com.xiaojinzi.tally.my.service

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.MyService
import com.xiaojinzi.tally.my.module.main.view.MyViewWrap

@ServiceAnno(MyService::class)
class MyServiceImpl: MyService {

    @ExperimentalFoundationApi
    @Composable
    override fun myView() {
        MyViewWrap()
    }

}