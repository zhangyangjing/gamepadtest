package com.zhangyangjing.gamepadtest.gamepadmanager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.view.InputDeviceCompat.SOURCE_GAMEPAD
import android.support.v4.view.InputDeviceCompat.SOURCE_JOYSTICK
import android.util.Log
import android.view.InputEvent
import android.view.MotionEvent
import com.zhangyangjing.gamepadtest.inputmanagercompat.InputManagerCompat
import java.util.*

/**
 * Created by zhangyangjing on 2018/8/4.
 */
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
