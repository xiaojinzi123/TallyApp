package com.xiaojinzi.module.base.service

import androidx.appcompat.app.AppCompatDelegate
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.ignoreCheckResult
import com.xiaojinzi.support.ktx.observeOnMainThread
import com.xiaojinzi.module.base.support.restartApp
import com.xiaojinzi.module.base.support.spService
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject

private fun Int.toAppTheme(): AppTheme {
    return when (this) {
        1 -> AppTheme.Dark
        2 -> AppTheme.Light
        else -> AppTheme.FollowSystem
    }
}

enum class AppTheme(val value: Int) {
    FollowSystem(0), Dark(1), Light(2)
}

enum class SystemTheme {
    Dark, Light;

    fun toAppTheme(): AppTheme {
        return when (this) {
            Dark -> AppTheme.Dark
            Light -> AppTheme.Light
        }
    }
}

interface AppThemeService {

    companion object {
        const val NAME = "appTheme"
        const val KEY_APP_THEME = "appTheme"
        const val KEY_USER_CHANGED = "userChanged"
    }

    /**
     * 标记用户已经选择过了
     */
    fun flagUserChanged()

    /**
     * 是否用户手动改变过
     */
    fun isUserChanged(): Boolean

    /**
     * 应用主题
     */
    fun applyAppTheme()

    /**
     * 应用亮色主题
     */
    fun applyLightAppTheme()

    /**
     * AppTheme 的切换
     */
    fun toggleAppTheme(rebootNow: Boolean = false, isUserSet: Boolean = false)

    /**
     * 获取当前的主题, 获取的值可能和界面的表现不一致, 因为用户可以下次启动再生效
     * @see getCurrentRealAppTheme
     */
    fun getCurrentAppTheme(): AppTheme

    /**
     * 当前是否切换到了 Drak 主题上
     */
    fun isCurrentDark(): Boolean

    /**
     * 获取当前 App 正在使用的主题, 可能和 [getCurrentAppTheme] 返回的值不一致
     * @see getCurrentAppTheme
     */
    fun getCurrentRealAppTheme(): AppTheme

    /**
     * 获取系统主题
     */
    fun getSystemTheme(): SystemTheme

    /**
     * 当前 App 是否使用了 Dark 主题
     */
    fun isCurrentRealDark(): Boolean

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    fun subscribeAppTheme(): Observable<AppTheme>

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    fun subscribeDarkAppTheme(): Observable<Boolean> {
        return subscribeAppTheme()
            .map {
                when (it) {
                    AppTheme.FollowSystem -> getSystemTheme() == SystemTheme.Dark
                    else -> it == AppTheme.Dark
                }
            }
    }

}

@ServiceAnno(AppThemeService::class)
class AppThemeServiceImpl : AppThemeService {

    private val appThemBehaviorSubject = BehaviorSubject.createDefault(
        when (isUserChanged()) {
            true -> {
                (spService.getInt(
                    fileName = AppThemeService.NAME,
                    key = AppThemeService.KEY_APP_THEME,
                ) ?: AppCompatDelegate.MODE_NIGHT_NO).toAppTheme()
            }
            false -> AppTheme.FollowSystem
        }
    )

    init {

        appThemBehaviorSubject
            .observeOnMainThread()
            .subscribeBy { mode ->
                spService.putInt(
                    fileName = AppThemeService.NAME,
                    key = AppThemeService.KEY_APP_THEME,
                    value = mode.value
                )
                // applyAppTheme(mode)
            }
            .ignoreCheckResult()

    }

    override fun flagUserChanged() {
        spService.putBool(
            fileName = AppThemeService.NAME,
            key = AppThemeService.KEY_USER_CHANGED,
            value = true
        )
    }

    override fun isUserChanged(): Boolean {
        return spService.getBool(
            fileName = AppThemeService.NAME,
            key = AppThemeService.KEY_USER_CHANGED,
        ) ?: false
    }

    override fun applyAppTheme() {
        applyAppTheme(appThemBehaviorSubject.value!!)
    }

    override fun applyLightAppTheme() {
        if (isCurrentDark()) {
            toggleAppTheme(rebootNow = false, isUserSet = false)
        }
        applyAppTheme()
    }

    private fun applyAppTheme(mode: AppTheme) {
        when (mode) {
            AppTheme.FollowSystem -> applyAppTheme(mode = getSystemTheme().toAppTheme())
            AppTheme.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppTheme.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * 先保存值, 然后重启. 然后生效切换
     */
    override fun toggleAppTheme(rebootNow: Boolean, isUserSet: Boolean) {
        if (isCurrentDark()) {
            appThemBehaviorSubject.onNext(AppTheme.Light)
        } else {
            appThemBehaviorSubject.onNext(AppTheme.Dark)
        }
        if (isUserSet) {
            flagUserChanged()
        }
        if (rebootNow) {
            restartApp()
            applyAppTheme()
        }
    }

    override fun getCurrentAppTheme(): AppTheme {
        return appThemBehaviorSubject.value!!
    }

    override fun isCurrentDark(): Boolean {
        return when (getCurrentAppTheme()) {
            AppTheme.FollowSystem -> getSystemTheme() == SystemTheme.Dark
            AppTheme.Dark -> true
            AppTheme.Light -> false
        }
    }

    override fun getCurrentRealAppTheme(): AppTheme {
        val result = AppCompatDelegate.getDefaultNightMode()
        return if (result in listOf(
                AppCompatDelegate.MODE_NIGHT_YES,
                AppCompatDelegate.MODE_NIGHT_NO
            )
        ) {
            return when (result) {
                AppCompatDelegate.MODE_NIGHT_YES -> AppTheme.Dark
                AppCompatDelegate.MODE_NIGHT_NO -> AppTheme.Light
                else -> error("not support")
            }
        } else {
            AppTheme.Light
        }
    }

    override fun getSystemTheme(): SystemTheme {
        return when (app.resources.configuration.uiMode == 0x21) {
            true -> SystemTheme.Dark
            false -> SystemTheme.Light
        }
    }

    override fun isCurrentRealDark(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    override fun subscribeAppTheme(): Observable<AppTheme> {
        return appThemBehaviorSubject
    }

}