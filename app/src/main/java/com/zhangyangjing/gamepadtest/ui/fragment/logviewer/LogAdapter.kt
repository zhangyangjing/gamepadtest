package com.zhangyangjing.gamepadtest.ui.fragment.logviewer

import android.support.v4.util.CircularArray
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.R
import kotlinx.android.synthetic.main.item_log.view.*

/**
 * Created by zhangyangjing on 2018/8/13.
 */
class LogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mMessages = CircularArray<CharSequence>(MAX_MESSAGES)

    fun clearMessage() {
        mMessages.clear()
        notifyDataSetChanged()
    }

    fun addMessage(msg: CharSequence) {
        mMessages.addLast(msg)
        if (mMessages.size() > MAX_MESSAGES)
            mMessages.popFirst()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tv_message.text = mMessages[position]
    }

    override fun getItemCount(): Int {
        return mMessages.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_log, null)
        return object : RecyclerView.ViewHolder(view) {}
    }

    companion object {
        private const val MAX_MESSAGES = 100
    }
}
