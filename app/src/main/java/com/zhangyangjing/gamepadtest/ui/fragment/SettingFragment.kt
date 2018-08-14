package com.zhangyangjing.gamepadtest.ui.fragment

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.zhangyangjing.gamepadtest.R
import com.zhangyangjing.gamepadtest.util.Settings

/**
 * Created by zhangyangjing on 2018/8/14.
 */
class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = Settings.PREF_NAME
        setPreferencesFromResource(R.xml.pref_setting, rootKey)
    }
}
