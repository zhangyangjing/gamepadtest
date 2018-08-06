package com.zhangyangjing.gamepadtest.gamepadviewer

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager

/**
 * Created by zhangyangjing on 2018/8/5.
 */
@SuppressLint("ValidFragment")
class ViewerContainer(val gamePadManager: GamePadManager): Fragment() {
    private val TAG = ViewerContainer::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LinearLayoutCompat(inflater.context, null)
        view.orientation = LinearLayoutCompat.VERTICAL
        view.setBackgroundColor(Color.CYAN)
        Log.v(TAG, "ViewerContainer: ${view.layoutParams}")

//        val viewer = GamePadViewer(context!!, gamePadManager, 0)
//        view.addView(viewer)

//        val params = viewer.layoutParams as LinearLayoutCompat.LayoutParams
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
//        params.weight = 1f
//        viewer.layoutParams = params

        return view

//        val textView = TextView(context)
//        textView.text = "ssssssssssss"
//        return textView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
