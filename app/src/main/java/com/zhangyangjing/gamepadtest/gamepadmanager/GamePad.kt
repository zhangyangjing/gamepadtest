package com.zhangyangjing.gamepadtest.gamepadmanager

import android.support.v4.view.InputDeviceCompat.*
import android.util.Log
import android.view.InputDevice
import android.view.InputDevice.SOURCE_MOUSE_RELATIVE
import android.view.InputEvent
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.MotionEvent
import java.util.*

/**
 * Created by zhangyangjing on 2018/8/4.
 */
class GamePad(val mDevice: InputDevice, private val clicked: (Int, Int) -> Unit) {
    private val listeners = LinkedList<Listener>()
    val mBtnStates = Array(BTN_COUNT) { false }
    val mAxisStates = Array(AXIS_COUNT) { 0f }

    fun handleEvent(event: InputEvent): Boolean {
        dump()
        return when (event::class) {
            KeyEvent::class -> handleKeyEvent(event as KeyEvent)
            MotionEvent::class -> handleMotionEvent(event as MotionEvent)
            else -> false
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    private fun handleKeyEvent(event: KeyEvent): Boolean {
        return sBtnCodeMap[event.keyCode]?.let {
            when (event.action) {
                ACTION_DOWN -> {
                    mBtnStates[it] = true
                    listeners.forEach { it.update() }
                    true
                }
                ACTION_UP -> {
                    mBtnStates[it] = false
                    takeIf { 200 > event.eventTime - event.downTime }?.apply { clicked(mDevice.id, it) }
                    listeners.forEach { it.update() }
                    true
                }
                else -> false
            }
        } ?: false
    }

    private fun handleMotionEvent(event: MotionEvent): Boolean {
        sAxisCodeMap.forEach { k, v -> mAxisStates[v] = getCenteredAxis(event, k) }

        mBtnStates[BTN_UP] = mAxisStates[AXIS_HAT_Y] < -0.6
        mBtnStates[BTN_DOWN] = mAxisStates[AXIS_HAT_Y] > 0.6
        mBtnStates[BTN_LEFT] = mAxisStates[AXIS_HAT_X] < -0.6
        mBtnStates[BTN_RIGHT] = mAxisStates[AXIS_HAT_X] > 0.6

        run {
            mBtnStates[BTN_UP] = mBtnStates[BTN_UP] || mAxisStates[AXIS_Y] < -0.6
            mBtnStates[BTN_DOWN] = mBtnStates[BTN_DOWN] || mAxisStates[AXIS_Y] > 0.6
            mBtnStates[BTN_LEFT] = mBtnStates[BTN_LEFT] || mAxisStates[AXIS_X] < -0.6
            mBtnStates[BTN_RIGHT] = mBtnStates[BTN_RIGHT] || mAxisStates[AXIS_X] > 0.6
        }

        listeners.forEach { it.update() }
        return true
    }

    private fun getCenteredAxis(event: MotionEvent, axis: Int): Float {
        val range = event.device?.getMotionRange(axis, event.source) ?: return 0f
        if (range != null) {
            val flat = range.flat
            val value = event.getAxisValue(axis)
            if (Math.abs(value) > flat)
                return value
        }
        return 0f
    }

    private fun dump() {
        Log.v(TAG, "------${mDevice.name}")
        Log.v(TAG, mAxisStates.mapIndexed { i, v -> "${sAxisNameMap[i]}:$v" }.joinToString())
        Log.v(TAG, mBtnStates.mapIndexed { i, v -> "${sBtnNameMap[i]}:${if(v) "t" else "f"}" }.joinToString())
    }

    interface Listener {
        fun update()
    }

    companion object {
        private val TAG = GamePad::class.java.simpleName

        // button
        const val BTN_A = 0
        const val BTN_B = 1
        const val BTN_X = 2
        const val BTN_Y = 3
        const val BTN_UP = 4
        const val BTN_DOWN = 5
        const val BTN_LEFT = 6
        const val BTN_RIGHT = 7
        const val BTN_L1 = 8
        const val BTN_L2 = 9
        const val BTN_R1 = 10
        const val BTN_R2 = 11
        const val BTN_SELECT = 12
        const val BTN_START = 13
        const val BTN_THUMBL = 14
        const val BTN_THUMBR = 15
        const val BTN_COUNT = 16

        // axis
        const val AXIS_HAT_X = 0
        const val AXIS_HAT_Y = 1
        const val AXIS_X = 2
        const val AXIS_Y = 3
        const val AXIS_Z = 4
        const val AXIS_RZ = 5
        const val AXIS_RTRIGGER = 6
        const val AXIS_LTRIGGER = 7
        const val AXIS_THROTTLE = 8
        const val AXIS_BRAKE = 9
        const val AXIS_COUNT = 10

        val sBtnCodeMap = mapOf(
                Pair(KEYCODE_BUTTON_A, BTN_A),
                Pair(KEYCODE_BUTTON_B, BTN_B),
                Pair(KEYCODE_BUTTON_X, BTN_X),
                Pair(KEYCODE_BUTTON_Y, BTN_Y),
                Pair(KEYCODE_DPAD_UP, BTN_UP),
                Pair(KEYCODE_DPAD_DOWN, BTN_DOWN),
                Pair(KEYCODE_DPAD_LEFT, BTN_LEFT),
                Pair(KEYCODE_DPAD_RIGHT, BTN_RIGHT),
                Pair(KEYCODE_BUTTON_L1, BTN_L1),
                Pair(KEYCODE_BUTTON_L2, BTN_L2),
                Pair(KEYCODE_BUTTON_R1, BTN_R1),
                Pair(KEYCODE_BUTTON_R2, BTN_R2),
                Pair(KEYCODE_BUTTON_SELECT, BTN_SELECT),
                Pair(KEYCODE_BUTTON_START, BTN_START),
                Pair(KEYCODE_BUTTON_THUMBL, BTN_THUMBL),
                Pair(KEYCODE_BUTTON_THUMBR, BTN_THUMBR))

        val sAxisCodeMap = mapOf(
                Pair(MotionEvent.AXIS_HAT_X, AXIS_HAT_X),
                Pair(MotionEvent.AXIS_HAT_Y, AXIS_HAT_Y),
                Pair(MotionEvent.AXIS_X, AXIS_X),
                Pair(MotionEvent.AXIS_Y, AXIS_Y),
                Pair(MotionEvent.AXIS_Z, AXIS_Z),
                Pair(MotionEvent.AXIS_RZ, AXIS_RZ),
                Pair(MotionEvent.AXIS_RTRIGGER, AXIS_RTRIGGER),
                Pair(MotionEvent.AXIS_LTRIGGER, AXIS_LTRIGGER),
                Pair(MotionEvent.AXIS_THROTTLE, AXIS_THROTTLE),
                Pair(MotionEvent.AXIS_BRAKE, AXIS_BRAKE))

        val sBtnNameMap = mapOf(
                Pair(BTN_A, "BTN_A"),
                Pair(BTN_B, "BTN_B"),
                Pair(BTN_X, "BTN_X"),
                Pair(BTN_Y, "BTN_Y"),
                Pair(BTN_UP, "BTN_UP"),
                Pair(BTN_DOWN, "BTN_DOWN"),
                Pair(BTN_LEFT, "BTN_LEFT"),
                Pair(BTN_RIGHT, "BTN_RIGHT"),
                Pair(BTN_L1, "BTN_L1"),
                Pair(BTN_L2, "BTN_L2"),
                Pair(BTN_R1, "BTN_R1"),
                Pair(BTN_R2, "BTN_R2"),
                Pair(BTN_SELECT, "BTN_SELECT"),
                Pair(BTN_START, "BTN_START"),
                Pair(BTN_THUMBL, "BTN_THUMBL"),
                Pair(BTN_THUMBR, "BTN_THUMBR"),
                Pair(BTN_COUNT, "BTN_COUNT"))

        val sAxisNameMap = mapOf(
                Pair(AXIS_HAT_X, "AXIS_HAT_X"),
                Pair(AXIS_HAT_Y, "AXIS_HAT_Y"),
                Pair(AXIS_X, "AXIS_X"),
                Pair(AXIS_Y, "AXIS_Y"),
                Pair(AXIS_Z, "AXIS_Z"),
                Pair(AXIS_RZ, "AXIS_RZ"),
                Pair(AXIS_LTRIGGER, "AXIS_LTRIGGER"),
                Pair(AXIS_RTRIGGER, "AXIS_RTRIGGER"),
                Pair(AXIS_THROTTLE, "AXIS_THROTTLE"),
                Pair(AXIS_BRAKE, "AXIS_BRAKE"))

        fun getSourcesDesc(sources: Int): String {
            return sSources.filter { it and sources == it }.joinToString {
                sSourceNames[it] ?: "unknow"
            }
        }

        private val sSources = listOf(
                SOURCE_DPAD, SOURCE_GAMEPAD, SOURCE_HDMI, SOURCE_JOYSTICK, SOURCE_KEYBOARD,
                SOURCE_MOUSE, SOURCE_MOUSE_RELATIVE, SOURCE_ROTARY_ENCODER, SOURCE_STYLUS,
                SOURCE_TOUCHPAD, SOURCE_TOUCHSCREEN, SOURCE_TOUCH_NAVIGATION, SOURCE_TRACKBALL)

        private val sSourceNames = mapOf(
                Pair(SOURCE_DPAD, "DPAD"),
                Pair(SOURCE_GAMEPAD, "GAMEPAD"),
                Pair(SOURCE_HDMI, "HDMI"),
                Pair(SOURCE_JOYSTICK, "JOYSTICK"),
                Pair(SOURCE_KEYBOARD, "KEYBOARD"),
                Pair(SOURCE_MOUSE, "MOUSE"),
                Pair(SOURCE_MOUSE_RELATIVE, "RELATIVE"),
                Pair(SOURCE_ROTARY_ENCODER, "ENCODER"),
                Pair(SOURCE_STYLUS, "STYLUS"),
                Pair(SOURCE_TOUCHPAD, "TOUCHPAD"),
                Pair(SOURCE_TOUCHSCREEN, "TOUCHSCREEN"),
                Pair(SOURCE_TOUCH_NAVIGATION, "TOUCH_NAVIGATION"),
                Pair(SOURCE_TRACKBALL, "TRACKBALL"))
    }
}
