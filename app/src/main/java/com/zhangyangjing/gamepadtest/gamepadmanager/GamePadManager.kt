package com.zhangyangjing.gamepadtest.gamepadmanager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.view.InputDeviceCompat.SOURCE_GAMEPAD
import android.support.v4.view.InputDeviceCompat.SOURCE_JOYSTICK
import android.util.Log
import android.view.InputEvent
import android.view.MotionEvent
import com.zhangyangjing.gamepadtest.gamepadmanager.inputmanagercompat.InputManagerCompat
import java.util.*

/**
 * Created by zhangyangjing on 2018/8/4.
 */
class GamePadManager(mCtx: Context) : InputManagerCompat.InputDeviceListener {
    var mGamePads: SortedMap<Int, GamePad>? = null
    private val mInputManager = InputManagerCompat.Factory.getInputManager(mCtx)!!
    private val mGamPadListeners = LinkedList<GamePadListener>()

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
        return mGamePads?.get(event.deviceId)?.let { it.handleEvent(event) } ?: return false
    }

    fun addGamePadListener(gamePadListener: GamePadListener) {
        mGamPadListeners.add(gamePadListener)
    }

    override fun onInputDeviceAdded(deviceId: Int) = updateGamePads()
    override fun onInputDeviceChanged(deviceId: Int) = updateGamePads()
    override fun onInputDeviceRemoved(deviceId: Int) = updateGamePads()

    private fun updateGamePads() {
        mGamePads = mInputManager.inputDeviceIds
                .map { mInputManager.getInputDevice(it) }
                .filter { isSource(it.sources, SOURCE_GAMEPAD) || isSource(it.sources, SOURCE_JOYSTICK) }
                .map { it.id to GamePad(it) }
                .toMap()
                .toSortedMap()
        mGamPadListeners.forEach { it.gamePadUpdate() }
    }

    interface GamePadListener {
        fun gamePadUpdate()
    }

    companion object {
        private val TAG = GamePadManager::class.java.simpleName

        private inline fun isSource(sources: Int, source: Int) = source and sources == source
    }
}
