package com.zhangyangjing.gamepadtest

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
import com.zhangyangjing.gamepadtest.ui.fragment.LogViewerFragment
import com.zhangyangjing.gamepadtest.ui.fragment.gamepadviewer.GamePadViewerFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mNavNow: Int? = null
    lateinit var gamePadManager: GamePadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        navTo(R.id.nav_camera)

        gamePadManager = GamePadManager(this)
    }

    override fun onResume() {
        super.onResume()
        gamePadManager.resume()
    }

    override fun onPause() {
        super.onPause()
        gamePadManager.pause()
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

    override fun onKeyUp(keyCode: Int, event: KeyEvent) = gamePadManager.handleEvent(event) || super.onKeyUp(keyCode, event)
    override fun onKeyDown(keyCode: Int, event: KeyEvent) = gamePadManager.handleEvent(event) || super.onKeyDown(keyCode, event)
    override fun onGenericMotionEvent(event: MotionEvent) = gamePadManager.handleEvent(event) || super.onGenericMotionEvent(event)

    private fun navTo(navId: Int) {
        takeIf { navId != mNavNow }?.let { mNavNow = navId } ?: return

        nav_view.setCheckedItem(navId)
        val fragment = when (navId) {
            R.id.nav_camera -> GamePadViewerFragment()
            R.id.nav_gallery -> LogViewerFragment()
            else -> Fragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commitAllowingStateLoss()
    }
}


