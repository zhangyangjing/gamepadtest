package com.zhangyangjing.gamepadtest.util

/**
 * Created by zhangyangjing on 2018/8/14.
 */
class Settings {

    companion object {
        const val PREF_NAME = "settings"

        const val PREF_KEY_INTERCEPT_KEY_EVENT = "intercept_key_event"
        const val PREF_KEY_TRANSFORM_STICK = "transform_stick"
        const val PREF_KEY_LOG_ENABLE_KEY_EVENT = "log_enable_key_event"
        const val PREF_KEY_LOG_ENABLE_MOTION_EVENT = "log_enable_motion_event"

        const val PREF_KEY_LOG_LAB_TIME = "log_lab_time"
        const val PREF_KEY_LOG_LAB_ID = "log_lab_id"
        const val PREF_KEY_LOG_LAB_NAME = "log_lab_name"
        const val PREF_KEY_LOG_LAB_SOURCE = "log_lab_source"

        const val PREF_KEY_PAD_LAB_ID = "pad_lab_id"
        const val PREF_KEY_PAD_LAB_NAME = "pad_lab_name"
        const val PREF_KEY_PAD_LAB_SOURCE = "pad_lab_source"
        const val PREF_KEY_PAD_LAB_DESC = "pad_lab_desc"
    }
}
