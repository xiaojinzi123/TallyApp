package com.xiaojinzi.tally

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.os.Process
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.module.base.support.DevelopHelper
import com.xiaojinzi.module.base.support.appThemeService
import com.xiaojinzi.module.base.support.buglyService
import com.xiaojinzi.module.base.support.imageUploadService
import com.xiaojinzi.support.init.AppInstance
import com.xiaojinzi.support.ktx.AppInitSupport
import com.xiaojinzi.support.ktx.AppInitTask
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.support.TallyTaskPriority
import com.xiaojinzi.tally.base.support.tallyAppService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class App : Application() {

    private val Tag = "App"

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(baseContext)
        AppInstance.app = this
        AppInstance.isDebug = BuildConfig.DEBUG
        DevelopHelper.init(BuildConfig.DEBUG)
    }

    override fun onCreate() {
        super.onCreate()

        LogSupport.d(tag = Tag, content = "ProcessId = ${Process.myPid()}")

        // 初始化组件化
        Component.init(
            BuildConfig.DEBUG,
            Config.Builder(this)
                .initRouterAsync(true)
                .errorCheck(true)
                .optimizeInit(true)
                .autoRegisterModule(true)
                .build()
        )

        AppScope.launch(context = Dispatchers.Main) {
            AppInitSupport
                .addTask(
                    // 常规初始化
                    AppInitTask(priority = TallyTaskPriority.Normal) {
                        if (BuildConfig.bugly) {
                            // 初始化 Bugly
                            buglyService?.init(
                                appKey = BuildConfig.buglyAppkey,
                                isDebugMode = BuildConfig.DEBUG
                            )
                        }
                        // 设置图片上传类型
                        imageUploadService.setImageServer(server = tallyAppService.imageType)
                        // 强制使用亮色主题
                        appThemeService.applyLightAppTheme()
                        // 注册退出全部 App 的监听
                        registerActivityLifecycleCallbacks(FinishTaskActivityLifecycleCallback())
                        // 开始初始化异步任务
                        tallyAppService.startInitTask()
                    }
                )
                .execute()
        }

    }

}