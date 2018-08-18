package com.zhangyangjing.gamepadtest

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager
import com.zhangyangjing.gamepadtest.ui.fragment.SettingFragment
import com.zhangyangjing.gamepadtest.ui.fragment.gamepadviewer.GamePadViewerFragment
import com.zhangyangjing.gamepadtest.ui.fragment.logviewer.LogAdapter
import com.zhangyangjing.gamepadtest.ui.fragment.logviewer.LogViewerFragment
import com.zhangyangjing.gamepadtest.util.Settings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private var mNavNow: Int = INVALIDATE_ID
    private lateinit var mPref: SharedPreferences

    val logAdapter: LogAdapter = LogAdapter()
    lateinit var gamePadManager: GamePadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        navTo(R.id.nav_game_pad)

        gamePadManager = GamePadManager(this)

        mPref = getSharedPreferences(Settings.PREF_NAME, Context.MODE_PRIVATE)
        updateGamePadManagerSettings()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        navTo(mNavNow, true)
    }

    override fun onResume() {
        super.onResume()
        gamePadManager.resume()
        mPref.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        gamePadManager.pause()
        mPref.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        navTo(item.itemId)
        return true
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        updateGamePadManagerSettings()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent) = gamePadManager.handleEvent(event) || super.onKeyUp(keyCode, event)
    override fun onKeyDown(keyCode: Int, event: KeyEvent) = gamePadManager.handleEvent(event) || super.onKeyDown(keyCode, event)
    override fun onGenericMotionEvent(event: MotionEvent) = gamePadManager.handleEvent(event) || super.onGenericMotionEvent(event)

    private fun updateGamePadManagerSettings() {
        gamePadManager.enableDpadTransform = mPref.getBoolean(Settings.PREF_KEY_TRANSFORM_STICK, true)
        gamePadManager.enableKeyEventIntercept = mPref.getBoolean(Settings.PREF_KEY_INTERCEPT_KEY_EVENT, true)
    }

    private fun navTo(navId: Int, forceReload: Boolean = false) {
        takeIf { forceReload || navId != mNavNow }?.let { mNavNow = navId } ?: return

        nav_view.setCheckedItem(navId)
        val fragment = when (navId) {
            R.id.nav_game_pad -> GamePadViewerFragment()
            R.id.nav_log -> LogViewerFragment()
            R.id.nav_settings -> SettingFragment()
            else -> Fragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commitAllowingStateLoss()
    }

    companion object {
        private const val INVALIDATE_ID = -1
    }
}


