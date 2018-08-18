package com.zhangyangjing.gamepadtest.ui.fragment.logviewer

import android.support.v4.util.CircularArray
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.R
import kotlinx.android.synthetic.main.item_log.view.*
import lt.neworld.spanner.Spanner

/**
 * Created by zhangyangjing on 2018/8/13.
 */
class LogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mMessages = CircularArray<SpannableStringBuilder>(MAX_MESSAGES)

    fun clearMessage() {
        mMessages.clear()
        notifyDataSetChanged()
    }

    fun addSplitter() {
        addMessage(Spanner(SPLITTER))
    }

    fun addMessage(msg: SpannableStringBuilder) {
        mMessages.addLast(msg)
        if (mMessages.size() > MAX_MESSAGES)
            mMessages.popFirst()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mMessages.size()
    }

    override fun getItemViewType(position: Int): Int {
        return if (SPLITTER == mMessages[position].toString()) ITEM_TYPE_SPLITTER else ITEM_TYPE_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_MESSAGE -> {
                val view = View.inflate(parent.context, R.layout.item_log, null)
                MessageViewHolder(view)
            }
            ITEM_TYPE_SPLITTER -> {
                val view = View.inflate(parent.context, R.layout.item_log_splitter, null)
                SplitterViewHolder(view)
            }
            else -> { object : RecyclerView.ViewHolder(null) {} }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder -> holder.itemView.tv_message.text = mMessages[position]
        }
    }

    companion object {
        private const val MAX_MESSAGES = 300
        private const val SPLITTER = "--------------"

        private const val ITEM_TYPE_MESSAGE = 0
        private const val ITEM_TYPE_SPLITTER = 1
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class SplitterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
