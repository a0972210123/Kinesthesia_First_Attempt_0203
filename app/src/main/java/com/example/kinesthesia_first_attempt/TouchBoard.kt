package com.example.kinesthesia_first_attempt

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.actionToString
import android.view.View
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import java.io.File
import java.io.FileOutputStream
import android.annotation.SuppressLint as SuppressLint1


var onBeepTime: Long = 0
var onUpTime: Long = 0
var onDownTime: Long = 0
var onLongPressTime: Long = 0
var reactionTime: Long = 0
var movementTime: Long = 0

fun resetGestureTime() {
    onBeepTime = 0
    onUpTime = 0
    onDownTime = 0
    onLongPressTime = 0
    reactionTime = 0
    movementTime = 0
    GestureTimeData.setLength(0)
}

fun printGestureTime(){
    reactionTime = onUpTime - onBeepTime
    movementTime = onLongPressTime - onUpTime
    Log.d("GestureTime", " Go Signal: $onBeepTime,\n" +
            "    Action Start: $onUpTime,\n" +
            "    Board Contact: $onDownTime,\n" +
            "    Action End: $onLongPressTime,\n" +
            "    Reaction Time: $reactionTime,\n" +
            "    Movement Time: $movementTime,")
}

class TouchBoard (context: Context, attrs: AttributeSet) : View(context, attrs){

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
                        if (buttonPressedCountsInATrial ==3){
                            onUpTime = System.currentTimeMillis()
                            u_arrangeInAirData()
                        }
                    }
                    1->{
                        //onDown
                        tipPressure = 1f
                        heightZ = 0f
                        if (buttonPressedCountsInATrial ==3){
                            onDownTime = System.currentTimeMillis()
                            u_arrangeInAirData()
                        }
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
                        tipPressure = 10f
                        heightZ = -1f
                        if (buttonPressedCountsInATrial ==3){
                            onLongPressTime = System.currentTimeMillis()
                            u_arrangeInAirData()
                        }
                    }

                }
            }
            "Pen" ->{
                when(GestureFlag){
                    0->{
                        //onUp
                        tipPressure = defaultInAirPressure //-1f
                        heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)
                        if (buttonPressedCountsInATrial ==3){
                            //onUpTime = System.currentTimeMillis()
                            onUpTime = SystemClock.uptimeMillis()
                            u_arrangeInAirData()
                        }
                    }
                    1->{
                        //onDown
                        tipPressure = event.pressure
                        heightZ = event.getAxisValue(MotionEvent.AXIS_DISTANCE)
                        if (buttonPressedCountsInATrial ==3){
                            //onDownTime = System.currentTimeMillis()
                            onDownTime = SystemClock.uptimeMillis()
                            u_arrangeInAirData()
                        }
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
                        tipPressure = 10f
                        heightZ = -1f
                        if (buttonPressedCountsInATrial ==3){
                            //onLongPressTime = System.currentTimeMillis()
                            onLongPressTime = SystemClock.uptimeMillis()
                            u_arrangeInAirData()
                        }
                    }

                }
            }
        }
        u_changeInAriText()
        Log.d("data", "X:$startX , Y:$startY, Z:$heightZ, Pressure:$tipPressure, Time:$systemTimestamp")
    }

    var isPenInAir:Boolean = true
    var result:Boolean = true
    var defaultInAirPressure:Float = -1f
    var gestureFlag:Int = 0
    var testGestureDetector: GestureDetectorCompat = GestureDetectorCompat(mContextKIN, MyGestureDetectorCompat())

    override fun onHoverEvent(event: MotionEvent): Boolean {
        isPenInAir = true
        gestureFlag = 3
        updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
        Log.d("gestureData", "onHover")
        return false
    }

    @SuppressLint1("ClickableViewAccessibility", "SetTextI18n")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        bb = event.getAxisValue(MotionEvent.AXIS_SIZE)
        b1 = event.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR)
        b2 = event.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR)
        isPenInAir = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                testOndown_ontouch = System.currentTimeMillis()
                isPenInAir = false
                gestureFlag = 1
                updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
                result = true
                //this have to be true for the app to keep recognizing gesture
                Log.d("gestureData", "onDown_onTouchEvent")
            }

            MotionEvent.ACTION_MOVE -> {
                isPenInAir = false
                gestureFlag =2
                updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
                result = true //whether this is true or false, it will be triggered and update data
                // when ACTION_MOVE return true, it blocks the gesture detector, this is not the desired result
                // while ACTION_MOVE return true, it can still detect longpress (without scrolling)

                Log.d("gestureData", "onMove")
            }

            MotionEvent.ACTION_UP -> {
                isPenInAir = true
                gestureFlag = 0
                updateParams(event,defaultInAirPressure,isPenInAir,gestureFlag)
                result = true
                // when ACTION_UP return true, it blocks the gesture detector. The result is fine.
                Log.d("gestureData", "onUp")
            }
        }


        Log.d("gestureData", "The Action is "+ checkAction(event.action))
        Log.d("gestureData", "The onTouchEvent result is $result")

        when(  testGestureDetector.onTouchEvent(event)){
            true ->{
                Log.d("gestureData11", "testGestureDetector,return true")
            }
            false ->{
                Log.d("gestureData11", "testGestureDetector,return false")
            }
        }
        //return super.onTouchEvent(event)
        return result
    }

}

