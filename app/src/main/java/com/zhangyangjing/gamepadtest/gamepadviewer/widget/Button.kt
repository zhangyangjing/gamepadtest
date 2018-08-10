package com.zhangyangjing.gamepadtest.gamepadviewer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_A
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_B
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_L1
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_R1
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_SELECT
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_START
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_X
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_Y

class Button(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {
    private val TAG = Button::class.java.simpleName

    var pressed: Boolean = false
    lateinit var drawableNormal: Drawable
    lateinit var drawablePressed: Drawable

    override fun onDraw(canvas: Canvas) {
        val drawable = if (pressed) drawablePressed else drawableNormal
        drawable.bounds = rect
        drawable.draw(canvas)
    }

    override fun onLoadDrawable(context: Context) {
        val res = when (code) {
            WGT_BTN_A -> Pair(R.drawable.button_a, R.drawable.button_a_press)
            WGT_BTN_B -> Pair(R.drawable.button_b, R.drawable.button_b_press)
            WGT_BTN_X -> Pair(R.drawable.button_x, R.drawable.button_x_press)
            WGT_BTN_Y -> Pair(R.drawable.button_y, R.drawable.button_y_press)
            WGT_BTN_L1 -> Pair(R.drawable.button_l1, R.drawable.button_l1_press)
            WGT_BTN_R1 -> Pair(R.drawable.button_r1, R.drawable.button_r1_press)
            WGT_BTN_START -> Pair(R.drawable.button_start, R.drawable.button_start_press)
            WGT_BTN_SELECT -> Pair(R.drawable.button_select, R.drawable.button_select_press)
            else -> Pair(R.drawable.button_y, R.drawable.button_y_press)
        }
        drawableNormal = ContextCompat.getDrawable(context, res.first)!!
        drawablePressed = ContextCompat.getDrawable(context, res.second)!!
    }
}
