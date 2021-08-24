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

                _testX.value = startX
                _testY.value = startY
                _testPosition.value = "Current Position: X= $startX  ,Y= $startY"

                //positionTask.text = "Current Position: X= $startX  ,Y= $startY"

                sharedViewModel.setCurrentPosition(startX ,startY)

                Log.d("X/Y/面積/長軸/短軸：onTouch", "$startX  $startY  $bb  $b1  $b2")

/*
                kData.append(currentTimestamp)
                kData.append(",")
                kData.append(startX)
                kData.append(",")
                kData.append(startY)
                kData.append(",")
                kData.append(bb)
                kData.append(",")
                kData.append(b1)
                kData.append(",")
                kData.append(b2)
                kData.append("\r\n")

 */
                invalidate() //重新整理整個view
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

