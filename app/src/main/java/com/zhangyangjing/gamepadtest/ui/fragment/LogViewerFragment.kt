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

class LogViewerFragment : Fragment(), IGamePadListener by GamePadListener() {
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
    }

    override fun onStop() {
        super.onStop()
        host?.gamePadManager?.removeGamePadListener(this)
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

    override fun gamePadEvent(event: InputEvent) {
        event.takeIf { filterEvent(it) }?.let { formatEvent(it) }?.let { log_viewer.addMessage(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_ops, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_clear -> { log_viewer.clearMessage(); true }
            R.id.item_add_splitter -> { log_viewer.addMessage("-------------------");  true }
            else -> false
        }
    }

    private fun filterEvent(event: InputEvent): Boolean {
        return when (event) {
            is KeyEvent -> mPref.getBoolean(Settings.PREF_KEY_LOG_ENABLE_KEY_EVENT, true)
            is MotionEvent -> mPref.getBoolean(Settings.PREF_KEY_LOG_ENABLE_MOTION_EVENT, true)
            else -> false
        }
    }

    private inline fun formatEvent(event: InputEvent): CharSequence? {
        return when (event::class) {
            KeyEvent::class -> formatKeyEvent(event as KeyEvent)
            MotionEvent::class -> formatMotionEvent(event as MotionEvent)
            else -> null
        }
    }

    private inline fun formatMotionEvent(event: MotionEvent): String {
        return "MOTION ${event.action}"
    }

    private inline fun formatKeyEvent(event: KeyEvent): Spanner {
        return LOG_LABS
                .filter { mPref.getBoolean(it.first, it.second) }
                .map { getKeyEventDesc(event, it.first) }
                .reduce { sum, ele  -> sum.append(" ").append(ele) }
                .append(" ")
                .append(formatKeyEventKeyCode(event.keyCode), Spans.foreground(Color.RED))
                .append(" ")
                .append(formatKeyEventAction(event.action), Spans.foreground(Color.GREEN))
    }

    private inline fun getKeyEventDesc(event: KeyEvent, lab: String): Spanner {
        return when (lab) {
            PREF_KEY_LOG_LAB_TIME -> Spanner().append(formatCurrentTime(), Spans.foreground(Color.BLUE))
            PREF_KEY_LOG_LAB_ID -> Spanner().append(event.device.id.toString(), Spans.foreground(Color.MAGENTA))
            PREF_KEY_LOG_LAB_NAME -> Spanner().append(event.device.name, Spans.foreground(Color.CYAN))
            PREF_KEY_LOG_LAB_SOURCE -> Spanner().append(GamePad.getSourcesDesc(event.source), Spans.foreground(Color.YELLOW))
            else -> Spanner()
        }
    }

    private inline fun formatCurrentTime(): String {
        return DateFormat.format("hh:mm:ss.SSS", System.currentTimeMillis()).toString()
    }

    private inline fun formatKeyEventKeyCode(key: Int): String {
        return KeyEvent.keyCodeToString(key).substring(8)
    }

    private inline fun formatKeyEventAction(action: Int): String {
        return when (action) {
            KeyEvent.ACTION_UP -> "UP"
            KeyEvent.ACTION_DOWN -> "DOWN"
            KeyEvent.ACTION_MULTIPLE -> "MULTIPLE"
            else -> "UNKNOWN"
        }
    }

    companion object {
        private val LOG_LABS = listOf(
                PREF_KEY_LOG_LAB_TIME to false,
                PREF_KEY_LOG_LAB_ID to false,
                PREF_KEY_LOG_LAB_NAME to false,
                PREF_KEY_LOG_LAB_SOURCE to false)
    }
}
