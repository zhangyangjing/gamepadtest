package com.zhangyangjing.gamepadtest.gamepadviewer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.zhangyangjing.gamepadtest.R

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
