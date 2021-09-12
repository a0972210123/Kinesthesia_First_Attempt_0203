package com.example.kinesthesia_first_attempt

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.actionToString
import android.view.View
import android.annotation.SuppressLint as SuppressLint1

class TouchBoard (context: Context, attrs: AttributeSet) : View(context, attrs) {

    var isPenInAir:Boolean = true
    var result:Boolean = true
    var defaultInAirPressure:Float = -1f

    @SuppressLint1("ClickableViewAccessibility", "SetTextI18n")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //val currentTimestamp = System.currentTimeMillis()
        bb = event.getAxisValue(MotionEvent.AXIS_SIZE)
        b1 = event.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR)
        b2 = event.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR)
        isPenInAir = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPenInAir = false
                startX = event.x
                startY = event.y
                //invalidate() //重新整理整個view
                updateParams(event,defaultInAirPressure,isPenInAir)
                result = true
            }

            MotionEvent.ACTION_MOVE -> {
                isPenInAir = false
                startX = event.x
                startY = event.y
                //invalidate()
                updateParams(event,defaultInAirPressure,isPenInAir)
                result = false
            }

            MotionEvent.ACTION_UP -> {
                isPenInAir = true
                //startY = 0f
                //startX = 0f
                //invalidate()
                updateParams(event,defaultInAirPressure,isPenInAir)
                result = true
            }
        }
        return  result
    }

/*
////多點觸控TEST
        var action = MotionEventCompat.getActionMasked(event);
        // Get the index of the pointer associated with the action.
        val index = MotionEventCompat.getActionIndex(event);
        var xPos = -1;
        var yPos = -1;
        Log.d( "DEBUG_TAG","The action is " + actionToString(action));
        if (event.getPointerCount() > 1) {
            Log.d("DEBUG_TAG","Multitouch event");
            // The coordinates of the current screen contact, relative to
            // the responding View or Activity.
            xPos = MotionEventCompat.getX(event, index).toInt()
            yPos = MotionEventCompat.getY(event, index).toInt()

        } else {
            // Single touch event
            Log.d("DEBUG_TAG","Single touch event");
            xPos = MotionEventCompat.getX(event, index).toInt()
            yPos = MotionEventCompat.getY(event, index).toInt()
        }
        checkAction(action)
//// 多點觸控TEST

 */


    override fun onHoverEvent(event: MotionEvent): Boolean {
        isPenInAir = true
        updateParams(event,defaultInAirPressure,isPenInAir)
        return true
    }




// Given an action int, returns a string description
    fun checkAction(action:Int):String{
        val checkingAction = actionToString(action)
        when (checkingAction) {
            MotionEvent.ACTION_DOWN.toString() -> {
                return "Down"
            }
            MotionEvent.ACTION_MOVE.toString() -> {
                return "Move"
            }
            MotionEvent.ACTION_POINTER_DOWN.toString() -> {
                return "Pointer Down"
            }
            MotionEvent.ACTION_UP.toString() -> {
                return "Up"
            }
            MotionEvent.ACTION_POINTER_UP.toString() -> {
                return "Pointer Up"
            }
            MotionEvent.ACTION_OUTSIDE.toString() -> {
                return "Outside"
            }
            MotionEvent.ACTION_CANCEL.toString() -> {
                return "Cancel"
            }
        }
        return ""
    }
    /////判斷動作TEST


    // 預計放在每一動作更新的地方
    fun updateParams(event: MotionEvent,defaultInAirPressure:Float,inAirFlag:Boolean){
        systemTimestamp = System.currentTimeMillis()
        startX = event.x
        startY = event.y
        if(inAirFlag){
            tipPressure = defaultInAirPressure
        } else{
            tipPressure = event.pressure
        }
        heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)  //Z值 (無單位 超過1cm抓不到
        //arangeInAir() //測試是否能更新全域stringbuffer
    }

    //可能移出到fragment 的overideInair 才能判斷哪一次按紐/哪一種情境
    fun arrangeInAir(){
        inAirData.append(systemTimestamp)
        inAirData.append(",")
        inAirData.append(startX)
        inAirData.append(",")
        inAirData.append(startY)
        inAirData.append(",")
        inAirData.append(heightZ)
        inAirData.append(",")
        inAirData.append(tipPressure)
        inAirData.append("\r\n")
    }


  /*  //參考CODE
    private val kData = StringBuffer()
    private fun getKdata(event: MotionEvent, tipPressure:Float){
        val currentTimestamp = System.currentTimeMillis()
        val paperX = event.x
        val paperY = event.y
        val tipPressure = event.pressure
        val bb = event.getAxisValue(MotionEvent.AXIS_DISTANCE)  //Z值 (無單位 超過1cm抓不到
        kData.append(currentTimestamp)
        kData.append(",")
        kData.append(paperX)
        kData.append(",")
        kData.append(paperY)
        kData.append(",")
        kData.append(tipPressure)
        kData.append(",")
        kData.append(bb)
        kData.append("\r\n")
        //Log.d("X/Y/壓力/距離測試onTouch：", "$currentTimestamp  秒  $paperX  $paperY  $tipPressure  $bb")
    }


    override fun onHoverEvent(event: MotionEvent): Boolean {
        getKdata(event, -1f)    //在空中時的壓力要是-1
        return true
    }

    */

}


