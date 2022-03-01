package com.xiaojinzi.module.base

import android.provider.Settings

// ------------------------------------ 公共的 ------------------------------------

object RouterConfig {

    const val PLACEHOLDER = "TODO/TODO"

    private const val COMMON_HOST_DEVELOP = "develop"
    const val COMMON_DEVELOP_MAIN = "$COMMON_HOST_DEVELOP/main"

    private const val HOST_SUPPORT = "support"
    const val SUPPORT_ICON_SELECT = "$HOST_SUPPORT/iconSelect"
    const val SUPPORT_IMAGE_PREVIEW = "$HOST_SUPPORT/imagePreview"

    private const val HOST_SYSTEM = "system"
    const val SYSTEM_APP_MARKET = "$HOST_SYSTEM/appMarket"
    const val SYSTEM_YEAR_SELECT = "$HOST_SYSTEM/yearSelect"
    const val SYSTEM_DATE_TIME_PICKER = "$HOST_SYSTEM/dateTimePicker"
    const val SYSTEM_ACTION_ACCESSIBILITY_SETTINGS = "$HOST_SYSTEM/${Settings.ACTION_ACCESSIBILITY_SETTINGS}"
    const val SYSTEM_ACTION_MANAGE_OVERLAY_PERMISSION = "$HOST_SYSTEM/${Settings.ACTION_MANAGE_OVERLAY_PERMISSION}"
    const val SYSTEM_IMAGE_PICKER = "$HOST_SYSTEM/imagePicker"
    const val SYSTEM_FILL_IN_NUMBER = "$HOST_SYSTEM/fillInNumber"
    const val SYSTEM_MENU_SELECT = "$HOST_SYSTEM/menuSelect"

}
