package com.zhangyangjing.gamepadtest.gamepadviewer.widget

import android.content.Context
import android.graphics.Canvas

class AnalogButton(ctx: Context, code: Int, centerX: Float, centerY: Float, width: Float, height: Float) : Base(ctx, code, centerX, centerY, width, height) {

    var axis: Float = 0f
    var pressed: Boolean = false

    override fun onDraw(canvas: Canvas) {

    }

    override fun onLoadDrawable(context: Context) {
    }
}
