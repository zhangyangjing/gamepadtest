package com.zhangyangjing.gamepadtest.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.zhangyangjing.gamepadtest.MainActivity
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.GamePadListener
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.IGamePadListener
import kotlinx.android.synthetic.main.fragment_log_viewer.*

class LogViewerFragment : Fragment(), IGamePadListener by GamePadListener() {
    private var host: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        return true
    }

    private inline fun formatEvent(event: InputEvent): String? {
        return when (event::class) {
            KeyEvent::class -> formatKeyEvent(event as KeyEvent)
            MotionEvent::class -> formatMotionEvent(event as MotionEvent)
            else -> null
        }
    }

    private inline fun formatMotionEvent(event: MotionEvent): String {
        return "MOTION ${event.action}"
    }

    private inline fun formatKeyEvent(event: KeyEvent): String {
        val key = formatKeyEventKeyCode(event.keyCode)
        val action = formatKeyEventAction(event.action)
        return "$key $action"
    }

    private inline fun formatKeyEventKeyCode(key: Int): String {
        return KeyEvent.keyCodeToString(key).substring(8)
    }

    private inline fun formatKeyEventAction(action: Int): String {
        return when (action) {
            KeyEvent.ACTION_UP -> "UP"
            KeyEvent.ACTION_DOWN -> "DOWN"
            KeyEvent.ACTION_MULTIPLE -> "MULTIPLE"
            else -> "UNKNOW"
        }
    }
}
