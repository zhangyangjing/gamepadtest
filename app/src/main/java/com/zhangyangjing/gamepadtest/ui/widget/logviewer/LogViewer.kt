package com.zhangyangjing.gamepadtest.ui.widget.logviewer

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

/**
 * Created by zhangyangjing on 2018/8/13.
 */
class LogViewer(context: Context, attrs: AttributeSet, private val mAdapter: LogAdapter) : RecyclerView(context, attrs), ILog by mAdapter {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, LogAdapter()) {
        layoutManager = LinearLayoutManager(context)
        adapter = mAdapter

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                smoothScrollToPosition(adapter.itemCount - 1)
            }
        })
    }
}
