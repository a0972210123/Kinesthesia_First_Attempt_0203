package com.example.kinesthesia_first_attempt

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.actionToString
import android.view.View
import androidx.core.view.GestureDetectorCompat
import android.annotation.SuppressLint as SuppressLint1

class TouchBoard (context: Context, attrs: AttributeSet) : View(context, attrs){

    var isPenInAir:Boolean = true
    var result:Boolean = true
    var defaultInAirPressure:Float = -1f
    var gestureFlag:Int = 0

    var testGestureDetector: GestureDetectorCompat = GestureDetectorCompat(mContextKIN, MyGestureDetectorListener())


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
                gestureFlag = 1
                updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
                result = true
            }

            MotionEvent.ACTION_MOVE -> {
                isPenInAir = false
                gestureFlag =2
                updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
                result = false
            }

            MotionEvent.ACTION_UP -> {
                isPenInAir = true
                gestureFlag = 0
                updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
                result = true
            }
        }

        if (testGestureDetector.onTouchEvent(event)){
            Log.d("data", "gesture detect return true")
        }else{

        }

        //return  result
       // return result
        return result
    }

    override fun onHoverEvent(event: MotionEvent): Boolean {
        isPenInAir = true
        gestureFlag = 3
        updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
        return false
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
    fun updateParams(event: MotionEvent,defaultInAirPressure:Float,inAirFlag:Boolean,GestureFlag:Int){
        systemTimestamp = System.currentTimeMillis()
        // var onLongPressTime = SystemClock.currentThreadTimeMillis()

        startX = event.x
        startY = event.y
        //heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)  //Z值 (無單位 超過1cm抓不到

        when(currentTestContext){
            "Finger" ->{
                when(GestureFlag){
                    0->{
                        //onUp
                        tipPressure = -1f
                        heightZ = 1f
                    }
                    1->{
                        //onDown
                        tipPressure = 1f
                        heightZ = 0f
                    }
                    2->{
                        //onMove
                        tipPressure = 0.5f
                        heightZ = 0f
                    }
                    3->{
                        //Hover & Pen
                        //do nothing
                    }
                    4->{
                        //Long Press
                        tipPressure = 2f
                        heightZ = -1f
                    }
                    5->{
                        //Show Press
                        tipPressure = 2.5f
                        heightZ = -1f
                    }
                }
            }
            "Pen" ->{
                when(GestureFlag){
                    0->{
                        //onUp
                        tipPressure = defaultInAirPressure //-1f
                        heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)
                    }
                    1->{
                        //onDown
                        tipPressure = event.pressure
                        heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)
                    }
                    2->{
                        //onMove
                        tipPressure = event.pressure
                        heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)
                    }
                    3->{
                        //Hover & Pen
                        tipPressure = defaultInAirPressure //-1f
                        heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)

                    }
                    4->{
                        //Long Press
                        tipPressure = 10f
                        heightZ = -1f
                    }
                    5->{
                        //Show Press
                        tipPressure = 15f
                        heightZ = -1f
                    }
                }
            }
        }

        Log.d("data", "X:$startX , Y:$startY, Z:$heightZ, Pressure:$tipPressure, Time:$systemTimestamp")
    }


}


class MyGestureDetectorListener : GestureDetector.OnGestureListener {
    //https://www.itread01.com/content/1549194854.html
    //https://developer.android.com/training/gestures/detector
    //https://developer.android.com/jetpack/compose/gestures
    private val SWIPE_THRESHOLD: Int = 300
    private val SWIPE_VELOCITY_THRESHOLD = 300

    var scrollThresholdValue_x = 0f
    var scrollThresholdValue_y = 0f
    var onDownTime: Long = 0
    var onLongPressTime: Long = 0
    var onScrollTime: Long = 0
    var interval: Long = 0

    var isLongPressed: Boolean = false

    fun resetTime() {
        onDownTime = 0
        onLongPressTime = 0
        interval = 0
        scrollThresholdValue_x = 0f
        scrollThresholdValue_y = 0f
        onScrollTime = 0
    }

