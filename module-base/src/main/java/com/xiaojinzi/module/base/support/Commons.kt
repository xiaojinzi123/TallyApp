package com.xiaojinzi.module.base.support

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.module.base.service.AppTheme
import com.xiaojinzi.support.ktx.getActivity
import com.xiaojinzi.support.ktx.getFragmentActivity
import com.xiaojinzi.support.ktx.newUUid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.Throws

private val counter = AtomicInteger()

fun generateUniqueStr(): String {
    return newUUid() + counter.incrementAndGet()
}

fun notSupportError(message: String = "Not Support"): Nothing = error(message = message)

inline fun <T> Flow<T>.filterExceptFirst(crossinline predicate: suspend (T) -> Boolean): Flow<T> {
    // 第一次订阅的 UI 数据不容错过, 会忽略用户的条件
    var isNextOnce = false
    return transform { value ->
        if (isNextOnce) {
            if (predicate(value)) return@transform emit(value)
        } else {
            isNextOnce = true
            return@transform emit(value)
        }
    }
}

fun finishAppAllTask() {
    (app.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager).appTasks?.forEach {
        it.finishAndRemoveTask()
    }
}

fun restartApp() {
    val launchIntent: Intent? = app.packageManager.getLaunchIntentForPackage(app.packageName)
    finishAppAllTask()
    launchIntent?.let { targetLaunchIntent ->
        app.startActivity(
            targetLaunchIntent
                .apply {
                    this.addCategory(Intent.CATEGORY_DEFAULT)
                    this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
        )
    }
}

fun Window.translateStatusBar() {
    //设置通知栏透明，导航栏不透明
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = 0x00000000 // transparent
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        this.addFlags(flags)
    }
    if (appThemeService.getSystemTheme().toAppTheme() == AppTheme.Dark) {
        this.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    } else {
        this.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Context.tryFinishActivity() {
    this.getActivity()?.let { act ->
        act.finish()
    }
}

fun AccessibilityNodeInfo.getRootParent(): AccessibilityNodeInfo {
    var temp = this
    while (temp.parent != null) {
        temp = temp.parent
    }
    return temp
}

@Throws(RuntimeException::class)
fun AccessibilityNodeInfo.toTreeString(
    sb: StringBuffer,
    tabCount: Int = 0
) {
    sb.append("\n")
    repeat(times = tabCount) {
        sb.append("------")
    }
    sb.append("className = ${this.className}, text = ${this.text}")
    for (index in 0 until this.childCount) {
        getChild(index).toTreeString(
            sb = sb,
            tabCount = tabCount + 1
        )
    }
}

@MainThread
fun commonAppToast(context: Context = app, @StringRes contentRsd: Int, isShort: Boolean = true) {
    commonAppToast(context = context, content = context.getString(contentRsd), isShort = isShort)
}

@MainThread
fun commonAppToast(context: Context = app, content: String, isShort: Boolean = true) {
    Toast.makeText(
        context,
        content,
        if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}

fun Long.commonTimeFormat1(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
    Date(this)
)
fun Long.commonTimeFormat2(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
    Date(this)
)
fun Long.commonTimeFormat3(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
    Date(this)
)
fun Long.commonTimeFormat4(): String = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(
    Date(this)
)