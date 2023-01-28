package com.xiaojinzi.tally.bill.auto

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.impl.service.ServiceManager
import com.xiaojinzi.module.base.support.toTreeString
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.screenHeight
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.IBill
import com.xiaojinzi.tally.base.TallyRouterConfig
import com.xiaojinzi.tally.base.service.bill.BillParseResultDTO
import com.xiaojinzi.tally.base.service.bill.BillParseService
import com.xiaojinzi.tally.base.support.TallyLogKeyword
import com.xiaojinzi.tally.bill.auto.view.AutoBillButtonView


private const val Tag = BillParseService.Tag

/**
 * 这个无障碍服务的功能是：
 * 1. 检测当前的窗口是否是支持的界面, 如果是则出现一个记账的按钮
 * 2. 点击记账的按钮, 支持对当前的界面进行解析, 读取记账必要的信息. 然后通过跨进程通信 [IBill] 给主进程. 执行后续的记账流程
 *
 * @see [https://developer.android.google.cn/guide/topics/ui/accessibility/service?hl=zh-cn]
 */
class BillCreateAccessibilityService : AccessibilityService() {

    private var isDelayPost = false
    private val handler = Handler(Looper.getMainLooper())

    private val wm by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val billToolViewLayoutParams = WindowManager.LayoutParams().apply {
        this.type = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        this.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        this.format = PixelFormat.TRANSLUCENT
        // this.gravity = Gravity.START
        // this.width = screenWidth
        // this.height = screenHeight
        this.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        this.width = WindowManager.LayoutParams.WRAP_CONTENT
        this.height = WindowManager.LayoutParams.WRAP_CONTENT
        // this.x = screenWidth / 4
        this.y = screenHeight / 5
        // this.x = 0
        // this.y = 0
    }
    private val billToolView = AutoBillButtonView(app).apply {
        this.setOnClickListener {
            val parseResult: BillParseResultDTO? = parseBillInfoFromRootInActiveWindow()
            LogSupport.d(
                tag = Tag,
                content = "event.parseResult = $parseResult",
                keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
            )
            if (parseResult == null) {
            } else {
                removeTool()
                isDelayPost = true
                Router.with()
                    .hostAndPath(TallyRouterConfig.TALLY_BILL_AUTO_CREATE)
                    .putParcelable("data", parseResult)
                    .addIntentFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                        // Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    )
                    .forward()
            }

        }
    }

    private var isAddTool = false



    init {
        LogSupport.d(
            tag = Tag, content = "初始化了",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        sendLoopMessage()
    }

    private fun parseBillInfoFromRootInActiveWindow(): BillParseResultDTO? {
        return this@BillCreateAccessibilityService.rootInActiveWindow?.let {
            try {
                val sb = StringBuffer()
                this@BillCreateAccessibilityService.rootInActiveWindow?.toTreeString(sb = sb)
                LogSupport.d(
                    tag = Tag, content = "rootInActiveWindow.treeString = $sb",
                    keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
                )
            } catch (e: Exception) {
                // ignore
            }
            ServiceManager
                .requiredGet(BillParseService::class.java)
                .parse(info = it)
        }
    }

    private fun sendLoopMessage() {
        handler.postDelayed(
            {
                isDelayPost = false
                val parsedInfo = parseBillInfoFromRootInActiveWindow()
                if (parsedInfo == null) {
                    removeTool()
                } else {
                    addTool()
                }
                sendLoopMessage()
            },
            if (isDelayPost) 2000 else 400
        )
    }

    @Synchronized
    private fun addTool() {
        if (isAddTool) {
            return
        }
        val canAddView = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(app)
        } else {
            true
        }
        if (!canAddView) {
            return
        }
        wm.addView(billToolView, billToolViewLayoutParams)
        isAddTool = true
    }

    @Synchronized
    private fun removeTool() {
        if (!isAddTool) {
            return
        }
        wm.removeView(billToolView)
        isAddTool = false
    }

    override fun onCreate() {
        super.onCreate()
        LogSupport.d(
            tag = Tag, content = "onCreate",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
    }

    override fun onDestroy() {
        LogSupport.d(
            tag = Tag, content = "onDestroy",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        handler.removeCallbacksAndMessages(null)
        removeTool()
        super.onDestroy()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        /*startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        );*/
        LogSupport.d(
            tag = Tag, content = "BillCreateAccessibilityService.onServiceConnected",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        /*this.serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
            canRetrieveWindowContent
            feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
            notificationTimeout = 100
        }*/
    } // "com.alipay.mobile.nebula:id/h5_tv_title"

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        LogSupport.d(
            tag = Tag,
            content = "-----------------------------------------------------------------------",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag, content = "rootInActiveWindow = ${this.rootInActiveWindow}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag,
            content = "rootInActiveWindow.packageName = ${this.rootInActiveWindow?.packageName}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag,
            content = "rootInActiveWindow.className = ${this.rootInActiveWindow?.className}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag, content = "event.eventType = ${event.eventType}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag, content = "event.windowId = ${event.windowId}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag, content = "event.packageName = ${event.packageName}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag, content = "event.className = ${event.className}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        /*when (event.eventType) {
            // AccessibilityEvent.TYPE_WINDOWS_CHANGED,
            // AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val isMatchWindow = billParseService.isMatchWindow(windowStateEvent = event).apply {
                    LogSupport.d(
                        tag = Tag, content = "event.isMatchWindow = $this",
                        keywords = arrayOf(LogKeyword.AUTO_BILL)
                    )
                }
                val isMatch = if (isMatchWindow) {
                    val isMatchContent = this.rootInActiveWindow?.getRootParent()?.let {
                        val sb = StringBuffer()
                        it.toTreeString(sb = sb)
                        LogSupport.d(
                            tag = Tag, content = "rootInActiveWindow.treeString = $sb",
                            keywords = arrayOf(LogKeyword.AUTO_BILL)
                        )
                        billParseService.isMatch(info = it)
                    } ?: false
                    isMatchContent
                } else {
                    false
                }
                if (isMatch) {
                    addTool()
                } else {
                    // 是自己的窗口
                    if (app.packageName == event.packageName) {
                    } else {
                        removeTool()
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                // empty
            }
        }*/
        LogSupport.d(
            tag = Tag, content = "event.contentDescription = ${event.contentDescription}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag, content = "event.source = ${event.source}",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
        LogSupport.d(
            tag = Tag,
            content = "-----------------------------------------------------------------------",
            keywords = arrayOf(TallyLogKeyword.AUTO_BILL)
        )
    }

    override fun onInterrupt() {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

}