var testOndown_ontouch:Long = 0
var testOndown_gesturedetector:Long = 0
var testOnLongPress:Long = 0


//TODO: check this website
// https://stackoverflow.com/questions/51704307/continue-onscroll-event-after-onlongpress
// 1. 嘗試解 longpresss vs scroll 的衝突
// 2. 寫自訂 longpress 用data smoothing概念


class MyGestureDetectorCompat : GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {
    //https://www.itread01.com/content/1549194854.html
    //https://developer.android.com/training/gestures/detector
    //https://developer.android.com/jetpack/compose/gestures
    private val SWIPE_THRESHOLD: Int = 300
    private val SWIPE_VELOCITY_THRESHOLD = 300
    var scrollThresholdValue_x = 0f
    var scrollThresholdValue_y = 0f

    override fun onDown(e: MotionEvent?): Boolean {
        testOndown_gesturedetector = System.currentTimeMillis()
        //onDownTime = SystemClock.currentThreadTimeMillis()
        //startX = e!!.x
        //startY = e!!.y
        Log.d("gestureData", "onDown_GestureDetector")
        return false
        //when this is true, the app can detect long press
        //when this is false, the app can still detect long press
        //but both setting cannot detected long press after large movement
    }

    override fun onShowPress(e: MotionEvent) {
        Log.d("gestureData", "onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.d("gestureData", "It is a SingleTapUp")
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
//        var scrollThresholdValue_x = e2!!.x - e1!!.x
//        var scrollThresholdValue_y = e2!!.y - e1!!.y
//        Log.d("gestureDataScroll", "onScroll: Scroll Threshold Value X=$scrollThresholdValue_x Y=$scrollThresholdValue_y")
//        Log.d("gestureDataScroll", "onScroll: Scroll parameters X=$distanceX Y=$distanceY")
        Log.d("gestureData", "It is a Scroll")
        //測試發現，短時間內，大概20px左右的位移，就會觸發Scroll
        //return true
        // 不論true/false都不會影響 longpress
        // 而是單一一個touchEvent中，觸發scroll後就無法觸發longpress

        //TODO: 嘗試在這邊 call longpress
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        // Android 平板 > 設定 > 協助工具/無障礙設定 > 互動與敏銳度 > 輕觸並按住的延遲時間 (暫時設定為 1秒，可以設為 0.5 1.0 1.5)
        testOnLongPress = System.currentTimeMillis()
        val interval_ontouch =  testOnLongPress - testOndown_ontouch
        val interval_gesturedetector = testOnLongPress - testOndown_gesturedetector
        Log.d("gestureData", "onLongPress Interval, From onTouch: $interval_ontouch ms,  From gesturedetector: $interval_gesturedetector ms, ")
        testOndown_ontouch = 0
        testOndown_gesturedetector =0
        testOnLongPress =0
        // 以上測試發現 longpress偵測時間，目前抓出來為 987 - 996 ms 約為1SEC，onTouchListener會比gesturedetector快1-10ms

        ///////////////////////////////
        touchBoard.isPenInAir = false
        touchBoard.gestureFlag = 4
        touchBoard.updateParams(e,touchBoard.defaultInAirPressure,touchBoard.isPenInAir,touchBoard.gestureFlag)
        Log.d("gestureData", "X:$startX , Y:$startY, Z:$heightZ, Pressure:$tipPressure, Time:$systemTimestamp")
        Log.d("gestureData", "It is a Long Press")
        Toast.makeText(mContextKIN, "偵測到長按", Toast.LENGTH_SHORT).show()
        // onLongPress 可以在少量move情況下偵測到，但當大量移動後在停下就無法偵測
        // onLongPress 會吃掉event，在這之後gesture無法辨識，只能判斷move/up，無法判斷 fling&scroll
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
        Log.d("gestureData", " onFling Confirm")
        return result
        // whether true or false, this will not affect long press
    }

    fun onSwipeRight() {
        Log.d("gestureData", " onFling: Right")
    }

    fun onSwipeLeft() {
        Log.d("gestureData", " onFling: Left")
    }

    fun onSwipeTop() {
        Log.d("gestureData", " onFling: Top")
    }

    fun onSwipeBottom() {
        Log.d("gestureData", " onFling: Bottom")
    }

    override fun onDoubleTap(event: MotionEvent?): Boolean {
        Log.d("gestureData", "It is a Double Tap")
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent?): Boolean {
        Log.d("gestureData", "It is a Double Tap")
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent?): Boolean {
        Log.d("gestureData", "It is a Double Tap Event")
        return true
    }
}


// 儲存 MT RT

var GestureTimeData = StringBuffer()

fun saveGestureTimeToCSV() {
    GestureTimeData.append(onBeepTime)
    GestureTimeData.append(",")
    GestureTimeData.append(onUpTime)
    GestureTimeData.append(",")
    GestureTimeData.append(onDownTime)
    GestureTimeData.append(",")
    GestureTimeData.append(onLongPressTime)
    GestureTimeData.append(",")
    GestureTimeData.append(reactionTime)
    GestureTimeData.append(",")
    GestureTimeData.append(movementTime)
    GestureTimeData.append(",")
    GestureTimeData.append("\r\n")

    //切割buffer
    val outputGestureTimeData = GestureTimeData.toString().replace("\r", "").split("\n")

    //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
    var outputFileName = "Dominant_" + testCondition + "_" + stimuliType + "_" + currentTestContext + "_" + currentTestDirection + "_GestureTime_Trial_" + currentTrial.toString()+ ".csv"
    // 存檔: name,List,flag
    outputGestureTimeCsv(outputFileName, outputGestureTimeData, 0)
}   //存檔要改成Direction名稱

fun outputGestureTimeCsv(fileName: String, input: List<String>, flag: Int) {
    //檔案路徑: 目前直接讀在demographic的全域變數，有error再讀viewModel備用
    //val outputPath = binding.viewModel!!.outputFilePath.toString()  //讀取存在viewModel的路徑
    val output = StringBuffer()//必需

    val titleList = listOf(
        "Go Signal",
        "Action Start",
        "Board Contact",
        "Action End",
        "Reaction Time",
        "Movement Time"
    )

    //合併title
    val allTitle: MutableList<String> = mutableListOf()
    allTitle.addAll(titleList)

    // 放入標題，使用迴圈，避免前後出現[]
    for (i in 0..(allTitle.size - 1)) {
        output.append(allTitle[i])
        output.append(",")
    }
    output.append("\r\n")

    //輸入後 再次排列
    for (u in input) {  //這邊需要測試看outPut結果
        output.append(u)
        output.append("\r\n")
    }

    val file = File(filePathStr, fileName)
    val os = FileOutputStream(file, true)   // 這邊給的字串要有檔案類型
    os.write(output.toString().toByteArray())
    os.flush()
    os.close()
    output.setLength(0) //clean buffer
    Toast.makeText(
        mContextKIN,
        "$stimuliType x $testCondition x $currentTestContext x $currentTestDirection _GestureTime_Trial$currentTrial 儲存成功",
        Toast.LENGTH_SHORT
    ).show()
    //Log.d("data", "outCSV Success")
}  // sample from HW
//以上 CSV存檔相關























//以下無用CODE


// Given an action int, returns a string description
fun checkAction(action:Int):String{

    when (action) {
        MotionEvent.ACTION_DOWN -> {
            return "Down"
        }
        MotionEvent.ACTION_MOVE-> {
            return "Move"
        }
        MotionEvent.ACTION_POINTER_DOWN-> {
            return "Pointer Down"
        }
        MotionEvent.ACTION_UP-> {
            return "Up"
        }
        MotionEvent.ACTION_POINTER_UP -> {
            return "Pointer Up"
        }
        MotionEvent.ACTION_OUTSIDE -> {
            return "Outside"
        }
        MotionEvent.ACTION_CANCEL -> {
            return "Cancel"
        }
    }
    return ""
}
/////判斷動作TEST

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