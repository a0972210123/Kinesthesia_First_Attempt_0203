package com.example.kinesthesia_first_attempt

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.annotation.SuppressLint as SuppressLint1

class TouchBoard (context: Context, attrs: AttributeSet) : View(context, attrs) {

    @SuppressLint1("ClickableViewAccessibility", "SetTextI18n")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val currentTimestamp = System.currentTimeMillis()
        bb = event.getAxisValue(MotionEvent.AXIS_SIZE)
        b1 = event.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR)
        b2 = event.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                Log.d("onTouch", "$startX  $startY  $bb  $b1  $b2")
                invalidate() //重新整理整個view
            }

            MotionEvent.ACTION_MOVE -> {
                startX = event.x
                startY = event.y
                Log.d("onMove", "$startX  $startY  $bb  $b1  $b2")
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                startX = 0f
                startY = 0f
                Log.d("onLift", "$startX  $startY  $bb  $b1  $b2")
                invalidate()
            }
        }
        return true
    }

}

