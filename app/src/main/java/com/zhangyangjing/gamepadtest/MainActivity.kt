package com.zhangyangjing.gamepadtest

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutCompat
import android.util.Log
import android.view.*
import android.view.InputDevice.*
import android.view.KeyEvent.*
import com.zhangyangjing.gamepadtest.gamepadviewer.GamePadViewer
import com.zhangyangjing.gamepadtest.inputmanagercompat.InputManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,GamePadManager.GamepadListener {
    private val TAG = MainActivity::class.java.simpleName

    override fun gamepadUpdate() {
        pad_container.removeAllViews()
        mGamePadManager.mGamePads?.forEach {
            val viewer = GamePadViewer(this, mGamePadManager, it.key)
            pad_container.addView(viewer)

            val params = viewer.layoutParams as LinearLayoutCompat.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.weight = 1f
            viewer.layoutParams = params
        }
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
        gamepadUpdate()
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

class GamePadManager(mCtx: Context) : InputManagerCompat.InputDeviceListener {
    var mGamePads: SortedMap<Int, GamePad>? = null
    private val mInputManager = InputManagerCompat.Factory.getInputManager(mCtx)!!
    private val listeners = LinkedList<Listener>()
    private val gampadlisteners = LinkedList<GamepadListener>()

    fun resume() {
        mInputManager.registerInputDeviceListener(this, Handler(Looper.getMainLooper()))
        mInputManager.onResume()
        updateGamePads()
    }

    fun pause() {
        mInputManager.unregisterInputDeviceListener(this)
        mInputManager.onPause()
    }

    fun handleEvent(event: InputEvent): Boolean {
        Log.v(TAG, "handleEvent: $event ${GamePad.getSourcesDesc(event.source)}")
        if (event is MotionEvent) mInputManager.onGenericMotionEvent(event)
        val result = mGamePads?.get(event.deviceId)?.let { it.handleEvent(event) } ?: return false
        listeners.forEach { it.update() }
        return result
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun addGamepadListener(gamepadListener: GamepadListener) {
        gampadlisteners.add(gamepadListener)
    }

    override fun onInputDeviceAdded(deviceId: Int) = updateGamePads()
    override fun onInputDeviceChanged(deviceId: Int) = updateGamePads()
    override fun onInputDeviceRemoved(deviceId: Int) = updateGamePads()

    private fun updateGamePads() {
        Log.v(TAG, "updateGamePads: ${mInputManager.inputDeviceIds.size}")
        mGamePads = mInputManager.inputDeviceIds
                .map { mInputManager.getInputDevice(it) }
                .filter { isSource(it.sources, SOURCE_GAMEPAD) || isSource(it.sources, SOURCE_JOYSTICK) }
                .map { it.id to GamePad(it, this::onClicked) }
                .toMap()
                .toSortedMap()
        gampadlisteners.forEach { it.gamepadUpdate() }
    }

    private fun onClicked(deviceId: Int, key: Int) {
        Log.v(TAG, "onClicked: ${GamePad.sBtnNameMap[key]}")
    }

    interface Listener {
        fun update()
    }

    interface GamepadListener {
        fun gamepadUpdate()
    }

    companion object {
        private val TAG = GamePadManager::class.java.simpleName

        private inline fun isSource(sources: Int, source: Int) = source and sources == source
    }
}

class GamePad(private val mDevice: InputDevice, private val clicked: (Int, Int) -> Unit) {
    val mBtnStates = Array(BTN_COUNT) { false }
    val mAxisStates = Array(AXIS_COUNT) { 0f }

    fun handleEvent(event: InputEvent): Boolean {
        dump()
        return when (event::class) {
            KeyEvent::class -> handleKeyEvent(event as KeyEvent)
            MotionEvent::class -> handleMotionEvent(event as MotionEvent)
            else -> false
        }
    }

    private fun handleKeyEvent(event: KeyEvent): Boolean {
        return sBtnCodeMap[event.keyCode]?.let {
            when (event.action) {
                ACTION_DOWN -> {
                    mBtnStates[it] = true
                    true
                }
                ACTION_UP -> {
                    mBtnStates[it] = false
                    takeIf { 200 > event.eventTime - event.downTime }?.apply { clicked(mDevice.id, it) }
                    true
                }
                else -> false
            }
        } ?: false
    }

    private fun handleMotionEvent(event: MotionEvent): Boolean {
        sAxisCodeMap.forEach { k, v -> mAxisStates[v] = getCenteredAxis(event, k) }
        return false
    }

    private fun getCenteredAxis(event: MotionEvent, axis: Int): Float {
        val range = event.device.getMotionRange(axis, event.source)
        if (range != null) {
            val flat = range.flat
            val value = event.getAxisValue(axis)
            if (Math.abs(value) > flat)
                return value
        }
        return 0f
    }

    private fun dump() {
        Log.v(TAG, "------${mDevice.name}")
        Log.v(TAG, mAxisStates.mapIndexed { i, v -> "${sAxisNameMap[i]}:$v" }.joinToString())
        Log.v(TAG, mBtnStates.mapIndexed { i, v -> "${sBtnNameMap[i]}:${if(v) "t" else "f"}" }.joinToString())
    }

    companion object {
        private val TAG = GamePad::class.java.simpleName

        // button
        const val BTN_A = 0
        const val BTN_B = 1
        const val BTN_X = 2
        const val BTN_Y = 3
        const val BTN_UP = 4
        const val BTN_DOWN = 5
        const val BTN_LEFT = 6
        const val BTN_RIGHT = 7
        const val BTN_L1 = 8
        const val BTN_L2 = 9
        const val BTN_R1 = 10
        const val BTN_R2 = 11
        const val BTN_SELECT = 12
        const val BTN_START = 13
        const val BTN_THUMBL = 14
        const val BTN_THUMBR = 15
        const val BTN_COUNT = 16

        // axis
        const val AXIS_HAT_X = 0
        const val AXIS_HAT_Y = 1
        const val AXIS_X = 2
        const val AXIS_Y = 3
        const val AXIS_Z = 4
        const val AXIS_RZ = 5
        const val AXIS_RTRIGGER = 6
        const val AXIS_LTRIGGER = 7
        const val AXIS_THROTTLE = 8
        const val AXIS_BRAKE = 9
        const val AXIS_COUNT = 10

        val sBtnCodeMap = mapOf(
                Pair(KEYCODE_BUTTON_A, BTN_A),
                Pair(KEYCODE_BUTTON_B, BTN_B),
                Pair(KEYCODE_BUTTON_X, BTN_X),
                Pair(KEYCODE_BUTTON_Y, BTN_Y),
                Pair(KEYCODE_DPAD_UP, BTN_UP),
                Pair(KEYCODE_DPAD_DOWN, BTN_DOWN),
                Pair(KEYCODE_DPAD_LEFT, BTN_LEFT),
                Pair(KEYCODE_DPAD_RIGHT, BTN_RIGHT),
                Pair(KEYCODE_BUTTON_L1, BTN_L1),
                Pair(KEYCODE_BUTTON_L2, BTN_L2),
                Pair(KEYCODE_BUTTON_R1, BTN_R1),
                Pair(KEYCODE_BUTTON_R2, BTN_R2),
                Pair(KEYCODE_BUTTON_SELECT, BTN_SELECT),
                Pair(KEYCODE_BUTTON_START, BTN_START),
                Pair(KEYCODE_BUTTON_THUMBL, BTN_THUMBL),
                Pair(KEYCODE_BUTTON_THUMBR, BTN_THUMBR))

        val sAxisCodeMap = mapOf(
                Pair(MotionEvent.AXIS_HAT_X, AXIS_HAT_X),
                Pair(MotionEvent.AXIS_HAT_Y, AXIS_HAT_Y),
                Pair(MotionEvent.AXIS_X, AXIS_X),
                Pair(MotionEvent.AXIS_Y, AXIS_Y),
                Pair(MotionEvent.AXIS_Z, AXIS_Z),
                Pair(MotionEvent.AXIS_RZ, AXIS_RZ),
                Pair(MotionEvent.AXIS_RTRIGGER, AXIS_RTRIGGER),
                Pair(MotionEvent.AXIS_LTRIGGER, AXIS_LTRIGGER),
                Pair(MotionEvent.AXIS_THROTTLE, AXIS_THROTTLE),
                Pair(MotionEvent.AXIS_BRAKE, AXIS_BRAKE))

        val sBtnNameMap = mapOf(
                Pair(BTN_A, "BTN_A"),
                Pair(BTN_B, "BTN_B"),
                Pair(BTN_X, "BTN_X"),
                Pair(BTN_Y, "BTN_Y"),
                Pair(BTN_UP, "BTN_UP"),
                Pair(BTN_DOWN, "BTN_DOWN"),
                Pair(BTN_LEFT, "BTN_LEFT"),
                Pair(BTN_RIGHT, "BTN_RIGHT"),
                Pair(BTN_L1, "BTN_L1"),
                Pair(BTN_L2, "BTN_L2"),
                Pair(BTN_R1, "BTN_R1"),
                Pair(BTN_R2, "BTN_R2"),
                Pair(BTN_SELECT, "BTN_SELECT"),
                Pair(BTN_START, "BTN_START"),
                Pair(BTN_THUMBL, "BTN_THUMBL"),
                Pair(BTN_THUMBR, "BTN_THUMBR"),
                Pair(BTN_COUNT, "BTN_COUNT"))

        val sAxisNameMap = mapOf(
                Pair(AXIS_HAT_X, "AXIS_HAT_X"),
                Pair(AXIS_HAT_Y, "AXIS_HAT_Y"),
                Pair(AXIS_X, "AXIS_X"),
                Pair(AXIS_Y, "AXIS_Y"),
                Pair(AXIS_Z, "AXIS_Z"),
                Pair(AXIS_RZ, "AXIS_RZ"),
                Pair(AXIS_LTRIGGER, "AXIS_LTRIGGER"),
                Pair(AXIS_RTRIGGER, "AXIS_RTRIGGER"),
                Pair(AXIS_THROTTLE, "AXIS_THROTTLE"),
                Pair(AXIS_BRAKE, "AXIS_BRAKE"))

        fun getSourcesDesc(sources: Int): String {
            return sSources.filter { it and sources == it }.joinToString {
                sSourceNames[it] ?: "unknow"
            }
        }

        private val sSources = listOf(
                SOURCE_DPAD, SOURCE_GAMEPAD, SOURCE_HDMI, SOURCE_JOYSTICK, SOURCE_KEYBOARD,
                SOURCE_MOUSE, SOURCE_MOUSE_RELATIVE, SOURCE_ROTARY_ENCODER, SOURCE_STYLUS,
                SOURCE_TOUCHPAD, SOURCE_TOUCHSCREEN, SOURCE_TOUCH_NAVIGATION, SOURCE_TRACKBALL)

        private val sSourceNames = mapOf(
                Pair(SOURCE_DPAD, "DPAD"),
                Pair(SOURCE_GAMEPAD, "GAMEPAD"),
                Pair(SOURCE_HDMI, "HDMI"),
                Pair(SOURCE_JOYSTICK, "JOYSTICK"),
                Pair(SOURCE_KEYBOARD, "KEYBOARD"),
                Pair(SOURCE_MOUSE, "MOUSE"),
                Pair(SOURCE_MOUSE_RELATIVE, "RELATIVE"),
                Pair(SOURCE_ROTARY_ENCODER, "ENCODER"),
                Pair(SOURCE_STYLUS, "STYLUS"),
                Pair(SOURCE_TOUCHPAD, "TOUCHPAD"),
                Pair(SOURCE_TOUCHSCREEN, "TOUCHSCREEN"),
                Pair(SOURCE_TOUCH_NAVIGATION, "TOUCH_NAVIGATION"),
                Pair(SOURCE_TRACKBALL, "TRACKBALL"))
    }
}
