package com.zhangyangjing.gamepadtest.ui.fragment.gamepadviewer

import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import com.zhangyangjing.gamepadtest.MainActivity
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.GamePadListener
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager.IGamePadListener
import kotlinx.android.synthetic.main.fragment_gamepad_viewer.*

class GamePadViewerFragment : Fragment(), IGamePadListener by GamePadListener() {
    private var host: MainActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gamepad_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orientation = if (resources.configuration.orientation == ORIENTATION_PORTRAIT) VERTICAL else HORIZONTAL
        list.layoutManager = LinearLayoutManager(context, orientation, false)
        list.adapter = GamePadAdapter()
    }

    override fun onStart() {
        super.onStart()
        host?.gamePadManager?.addGamePadListener(this)
        gamePadUpdate()
    }

    override fun onStop() {
        super.onStop()
        host?.gamePadManager?.removeGamePadListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity)
            host = context
    }

    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun gamePadUpdate() {
        (list.adapter as GamePadAdapter).gamePads = host?.gamePadManager?.gamePads?.values?.toList()
    }
}
