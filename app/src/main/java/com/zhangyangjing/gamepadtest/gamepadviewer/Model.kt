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
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_L1
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_R1
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_SELECT
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_START
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_X
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer.Companion.WGT_BTN_Y
import java.lang.Math.*

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
    lateinit var drawables: List<Drawable>

    override fun onDraw(canvas: Canvas) {
        val d = drawables[direction]
        d.bounds = rect
        d.draw(canvas)
    }

    override fun onLoadDrawable(context: Context) {
        drawables = listOf(
            ContextCompat.getDrawable(context, R.drawable.stick_none)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_up)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_up_right)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_right)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_down_right)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_down)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_down_left)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_left)!!,
            ContextCompat.getDrawable(context, R.drawable.stick_up_left)!!
        )
    }
}

class Stick(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {
    private val TAG = Stick::class.java.simpleName

    lateinit var large: Drawable
    lateinit var smallNormal: Drawable
    lateinit var smallPressed: Drawable

    // for test
//    lateinit var bmp: Bitmap
//    lateinit var cvs: Canvas
//    lateinit var paint: Paint

    var axisX: Float = 0f
    var axisY: Float = 0f
    var pressed: Boolean = false

    override fun onDraw(canvas: Canvas) {
        large.bounds = rect
        large.draw(canvas)

        val scale = rect.width().toFloat() / large.intrinsicWidth
        val shrink = ((large.intrinsicWidth - smallNormal.intrinsicWidth) / 2 * scale).toInt()
        val rectSmall = Rect(rect)
        rectSmall.left += shrink
        rectSmall.right -= shrink
        rectSmall.top += shrink
        rectSmall.bottom -= shrink

        val offsetMax = (rect.width() - rectSmall.width())  / 2
        val offsetX = (offsetMax * axisX).toInt()
        val offsetY = (offsetMax * axisY).toInt()
        rectSmall.offset(offsetX, offsetY)
        val smallDrawable = if (pressed) smallPressed else smallNormal
        Log.v(TAG, "x:$axisX y:$axisY len:${sqrt(pow(axisX.toDouble(), 2.toDouble()) + pow(axisY.toDouble(), 2.toDouble()))}")
        smallDrawable.bounds = rectSmall
        smallDrawable.draw(canvas)

        // for test
//        cvs.drawPoint(bmp.width / 2f * (1 + axisX), bmp.width / 2f * (1 + axisY), paint)
//        canvas.drawBitmap(bmp, Rect(0, 0, bmp.width, bmp.height), rect, paint)
    }

    override fun onLoadDrawable(context: Context) {
        large = ContextCompat.getDrawable(context, R.drawable.stick_large)!!
        smallNormal = ContextCompat.getDrawable(context, R.drawable.stick_small)!!
        smallPressed = ContextCompat.getDrawable(context, R.drawable.stick_small_pressed)!!

        // for test
//        bmp = Bitmap.createBitmap(large.intrinsicWidth, large.intrinsicWidth, Bitmap.Config.ARGB_8888)
//        cvs = Canvas(bmp)
//        paint = Paint()
//        paint.color = Color.RED
//        paint.strokeWidth = 10f
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
        drawable.draw(canvas)
    }

    override fun onLoadDrawable(context: Context) {
        val res = when (code) {
            WGT_BTN_A -> Pair(R.drawable.button_a, R.drawable.button_a_press)
            WGT_BTN_B -> Pair(R.drawable.button_b, R.drawable.button_b_press)
            WGT_BTN_X -> Pair(R.drawable.button_x, R.drawable.button_x_press)
            WGT_BTN_Y -> Pair(R.drawable.button_y, R.drawable.button_y_press)
            WGT_BTN_L1 -> Pair(R.drawable.button_l, R.drawable.button_l_press)
            WGT_BTN_R1 -> Pair(R.drawable.button_r, R.drawable.button_r_press)
            WGT_BTN_START -> Pair(R.drawable.button_start, R.drawable.button_start_press)
            WGT_BTN_SELECT -> Pair(R.drawable.button_select, R.drawable.button_select_press)
            else -> Pair(R.drawable.button_y, R.drawable.button_y_press)
        }
        drawableNormal = ContextCompat.getDrawable(context, res.first)!!
        drawablePressed = ContextCompat.getDrawable(context, res.second)!!
    }
}
