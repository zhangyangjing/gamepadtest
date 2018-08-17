package com.zhangyangjing.gamepadtest.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.*
import com.zhangyangjing.gamepadtest.MainActivity
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad.Companion.TYPE_AXIS
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.GamePadListener
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.IGamePadListener
import com.zhangyangjing.gamepadtest.util.Settings
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_ID
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_NAME
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_SOURCE
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_LOG_LAB_TIME
import kotlinx.android.synthetic.main.fragment_log_viewer.*
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans

class LogViewerFragment : Fragment(), IGamePadListener by GamePadListener(), GamePad.Listener {

    private var host: MainActivity? = null
    private lateinit var mPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mPref = context!!.getSharedPreferences(Settings.PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_log_viewer, container, false)
    }

    override fun onStart() {
        super.onStart()
        host?.gamePadManager?.addGamePadListener(this)
        registerGamePad()
    }

    override fun onStop() {
        super.onStop()
        host?.gamePadManager?.removeGamePadListener(this)
        unregisterGamePad()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity)
            host = context
    }

    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun gamePadAdded(gamePad: GamePad) = gamePad.addListener(this)
    override fun gamePadRemoved(gamePad: GamePad) = gamePad.removeListener(this)

    override fun gamePadEvent(event: InputEvent) {
        val isKeyEvent = event is KeyEvent
        val enableKeyEvent = mPref.getBoolean(Settings.PREF_KEY_LOG_ENABLE_KEY_EVENT, true)
        if (!isKeyEvent || !enableKeyEvent)
            return

        val message = formatKeyEvent(event as KeyEvent)
        log_viewer.addMessage(message)
    }

    override fun onStateUpdate(gamePad: GamePad, type: Int, code: Int) {
        if (TYPE_AXIS != type)
            return

        val enableMotionEvent = mPref.getBoolean(Settings.PREF_KEY_LOG_ENABLE_KEY_EVENT, true)
        if (!enableMotionEvent)
            return

        val value = gamePad.axisStates[code]
        val message = formatMotionEvent(gamePad.device, gamePad.device.sources, code, value)
        log_viewer.addMessage(message)
    }

    override fun onBtnClick(code: Int) {
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_ops, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_clear -> {
                log_viewer.clearMessage(); true
            }
            R.id.item_add_splitter -> {
                log_viewer.addMessage("-------------------"); true
            }
            else -> false
        }
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

    private inline fun formatCurrentTime() = DateFormat.format("hh:mm:ss.SSS", System.currentTimeMillis()).toString()

    private inline fun formatKeyEventKeyCode(key: Int) = KeyEvent.keyCodeToString(key).substring(8)

    private inline fun formatKeyEventAction(action: Int) = when (action) {
        KeyEvent.ACTION_UP -> "UP"
        KeyEvent.ACTION_DOWN -> "DOWN"
        KeyEvent.ACTION_MULTIPLE -> "MULTIPLE"
        else -> "UNKNOWN"
    }

    private fun registerGamePad() {
        host?.gamePadManager?.gamePads?.forEach { it.value.addListener(this) }
    }

    private fun unregisterGamePad() {
        host?.gamePadManager?.gamePads?.forEach { it.value.removeListener(this) }
    }

    companion object {
        private val LOG_LABS = listOf(
                PREF_KEY_LOG_LAB_TIME to false,
                PREF_KEY_LOG_LAB_ID to false,
                PREF_KEY_LOG_LAB_NAME to false,
                PREF_KEY_LOG_LAB_SOURCE to false)
    }
}
