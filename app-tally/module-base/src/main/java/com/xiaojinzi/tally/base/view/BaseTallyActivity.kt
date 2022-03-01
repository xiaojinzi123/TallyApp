package com.xiaojinzi.tally.base.view

import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.xiaojinzi.module.base.R
import com.xiaojinzi.module.base.view.BaseActivity
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.tally.base.service.setting.LanguageSettingDTO
import com.xiaojinzi.tally.base.support.settingService
import java.util.*

open class BaseTallyActivity<VM : BaseViewModel> : BaseActivity<VM>() {

    override fun attachBaseContext(newBase: Context) {
        val currentLanguage = settingService.languageObservableDTO.value
        val configuration = newBase.resources.configuration
        if (currentLanguage == LanguageSettingDTO.FollowSystem) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocales(null)
            } else {
                configuration.locale = null
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocales(LocaleList(currentLanguage.locale))
            } else {
                configuration.locale = currentLanguage.locale
            }
        }
        val newContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newBase.createConfigurationContext(configuration)
        } else {
            newBase.resources.updateConfiguration(
                configuration,
                newBase.resources.displayMetrics
            )
            newBase
        }
        super.attachBaseContext(newContext)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

}