package com.example.kinesthesia_first_attempt

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.actionToString
import android.view.View
import android.annotation.SuppressLint as SuppressLint1

class TouchBoard (context: Context, attrs: AttributeSet) : View(context, attrs) {




    @SuppressLint1("ClickableViewAccessibility", "SetTextI18n")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val currentTimestamp = System.currentTimeMillis()
        bb = event.getAxisValue(MotionEvent.AXIS_SIZE)
        b1 = event.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR)
        b2 = event.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR)

        var result:Boolean = true


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                //invalidate() //重新整理整個view
                result = true
            }

            MotionEvent.ACTION_MOVE -> {
                startX = event.x
                startY = event.y
                //invalidate()
                result = false
            }

            MotionEvent.ACTION_UP -> {
                startX = 0f
                startY = 0f
                invalidate()
                result = true
            }
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
        return  result
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



}


