package com.zhangyangjing.gamepadtest.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.InputEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.MainActivity
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.GamePadListener
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.IGamePadListener
import kotlinx.android.synthetic.main.fragment_log_viewer.*

class LogViewerFragment : Fragment(), IGamePadListener by GamePadListener() {
    private var host: MainActivity? = null

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
        log_viewer.addMessage(event.toString())
    }
}
