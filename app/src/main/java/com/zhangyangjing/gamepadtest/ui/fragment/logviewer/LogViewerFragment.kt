package com.zhangyangjing.gamepadtest.ui.fragment.logviewer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.zhangyangjing.gamepadtest.MainActivity
import com.zhangyangjing.gamepadtest.R
import kotlinx.android.synthetic.main.fragment_log_viewer.*


class LogViewerFragment : Fragment() {
    private var host: MainActivity? = null

    private val mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            log_viewer.layoutManager.scrollToPosition(log_viewer.adapter.itemCount - 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_log_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log_viewer.layoutManager = LinearLayoutManager(context)
        log_viewer.adapter = host?.logAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_ops, menu)

        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)

            var drawable = item.icon
            drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(drawable, Color.WHITE)
            item.icon = drawable
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_clear -> {
                host?.logAdapter?.clearMessage(); true
            }
            R.id.item_add_splitter -> {
                host?.logAdapter?.addSplitter(); true
            }
            else -> false
        }
    }

    override fun onStart() {
        super.onStart()
        host?.logAdapter?.registerAdapterDataObserver(mAdapterDataObserver)
    }

    override fun onStop() {
        super.onStop()
        host?.logAdapter?.unregisterAdapterDataObserver(mAdapterDataObserver)
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
}
