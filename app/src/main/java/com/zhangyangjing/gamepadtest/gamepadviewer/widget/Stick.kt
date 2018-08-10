package com.zhangyangjing.gamepadtest.gamepadviewer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.Log
import com.zhangyangjing.gamepadtest.R

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
        Log.v(TAG, "x:$axisX y:$axisY len:${Math.sqrt(Math.pow(axisX.toDouble(), 2.toDouble()) + Math.pow(axisY.toDouble(), 2.toDouble()))}")
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
