package com.zhangyangjing.gamepadtest.ui.fragment.logviewer

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Color
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.TYPE_AXIS
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager
import com.zhangyangjing.gamepadtest.util.Settings
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_ID
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_NAME
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_SOURCE
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_TIME
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zhangyangjing on 2018/8/18.
 */
class LogWriter(context: Context, private val gamePadManager: GamePadManager, private val logAdapter: LogAdapter) : LifecycleObserver, GamePadManager.IListener by GamePadManager.Listener(), GamePad.IListener by GamePad.Listener() {

    private val formatter = SimpleDateFormat("hh:mm:ss.SSS")
    private val mPref = context.getSharedPreferences(Settings.PREF_NAME, Context.MODE_PRIVATE)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        gamePadManager.addGamePadListener(this)
        registerGamePad()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        gamePadManager.removeGamePadListener(this)
        unregisterGamePad()
    }

    override fun gamePadAdded(gamePad: GamePad) = gamePad.addListener(this)
    override fun gamePadRemoved(gamePad: GamePad) = gamePad.removeListener(this)

    override fun gamePadEvent(event: InputEvent) {
        val isKeyEvent = event is KeyEvent
        val enableKeyEvent = mPref.getBoolean(Settings.PREF_KEY_LOG_ENABLE_KEY_EVENT, true)
        if (!isKeyEvent || !enableKeyEvent)
            return

        val message = formatKeyEvent(event as KeyEvent)
        logAdapter.addMessage(message)
    }

    override fun onGamePadStateUpdate(gamePad: GamePad, type: Int, code: Int) {
        if (TYPE_AXIS != type)
            return

        val enableMotionEvent = mPref.getBoolean(Settings.PREF_KEY_LOG_ENABLE_KEY_EVENT, true)
        if (!enableMotionEvent)
            return

        val value = gamePad.axisStates[code]
        val message = formatMotionEvent(gamePad.device, gamePad.device.sources, code, value)
        logAdapter.addMessage(message)
    }

    private inline fun formatKeyEvent(event: KeyEvent) = geEventDesc(event.device, event.source)
            .append(" ")
            .append(formatKeyEventKeyCode(event.keyCode), Spans.foreground(Color.RED))
            .append(" ")
            .append(formatKeyEventAction(event.action), Spans.foreground(Color.GREEN))

    private inline fun formatMotionEvent(device: InputDevice, source: Int, code: Int, value: Float) = geEventDesc(device, source)
            .append(" ")
            .append(GamePad.sAxisNameMap[code], Spans.foreground(Color.RED))
            .append(" ")
            .append(value.toString(), Spans.foreground(Color.GREEN))

    private inline fun geEventDesc(device: InputDevice, source: Int) = LOG_LABS
            .filter { mPref.getBoolean(it.first, it.second) }
            .map { getLabelDesc(device, source, it.first) }
            .reduce { sum, ele -> sum.append(" ").append(ele) }

    private inline fun getLabelDesc(device: InputDevice, source: Int, lab: String) = when (lab) {
        PREF_KEY_LOG_LAB_TIME -> Spanner().append(formatCurrentTime(), Spans.foreground(Color.BLUE))
        PREF_KEY_LOG_LAB_ID -> Spanner().append(device.id.toString(), Spans.foreground(Color.MAGENTA))
        PREF_KEY_LOG_LAB_NAME -> Spanner().append(device.name, Spans.foreground(Color.CYAN))
        PREF_KEY_LOG_LAB_SOURCE -> Spanner().append(GamePad.getSourcesDesc(source), Spans.foreground(Color.YELLOW))
        else -> Spanner()
    }

    private inline fun formatCurrentTime() = formatter.format(Date(System.currentTimeMillis()))

    private inline fun formatKeyEventKeyCode(key: Int) = KeyEvent.keyCodeToString(key).substring(8)

    private inline fun formatKeyEventAction(action: Int) = when (action) {
        KeyEvent.ACTION_UP -> "UP"
        KeyEvent.ACTION_DOWN -> "DOWN"
        KeyEvent.ACTION_MULTIPLE -> "MULTIPLE"
        else -> "UNKNOWN"
    }

    private fun registerGamePad() {
        gamePadManager.gamePads?.forEach { it.value.addListener(this) }
    }

    private fun unregisterGamePad() {
        gamePadManager.gamePads?.forEach { it.value.removeListener(this) }
    }

    companion object {
        private val LOG_LABS = listOf(
                PREF_KEY_LOG_LAB_TIME to false,
                PREF_KEY_LOG_LAB_ID to false,
                PREF_KEY_LOG_LAB_NAME to false,
                PREF_KEY_LOG_LAB_SOURCE to false)
    }
}
