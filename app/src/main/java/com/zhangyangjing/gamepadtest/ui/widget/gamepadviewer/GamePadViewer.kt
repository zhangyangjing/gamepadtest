package com.zhangyangjing.gamepadtest.ui.widget.gamepadviewer

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_BRAKE
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_LTRIGGER
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_RTRIGGER
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_RZ
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_THROTTLE
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_X
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_Y
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.AXIS_Z
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_A
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_B
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_DOWN
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_L1
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_L2
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_LEFT
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_R1
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_R2
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_RIGHT
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_SELECT
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_START
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_THUMBL
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_THUMBR
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_UP
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_X
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.BTN_Y
import com.zhangyangjing.gamepadtest.ui.widget.gamepadviewer.widget.AnalogButton
import com.zhangyangjing.gamepadtest.ui.widget.gamepadviewer.widget.Base
import java.util.*
import kotlin.math.max

/**
 * Created by zhangyangjing on 2018/7/31.
 */
class GamePadViewer : View, GamePad.IListener {
    private val widgets = LinkedList<Base>()

    var gamePad: GamePad? = null
        set(value) {
            field = value
            field?.addListener(this)
            postInvalidate()
        }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initWidgets()
    }

    override fun onGamePadStateUpdate(gamePad: GamePad, type: Int, code: Int) {
        ViewCompat.postInvalidateOnAnimation(this)

    }

    override fun onGamePadClick(gamePad: GamePad, code: Int) {
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widgets.forEach { it.updateCanvasSize(w, h) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        when (context.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                val height = (w * 0.6).toInt()
                setMeasuredDimension(w, height)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                val width = (h * 1.6).toInt()
                setMeasuredDimension(width, h)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        widgets.forEach {
            updateWidgetState(it, gamePad ?: return)
            canvas.save()
            canvas.clipRect(it.rect)
            it.onDraw(canvas)
            canvas.restore()
        }
    }

    private fun initWidgets() {
        widgets.add(Button(context, WGT_BTN_A, 0.7f, 0.6f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_B, 0.85f, 0.5f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_X, 0.55f, 0.5f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_Y, 0.7f, 0.4f, 0.2f, 0.2f))
        widgets.add(Button(context, WGT_BTN_SELECT, 0.38f, 0.06f, 0.25f, 0.1f))
        widgets.add(Button(context, WGT_BTN_START, 0.62f, 0.06f, 0.25f, 0.1f))
        widgets.add(DPad(context, WGT_DPAD, 0.16f, 0.7f, 0.3f, 0.3f))
        widgets.add(Stick(context, WGT_STICK_LEFT, 0.35f, 0.83f, 0.3f, 0.3f))
        widgets.add(Stick(context, WGT_STICK_RIGHT, 0.55f, 0.83f, 0.3f, 0.3f))

        widgets.add(Button(context, WGT_BTN_L1, 0.16f, 0.2f, 0.3f, 0.1f))
        widgets.add(AnalogButton(context, WGT_ANALOG_BTN_L2, 0.16f, 0.08f, 0.3f, 0.1f))
        widgets.add(Button(context, WGT_BTN_R1, 0.84f, 0.2f, 0.3f, 0.1f))
        widgets.add(AnalogButton(context, WGT_ANALOG_BTN_R2, 0.84f, 0.08f, 0.3f, 0.1f))
    }

    private fun updateWidgetState(widget: Base, gamePad: GamePad) {
        when (widget) {
            is Button -> { updateBtnState(gamePad, widget) }
            is AnalogButton -> { updateAnalogBtnState(gamePad, widget) }
            is Stick -> { updateStickState(gamePad, widget) }
            is DPad -> { updateDpadState(gamePad, widget) }
        }
    }

    private fun updateBtnState(gamePad: GamePad, btn: Button) {
        when (btn.code) {
            WGT_BTN_A -> btn.pressed = gamePad.btnStates[BTN_A]
            WGT_BTN_B -> btn.pressed = gamePad.btnStates[BTN_B]
            WGT_BTN_X -> btn.pressed = gamePad.btnStates[BTN_X]
            WGT_BTN_Y -> btn.pressed = gamePad.btnStates[BTN_Y]
            WGT_BTN_L1 -> btn.pressed = gamePad.btnStates[BTN_L1]
            WGT_BTN_R1 -> btn.pressed = gamePad.btnStates[BTN_R1]
            WGT_BTN_START -> btn.pressed = gamePad.btnStates[BTN_START]
            WGT_BTN_SELECT -> btn.pressed = gamePad.btnStates[BTN_SELECT]
        }
    }

    private fun updateAnalogBtnState(gamePad: GamePad, btn: AnalogButton) {
        when (btn.code) {
            WGT_ANALOG_BTN_L2 -> {
                btn.pressed = gamePad.btnStates[BTN_L2]
                btn.axis = max(gamePad.axisStates[AXIS_LTRIGGER], gamePad.axisStates[AXIS_BRAKE])
            }
            WGT_ANALOG_BTN_R2 -> {
                btn.pressed = gamePad.btnStates[BTN_R2]
                btn.axis = max(gamePad.axisStates[AXIS_RTRIGGER], gamePad.axisStates[AXIS_THROTTLE])
            }
        }
    }

    private fun updateStickState(gamePad: GamePad, stick: Stick) {
        when (stick.code) {
            WGT_STICK_LEFT -> {
                stick.axisX = gamePad.axisStates[AXIS_X]
                stick.axisY = gamePad.axisStates[AXIS_Y]
                stick.pressed = gamePad.btnStates[BTN_THUMBL]
            }
            WGT_STICK_RIGHT -> {
                stick.axisX = gamePad.axisStates[AXIS_Z]
                stick.axisY = gamePad.axisStates[AXIS_RZ]
                stick.pressed = gamePad.btnStates[BTN_THUMBR]
            }
        }
    }

    private fun updateDpadState(gamePad: GamePad, dpad: DPad) {
        var state = 0
        if (gamePad.btnStates[BTN_UP]) state = state or 0b0100
        if (gamePad.btnStates[BTN_DOWN]) state = state or 0b0001
        if (gamePad.btnStates[BTN_LEFT]) state = state or 0b1000
        if (gamePad.btnStates[BTN_RIGHT]) state = state or 0b0010
        dpad.direction = sArrayMap[state]
    }

    companion object {
        private val TAG = GamePadViewer::class.java.simpleName

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

        private const val STICK_STATE_NORMAL      = 0
        private const val STICK_STATE_UP          = 1
        private const val STICK_STATE_UP_RIGHT    = 2
        private const val STICK_STATE_RIGHT       = 3
        private const val STICK_STATE_DOWN_RIGHT  = 4
        private const val STICK_STATE_DOWN        = 5
        private const val STICK_STATE_DOWN_LEFT   = 6
        private const val STICK_STATE_LEFT        = 7
        private const val STICK_STATE_UP_LEFT     = 8

        private val sArrayMap = intArrayOf(
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
    }
}
