package com.example.kinesthesia_first_attempt

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import android.annotation.SuppressLint as SuppressLint1


class TouchBoard (context: Context, attrs: AttributeSet) : View(context, attrs) {
    var sharedViewModel = MainViewModel()




    // val kData = StringBuffer()

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

        }

        return true
    }


/*
    fun sendkData() {
        val currentTimestamp = System.currentTimeMillis()
        val fileW = File(filePathStr, "$currentTimestamp.csv")
        val os = FileOutputStream(fileW, true)
        val tmp = StringBuffer()

        os.write(kData.toString().toByteArray())
        os.flush()
        os.close()
        tmp.setLength(0) //clean buffer
    }

 */



}

