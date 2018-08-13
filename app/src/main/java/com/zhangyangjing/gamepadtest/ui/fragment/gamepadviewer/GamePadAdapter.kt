package com.zhangyangjing.gamepadtest.ui.fragment.gamepadviewer

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import kotlinx.android.synthetic.main.item_pad.view.*

/**
 * Created by zhangyangjing on 2018/8/13.
 */
class GamePadAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var gamePads: List<GamePad>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_pad, null)
        return object : RecyclerView.ViewHolder(view) { }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val gamePad = gamePads?.get(position) ?: return
        val device = gamePad.device

        val sb = StringBuilder()
        sb.append("id: ${device.id}")
        sb.append("\nname: ${device.name}")
        sb.append("\ndesc: ${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) device.descriptor else "none"}")
        sb.append("\nsource: ${GamePad.getSourcesDesc(device.sources)}")
        holder.itemView.info.text = sb.toString()
        holder.itemView.viewer.gamePad = gamePad
    }

    override fun getItemCount(): Int {
        return gamePads?.count() ?: 0
    }
}
