package com.zhangyangjing.gamepadtest.ui.fragment.gamepadviewer

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.MainActivity
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager
import com.zhangyangjing.gamepadtest.ui.fragment.EmptyFragment


class GamePadViewerWrapperFragment : Fragment(), GamePadManager.IListener by GamePadManager.Listener() {
    private var host: MainActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wrapper, container, false)
    }

    override fun onStart() {
        super.onStart()
        host?.gamePadManager?.addGamePadListener(this)
        updateContent()
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

    override fun gamePadAdded(gamePad: GamePad) {
        updateContent()
    }

    override fun gamePadRemoved(gamePad: GamePad) {
        updateContent()
    }

    private fun updateContent() {
        val cnt = host?.gamePadManager?.gamePads?.size ?: 0
        val fragment = if (cnt > 0) GamePadViewerFragment() else EmptyFragment.ins("没有检测到手柄设备")
        childFragmentManager.beginTransaction().replace(R.id.wrapper, fragment).commitAllowingStateLoss()
    }
}