    override fun onDown(e: MotionEvent?): Boolean {
        //onDownTime = SystemClock.currentThreadTimeMillis()
        //startX = e!!.x
        //startY = e!!.y
        Log.d("Gesture", "onDown")
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
        //startX = e!!.x
        //startY = e!!.y
        touchBoard.isPenInAir = false
        touchBoard.gestureFlag = 5
        if (e != null) {
            touchBoard.updateParams(e,touchBoard.defaultInAirPressure,touchBoard.isPenInAir,touchBoard.gestureFlag)
        }
        Log.d("data", "X:$startX , Y:$startY, Z:$heightZ, Pressure:$tipPressure, Time:$systemTimestamp")
        Log.d("data", "It is a Show Press")
        Log.d("Gesture", "onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        startX = 0f
        startY = 0f
        Log.d("Gesture", "onSingleTapUp")
        Log.d("data", "It is a SingleTapUp")
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        //onScrollTime = SystemClock.currentThreadTimeMillis()
        //interval = onScrollTime - onDownTime
        //Log.d("Gesture", "onScroll: interval = $interval ms")
        //var scrollThresholdValue_x = e2!!.x - e1!!.x
        //var scrollThresholdValue_y = e2!!.y - e1!!.y
        //Log.d("Gesture", "onScroll: Scroll Threshold Value X=$scrollThresholdValue_x Y=$scrollThresholdValue_y")
        //Log.d("Gesture", "onScroll: Scroll parameters X=$distanceX Y=$distanceY")
        //Log.d("data", "It is a Scroll")
        //resetTime()
        //return false
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        // Android 平板 > 設定 > 協助工具/無障礙設定 > 互動與敏銳度 > 輕觸並按住的延遲時間 (暫時設定為 1秒，可以設為 0.5 0 1.5)
        //onLongPressTime = SystemClock.currentThreadTimeMillis()
        //interval = onLongPressTime - onDownTime
        Log.d("dataGesture", "onLongPress: interval = $interval ms")
        //resetTime()

        touchBoard.isPenInAir = false
        touchBoard.gestureFlag = 4
        if (e != null) {
            touchBoard.updateParams(e,touchBoard.defaultInAirPressure,touchBoard.isPenInAir,touchBoard.gestureFlag)
        }
        Log.d("data", "X:$startX , Y:$startY, Z:$heightZ, Pressure:$tipPressure, Time:$systemTimestamp")
        Log.d("data", "It is a Long Press")
        //測試結果: GestureDetector 本身 onLongPress 的時間設定為 10 - 40ms之間，並不穩定，且時長短
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        var result: Boolean = false

        try {
            var diffY = e2!!.y - e1!!.y
            var diffX = e2!!.x - e1!!.x

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }

            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }

        } catch (exception: Exception) {
            exception.printStackTrace();
        }
        Log.d("Gesture", " onFling Confirm")
        //return result //return true
        return false
    }

    fun onSwipeRight() {
        Log.d("Gesture", " onFling: Right")
    }

    fun onSwipeLeft() {
        Log.d("Gesture", " onFling: Left")
    }

    fun onSwipeTop() {
        Log.d("Gesture", " onFling: Top")
    }

    fun onSwipeBottom() {
        Log.d("Gesture", " onFling: Bottom")
    }

}




class MyOnDoubleTapListener : GestureDetector.OnDoubleTapListener {

    override fun onDoubleTap(event: MotionEvent?): Boolean {
        Log.d("Gesture", "onDoubleTap: $event")
        Log.d("data", "It is a Double Tap")
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent?): Boolean {
        Log.d("Gesture", "onDoubleTapEvent: $event")
        Log.d("data", "It is a Double Tap")
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent?): Boolean {
        Log.d("Gesture", "onSingleTapConfirmed: $event")
        Log.d("data", "It is a Double Tap Event")
        return true
    }

}


//以下無用CODE
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

    */

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