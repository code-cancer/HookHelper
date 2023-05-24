package com.code.cancer.hook.window

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

@SuppressLint("ClickableViewAccessibility")
class WindowViewTouchListener(
    private val wl: WindowManager.LayoutParams,
    private val windowManager: WindowManager
) : View.OnTouchListener {

    //TODO: 0 <= x <= phoneWidth
    private var x = 0
    private var y = 0

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x = rawX
                y = rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val movedX = rawX - x
                val movedY = rawY - y
                x = rawX
                y = rawY
                wl.x += movedX
                wl.y += movedY
                Log.d("DebugTest", "onTouch: ($x, $y), wl: (${wl.x}, ${wl.y})")
                windowManager.updateViewLayout(v, wl)
            }
        }
        return false
    }
}