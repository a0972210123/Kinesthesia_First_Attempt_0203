package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

class TouchBoard (context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private val kData = StringBuffer()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val currentTimestamp = System.currentTimeMillis()
        val bb = event.getAxisValue(MotionEvent.AXIS_SIZE)
        val b1 = event.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR)
        val b2 = event.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y

                val text1 = findViewById<TextView>(R.id.text1)
                val text3 = findViewById<TextView>(R.id.text3)

                if (text1.text.toString()=="第一次：-999") {
                    text1.text = "第一次：X= $startX ; Y= $startY"
                }
                text3.text = "最後一次：X= $startX ; Y= $startY"

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

                invalidate()
                Log.d("X/Y/面積/長軸/短軸：", "$startX  $startY  $bb  $b1  $b2")
            }
        }
        return true
    }




    // 解法嘗試
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val text1 = findViewById<TextView>(R.id.text1)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> text1.text = "hey"
            else -> {
            }
        }
        return dispatchTouchEvent(event)
    }




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

}

