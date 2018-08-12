package com.zhangyangjing.gamepadtest.widget.gamepadviewer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.widget.gamepadviewer.GamePadViewer.Companion.WGT_ANALOG_BTN_L2
import com.zhangyangjing.gamepadtest.widget.gamepadviewer.GamePadViewer.Companion.WGT_ANALOG_BTN_R2

class AnalogButton(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {
    private lateinit var drawableNormal: Drawable
    private lateinit var drawablePressed: Drawable
    private val mPaint: Paint = Paint()

    var axis: Float = 0f
    var pressed: Boolean = false

    override fun onDraw(canvas: Canvas) {
        if (axis > 0) {
            mPaint.color = Color.LTGRAY
            mPaint.alpha = 180

            val r = Rect(rect)
            r.right = rect.left + (rect.width() * axis).toInt()
            canvas.drawRect(r, mPaint)
        }

        val drawable = if (pressed) drawablePressed else drawableNormal
        drawable.bounds = rect
        drawable.draw(canvas)
    }

    override fun onLoadDrawable(context: Context) {
        val res = when (code) {
            WGT_ANALOG_BTN_L2 -> Pair(R.drawable.button_l2, R.drawable.button_l2_press)
            WGT_ANALOG_BTN_R2 -> Pair(R.drawable.button_r2, R.drawable.button_r2_press)
            else -> Pair(R.drawable.button_a, R.drawable.button_a_press)
        }
        drawableNormal = ContextCompat.getDrawable(context, res.first)!!
        drawablePressed = ContextCompat.getDrawable(context, res.second)!!
    }
}
