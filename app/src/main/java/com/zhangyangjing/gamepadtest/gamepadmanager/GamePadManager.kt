package com.zhangyangjing.gamepadtest.gamepadmanager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.view.InputDeviceCompat.SOURCE_GAMEPAD
import android.support.v4.view.InputDeviceCompat.SOURCE_JOYSTICK
import android.view.InputEvent
import android.view.MotionEvent
import com.zhangyangjing.gamepadtest.gamepadmanager.inputmanagercompat.InputManagerCompat
import java.util.*

/**
 * Created by zhangyangjing on 2018/8/4.
 */
class GamePadManager(mCtx: Context) : InputManagerCompat.InputDeviceListener {
    var enableKeyEventIntercept = true
    var enableDpadTransform = true
        set(value) {
            field = value
            gamePads.forEach { _, gamePad ->  gamePad.enableDpadTransform = value}
        }

    var gamePads: SortedMap<Int, GamePad> = sortedMapOf()
    private val mInputManager = InputManagerCompat.Factory.getInputManager(mCtx)!!
    private val mGamPadListeners = LinkedList<IListener>()

    init {
        ensureGamePads()
    }

    fun resume() {
        mInputManager.registerInputDeviceListener(this, Handler(Looper.getMainLooper()))
        mInputManager.onResume()
        ensureGamePads()
    }

    fun pause() {
        mInputManager.unregisterInputDeviceListener(this)
        mInputManager.onPause()
    }

    fun handleEvent(event: InputEvent): Boolean {
        mGamPadListeners.forEach { it.gamePadEvent(event) }
        if (event is MotionEvent) mInputManager.onGenericMotionEvent(event)
        return (gamePads[event.deviceId]?.let { it.handleEvent(event) } ?: false) && enableKeyEventIntercept
    }

    fun addGamePadListener(listener: IListener) {
        mGamPadListeners.add(listener)
    }

    fun removeGamePadListener(listener: IListener) {
        mGamPadListeners.remove(listener)
    }

    override fun onInputDeviceAdded(deviceId: Int) {
        mInputManager.getInputDevice(deviceId)
                ?.takeIf { isSource(it.sources, SOURCE_GAMEPAD) || isSource(it.sources, SOURCE_JOYSTICK) }
                ?.let {
                    val gamePad = GamePad(it, enableDpadTransform)
                    gamePads[deviceId] = gamePad
                    mGamPadListeners.forEach { it.gamePadAdded(gamePad) }
                }
    }

    override fun onInputDeviceChanged(deviceId: Int) {
        gamePads[deviceId]?.let {
            mGamPadListeners.forEach {
                item -> item.gamePadChanged(it)
            }
        }
    }

    override fun onInputDeviceRemoved(deviceId: Int) {
        gamePads[deviceId]?.let {
            gamePads.remove(deviceId)
            mGamPadListeners.forEach {
                item -> item.gamePadRemoved(it)
            }
        }
    }

    private fun ensureGamePads() {
        val inputDeviceIds = mInputManager.inputDeviceIds.toList()
        gamePads.keys.minus(inputDeviceIds).forEach { onInputDeviceRemoved(it) }
        inputDeviceIds.minus(gamePads.keys).forEach { onInputDeviceAdded(it) }
    }

    interface IListener {
        fun gamePadEvent(event: InputEvent)
        fun gamePadAdded(gamePad: GamePad)
        fun gamePadChanged(gamePad: GamePad)
        fun gamePadRemoved(gamePad: GamePad)
    }

    class Listener : IListener {
        override fun gamePadEvent(event: InputEvent) {}
        override fun gamePadAdded(gamePad: GamePad) {}
        override fun gamePadChanged(gamePad: GamePad) {}
        override fun gamePadRemoved(gamePad: GamePad) {}
    }

    companion object {
        private val TAG = GamePadManager::class.java.simpleName

        private inline fun isSource(sources: Int, source: Int) = source and sources == source
    }
}
