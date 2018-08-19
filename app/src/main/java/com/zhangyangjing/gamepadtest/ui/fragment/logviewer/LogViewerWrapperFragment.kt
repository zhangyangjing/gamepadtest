package com.zhangyangjing.gamepadtest.ui.fragment.logviewer

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.MainActivity
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.ui.fragment.EmptyFragment


class LogViewerWrapperFragment : Fragment() {
    private var host: MainActivity? = null

    private val mAdapterObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateContent()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wrapper, container, false)
    }

    override fun onStart() {
        super.onStart()
        host?.logAdapter?.registerAdapterDataObserver(mAdapterObserver)
        updateContent()
    }

    override fun onStop() {
        super.onStop()
        host?.logAdapter?.unregisterAdapterDataObserver(mAdapterObserver)
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

    private fun updateContent() {
        val cnt = host?.logAdapter?.itemCount ?: 0
        val fragment = if (cnt > 0) LogViewerFragment() else EmptyFragment.ins("暂无手柄事件发生")
        childFragmentManager.beginTransaction().replace(R.id.wrapper, fragment).commitAllowingStateLoss()
    }
}
