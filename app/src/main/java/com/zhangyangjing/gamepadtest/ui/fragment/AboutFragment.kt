package com.zhangyangjing.gamepadtest.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhangyangjing.gamepadtest.R
import kotlinx.android.synthetic.main.fragment_about.*


/**
 * Created by zhangyangjing on 2018/8/14.
 */
class AboutFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_github.setOnClickListener {
            CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                    .setSecondaryToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
                    .build()
                    .launchUrl(context, Uri.parse("https://github.com/zhangyangjing/gamepadtest"))
        }
    }
}
