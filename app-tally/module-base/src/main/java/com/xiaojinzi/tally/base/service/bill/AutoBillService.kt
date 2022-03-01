package com.xiaojinzi.tally.base.service.bill

interface AutoBillService {

    /**
     * 是否能显示在其他 App 的上层
     */
    val canDrawOverlays: Boolean

    /**
     * 是否开启了自动记账功能
     */
    val isOpenAutoBill: Boolean

}