package com.zhangyangjing.gamepadtest.gamepadviewer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_A
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_B
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_DOWN
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_L1
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_LEFT
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_R1
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_RIGHT
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_SELECT
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_START
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_UP
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_X
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_Y
import java.util.*

/**
 * Created by zhangyangjing on 2018/7/31.
 */
class GamePadViewer : View, GamePad.Listener {
    override fun update() {
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private val TAG = GamePadViewer::class.java.simpleName

//    private val widgets = SparseArray<Base>()
    private val widgets = LinkedList<Base>()
//    var gamePadManager: GamePadManager
//    var deviceId: Int

    var gamePad: GamePad? = null
        set(value) {
            field = value
            field?.addListener(this)
            postInvalidate()
        }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initWidgets()
    }

//    constructor(context: Context, _gamePadManager: GamePadManager, _deviceId: Int): super(context) {
//        initWidgets()
//        deviceId = _deviceId
//        gamePadManager = _gamePadManager
//        gamePadManager.addListener(this)
//    }

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

        widgets.add(Button(context, WGT_BTN_A, 0.7f, 0.8f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_B, 0.85f, 0.7f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_X, 0.55f, 0.7f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_Y, 0.7f, 0.6f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_L1, 0.2f, 0.1f, 0.3f, 0.1f))
        widgets.add(Button(context, WGT_BTN_R1, 0.8f, 0.1f, 0.3f, 0.1f))
        widgets.add(Button(context, WGT_BTN_START, 0.65f, 0.9f, 0.25f, 0.1f))
        widgets.add(Button(context, WGT_BTN_SELECT, 0.35f, 0.9f, 0.25f, 0.1f))
        widgets.add(DPad(context, WGT_DPAD, 0.2f, 0.7f, 0.3f, 0.3f))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (width * 0.5).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.v(TAG, "onDraw")

        canvas.drawColor(Color.BLUE)

//        val a = gamePadManager
//        val b = gamePadManager?.mGamePads
//        val c = gamePadManager?.mGamePads?.takeIf { it.size > 0 }
//        val d = gamePadManager?.mGamePads?.takeIf { it.size > 0 }?.get(0)
//        val gamepad = gamePadManager?.mGamePads?.takeIf { it.size > 0 }?.values?.first() ?: return
//        val gamepad = gamePadManager?.mGamePads?.get(deviceId) ?: return

        widgets.forEach {
            updateWidgetState(it, gamePad ?: return)
            canvas.save()
            canvas.clipRect(it.rect)
            it.onDraw(canvas)
            canvas.restore()
        }
    }

    private fun updateWidgetState(widget: Base, gamepad: GamePad) {
        when (widget::class) {
            Button::class -> {
                val btn = widget as Button
                when (btn.code) {
                    WGT_BTN_A -> btn.pressed = gamepad.mBtnStates[BTN_A]
                    WGT_BTN_B -> btn.pressed = gamepad.mBtnStates[BTN_B]
                    WGT_BTN_X -> btn.pressed = gamepad.mBtnStates[BTN_X]
                    WGT_BTN_Y -> btn.pressed = gamepad.mBtnStates[BTN_Y]
                    WGT_BTN_L1 -> btn.pressed = gamepad.mBtnStates[BTN_L1]
                    WGT_BTN_R1 -> btn.pressed = gamepad.mBtnStates[BTN_R1]
                    WGT_BTN_START -> btn.pressed = gamepad.mBtnStates[BTN_START]
                    WGT_BTN_SELECT -> btn.pressed = gamepad.mBtnStates[BTN_SELECT]
                }
            }
            AnalogButton::class -> {
                val analogButton = widget as AnalogButton
            }
            Stick::class -> {
                val stick = widget as Stick
            }
            DPad::class -> {
                val dpad = widget as DPad

                val STICK_STATE_NORMAL      = 0
                val STICK_STATE_UP          = 1
                val STICK_STATE_UP_RIGHT    = 2
                val STICK_STATE_RIGHT       = 3
                val STICK_STATE_DOWN_RIGHT  = 4
                val STICK_STATE_DOWN        = 5
                val STICK_STATE_DOWN_LEFT   = 6
                val STICK_STATE_LEFT        = 7
                val STICK_STATE_UP_LEFT     = 8

                val sArrayMap = intArrayOf(
                        STICK_STATE_NORMAL, // 0000
                        STICK_STATE_DOWN, // 0001
                        STICK_STATE_RIGHT, // 0010
                        STICK_STATE_DOWN_RIGHT, // 0011
                        STICK_STATE_UP, // 0100
                        STICK_STATE_UP, // 0101
                        STICK_STATE_UP_RIGHT, // 0110
                        STICK_STATE_UP_RIGHT, // 0111
                        STICK_STATE_LEFT, // 1000
                        STICK_STATE_DOWN_LEFT, // 1001
                        STICK_STATE_LEFT, // 1010
                        STICK_STATE_LEFT, // 1011
                        STICK_STATE_UP_LEFT, // 1100
                        STICK_STATE_UP_LEFT, // 1101
                        STICK_STATE_UP_LEFT, // 1110
                        STICK_STATE_UP_LEFT)// 1111

                var state = 0
                if (gamepad.mBtnStates[BTN_UP]) state = state or 0b0100
                if (gamepad.mBtnStates[BTN_DOWN]) state = state or 0b0001
                if (gamepad.mBtnStates[BTN_LEFT]) state = state or 0b1000
                if (gamepad.mBtnStates[BTN_RIGHT]) state = state or 0b0010

                dpad.direction = sArrayMap[state]
            }
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
