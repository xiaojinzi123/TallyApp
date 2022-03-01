package com.xiaojinzi.tally.bill.auto.view

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.xiaojinzi.support.ktx.viewBindings
import com.xiaojinzi.tally.bill.auto.databinding.BillAutoToolLayoutBinding

/**
 * 点击了即发起记账的按钮的视图
 */
class AutoBillButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewBinding: BillAutoToolLayoutBinding by viewBindings()

    var moveListener: (x: Float, y: Float) -> Unit = { x, y ->
        println("x = $x, y= $y")
    }

    init {
        this.addView(viewBinding.root)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return super.generateLayoutParams(lp).apply {
            this.width = LayoutParams.WRAP_CONTENT
            this.height = LayoutParams.WRAP_CONTENT
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return super.generateDefaultLayoutParams().apply {
            this.width = LayoutParams.WRAP_CONTENT
            this.height = LayoutParams.WRAP_CONTENT
        }
    }

    val tempPointF = PointF(0f, 0f)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                tempPointF.x = event.x
                tempPointF.y = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                moveListener.invoke(
                    event.x - tempPointF.x,
                    event.y - tempPointF.y,
                )
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // empty
            }
        }
        return super.onTouchEvent(event)
    }

}