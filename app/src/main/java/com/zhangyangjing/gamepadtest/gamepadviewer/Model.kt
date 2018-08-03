package com.zhangyangjing.gamepadtest.gamepadviewer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.Log
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_A
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_B
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_X
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_Y
import java.lang.Math.min

/**
 * Created by zhangyangjing on 2018/7/31.
 */
abstract class Base() {

    var code: Int = 0
    var metaWidth: Float = 0f
    var metaHeight: Float = 0f
    var metaCenterX: Float = 0f
    var metaCenterY: Float = 0f
    var rect: Rect = Rect()

    constructor(ctx: Context, code: Int, metaCenterX: Float, metaCenterY: Float, metaWidth: Float, metaHeight: Float) : this() {
        this.code = code
        this.metaWidth = metaWidth
        this.metaHeight = metaHeight
        this.metaCenterX = metaCenterX
        this.metaCenterY = metaCenterY

        onLoadDrawable(ctx)
    }

    fun updateCanvasSize(width: Int, height: Int) {
        val size = min(width, height).toFloat()
        val w = metaWidth * size
        val h = metaHeight * size
        val cx = metaCenterX * width
        val cy = metaCenterY * height
        rect.left = (cx - w / 2).toInt()
        rect.right = (cx + w / 2).toInt()
        rect.top = (cy - h / 2).toInt()
        rect.bottom = (cy + h / 2).toInt()
    }

    abstract fun onLoadDrawable(context: Context)
    abstract fun onDraw(canvas: Canvas)
}

class DPad(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {

    var direction = 0

    override fun onDraw(canvas: Canvas) {
    }

    override fun onLoadDrawable(context: Context) {
    }
}

class Stick(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {

    var axisX: Float = 0f
    var axisY: Float = 0f
    var pressed: Boolean = false

    override fun onDraw(canvas: Canvas) {
    }

    override fun onLoadDrawable(context: Context) {
    }
}

class AnalogButton(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {

    var axis: Float = 0f
    var pressed: Boolean = false

    override fun onDraw(canvas: Canvas) {

    }

    override fun onLoadDrawable(context: Context) {
    }
}

class Button(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {
    private val TAG = Button::class.java.simpleName

    var pressed: Boolean = false
    lateinit var drawableNormal: Drawable
    lateinit var drawablePressed: Drawable

    override fun onDraw(canvas: Canvas) {
        val drawable = if (pressed) drawablePressed else drawableNormal
        drawable.bounds = rect
        Log.v(TAG, "bounds: $rect")
        drawable.draw(canvas)
    }

    override fun onLoadDrawable(context: Context) {
        val res = when (code) {
            WGT_BTN_A -> Pair(R.drawable.button_a, R.drawable.button_a_press)
            WGT_BTN_B -> Pair(R.drawable.button_b, R.drawable.button_b_press)
            WGT_BTN_X -> Pair(R.drawable.button_x, R.drawable.button_x_press)
            WGT_BTN_Y -> Pair(R.drawable.button_y, R.drawable.button_y_press)
            else -> Pair(R.drawable.button_y, R.drawable.button_y_press)
        }
        drawableNormal = ContextCompat.getDrawable(context, res.first)!!
        drawablePressed = ContextCompat.getDrawable(context, res.second)!!
    }
}
