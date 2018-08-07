package com.zhangyangjing.gamepadtest

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.*
import android.view.*
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePad
import com.zhangyangjing.gamepadtest.gamepadmanager.GamePadManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.item_pad.view.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GamePadManager.GamepadListener {
    private val TAG = MainActivity::class.java.simpleName

    override fun gamepadUpdate() {
//        pad_container.removeAllViews()
//        mGamePadManager.mGamePads?.forEach {
////            val viewer = GamePadViewer(this, mGamePadManager, it.key)
////            pad_container.addView(viewer)
//
//            val view = View.inflate(this, R.layout.item_pad, null)
//            pad_container.addView(view)
//            view.viewer.gamePad = it.value
//
////            val params = viewer.layoutParams as LinearLayoutCompat.LayoutParams
////            params.width = ViewGroup.LayoutParams.MATCH_PARENT
////            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
////            params.weight = 1f
////            viewer.layoutParams = params
//        }

        (list.adapter as Adapter).gamePads = mGamePadManager?.mGamePads?.values?.toList()
    }

    private lateinit var mGamePadManager: GamePadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mGamePadManager = GamePadManager(this)
        mGamePadManager.addGamepadListener(this)
//        gamepadUpdate()

//        val view = View.inflate(this, R.layout.item_pad, null)
//        pad_container.addView(view)
//        view.viewer.gamePad = mGamePadManager.mGamePads?.values?.first()

//        supportFragmentManager.beginTransaction().add(R.id.pad_container, ViewerContainer(mGamePadManager)).commitAllowingStateLoss()

        val orientation = if (resources.configuration.orientation == ORIENTATION_PORTRAIT) VERTICAL else HORIZONTAL
        list.layoutManager = LinearLayoutManager(this, orientation, false)
//        list.layoutManager = LinearLayoutManager(this)
        list.adapter = Adapter()
    }

    class Adapter : RecyclerView.Adapter<ViewHolder>() {

        var gamePads: List<GamePad>? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = View.inflate(parent.context, R.layout.item_pad, null)
            return object : ViewHolder(view) { }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val gamePad = gamePads?.get(position) ?: return
            val device = gamePad.mDevice

            holder.itemView.viewer.gamePad = gamePad
            holder.itemView.info.text = "name: ${device.name}\nsource: ${GamePad.getSourcesDesc(device.sources)}"
        }

        override fun getItemCount(): Int {
            return gamePads?.count() ?: 0
        }
    }

    override fun onResume() {
        super.onResume()
        mGamePadManager.resume()
    }

    override fun onPause() {
        super.onPause()
        mGamePadManager.pause()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent) = mGamePadManager.handleEvent(event) || super.onKeyUp(keyCode, event)
    override fun onKeyDown(keyCode: Int, event: KeyEvent) = mGamePadManager.handleEvent(event) || super.onKeyDown(keyCode, event)
    override fun onGenericMotionEvent(event: MotionEvent) = mGamePadManager.handleEvent(event) || super.onGenericMotionEvent(event)
}


