package com.zhangyangjing.gamepadtest.gamepadviewer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.zhangyangjing.gamepadtest.GamePad.Companion.BTN_A
import com.zhangyangjing.gamepadtest.GamePad.Companion.BTN_B
import com.zhangyangjing.gamepadtest.GamePad.Companion.BTN_X
import com.zhangyangjing.gamepadtest.GamePad.Companion.BTN_Y
import com.zhangyangjing.gamepadtest.GamePadManager
import java.util.*

/**
 * Created by zhangyangjing on 2018/7/31.
 */
class GamePadViewer : View, GamePadManager.Listener {
    override fun update() {
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private val TAG = GamePadViewer::class.java.simpleName

//    private val widgets = SparseArray<Base>()
    private val widgets = LinkedList<Base>()

    var gamePadManager: GamePadManager? = null
        set(value) {
            field = value
            field?.addListener(this)
        }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {

        initWidgets()
    }

    private fun initWidgets() {
        Log.v(TAG, "initWidgets")
//        widgets.put(WGT_BTN_A, Button(resources, WGT_BTN_A, 0.2f, 0.2f, 0.2f, 0.2f))
//        widgets.put(WGT_BTN_B, Button(resources, WGT_BTN_B, 0.4f, 0.4f, 0.2f, 0.2f))
//        widgets.put(WGT_BTN_X, Button(resources, WGT_BTN_X, 0.6f, 0.6f, 0.2f, 0.2f))
//        widgets.put(WGT_BTN_X, Button(resources, WGT_BTN_Y, 0.8f, 0.8f, 0.2f, 0.2f))
//        widgets.put(WGT_BTN_L1, Button())
//        widgets.put(WGT_BTN_R1, Button())
//        widgets.put(WGT_BTN_SELECT, Button())
//        widgets.put(WGT_BTN_START, Button())

        widgets.add(Button(context, WGT_BTN_A, 0.2f, 0.2f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_B, 0.4f, 0.4f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_X, 0.6f, 0.6f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_Y, 0.8f, 0.8f, 0.2f, 0.2f))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.v(TAG, "onDraw")

        canvas.drawColor(Color.BLUE)

        val a = gamePadManager
        val b = gamePadManager?.mGamePads
        val c = gamePadManager?.mGamePads?.takeIf { it.size > 0 }
        val d = gamePadManager?.mGamePads?.takeIf { it.size > 0 }?.get(0)
        val gamepad = gamePadManager?.mGamePads?.takeIf { it.size > 0 }?.values?.first() ?: return

        widgets.forEach {
            when (it::class) {
                Button::class -> {
                    val btn = it as Button
                    when (btn.code) {
                        WGT_BTN_A -> btn.pressed = gamepad.mBtnStates[BTN_A]
                        WGT_BTN_B -> btn.pressed = gamepad.mBtnStates[BTN_B]
                        WGT_BTN_X -> btn.pressed = gamepad.mBtnStates[BTN_X]
                        WGT_BTN_Y -> btn.pressed = gamepad.mBtnStates[BTN_Y]
                    }
                }
            }

            canvas.save()
            canvas.clipRect(it.rect)
            it.onDraw(canvas)
            canvas.restore()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.v(TAG, "onSizeChanged")
        widgets.forEach { it.updateCanvasSize(w, h) }
    }

    companion object {
        const val WGT_BTN_A = 0
        const val WGT_BTN_B = 1
        const val WGT_BTN_X = 2
        const val WGT_BTN_Y = 3
        const val WGT_BTN_L1 = 4
        const val WGT_BTN_R1 = 5
        const val WGT_BTN_SELECT = 6
        const val WGT_BTN_START = 7
        const val WGT_DPAD = 8
        const val WGT_STICK_LEFT = 9
        const val WGT_STICK_RIGHT = 10
        const val WGT_ANALOG_BTN_L2 = 11
        const val WGT_ANALOG_BTN_R2 = 12
    }
}
