package com.zhangyangjing.gamepadtest.ui.widget.gamepadviewer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect

/**
 * Created by zhangyangjing on 2018/8/10.
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
        val size = Math.min(width, height).toFloat()
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
