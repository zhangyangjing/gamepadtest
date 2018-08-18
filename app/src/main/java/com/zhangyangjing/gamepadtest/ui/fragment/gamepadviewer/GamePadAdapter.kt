package com.zhangyangjing.gamepadtest.ui.fragment.gamepadviewer

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.view.InputDevice
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import com.zhangyangjing.gamepadtest.util.Settings
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_PAD_LAB_DESC
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_PAD_LAB_ID
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_PAD_LAB_NAME
import com.zhangyangjing.gamepadtest.util.Settings.Companion.PREF_KEY_PAD_LAB_SOURCE
import kotlinx.android.synthetic.main.item_pad.view.*
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans

/**
 * Created by zhangyangjing on 2018/8/13.
 */
class GamePadAdapter(private val mCtx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mPref = mCtx.getSharedPreferences(Settings.PREF_NAME, Context.MODE_PRIVATE)

    var gamePads: List<GamePad>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = View.inflate(mCtx, R.layout.item_pad, null)
        return object : RecyclerView.ViewHolder(view) { }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val gamePad = gamePads?.get(position) ?: return

        holder.itemView.viewer.gamePad = gamePad
        holder.itemView.info.text = PAD_LABS
                .filter { mPref.getBoolean(it.first, it.second) }
                .map { getPadLabel(gamePad.device, it.first) }
                .reduce { sum, ele -> sum.append("\n").append(ele) }
    }

    private fun getPadLabel(device: InputDevice, label: String): SpannableStringBuilder {
        return when (label) {
            PREF_KEY_PAD_LAB_ID -> Spanner().append("id: ", Spans.bold()).append(device.id.toString())
            PREF_KEY_PAD_LAB_NAME -> Spanner().append("name: ", Spans.bold()).append(device.name)
            PREF_KEY_PAD_LAB_SOURCE -> Spanner().append("source: ", Spans.bold()).append(GamePad.getSourcesDesc(device.sources))
            PREF_KEY_PAD_LAB_DESC -> Spanner().append("desc: ", Spans.bold()).append(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) device.descriptor else "unknown")
            else -> Spanner("__unknown__")
        }
    }

    override fun getItemCount(): Int {
        return gamePads?.count() ?: 0
    }

    companion object {
        private val PAD_LABS = listOf(
                PREF_KEY_PAD_LAB_ID to false,
                PREF_KEY_PAD_LAB_NAME to true,
                PREF_KEY_PAD_LAB_SOURCE to true,
                PREF_KEY_PAD_LAB_DESC to false)
    }
}
