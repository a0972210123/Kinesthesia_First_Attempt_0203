package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock.currentThreadTimeMillis
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentAutorecordBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class AutorecordFragment : Fragment() {
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentAutorecordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        //hideBottomUIMenu()
        val decorView = requireActivity().window.decorView
        u_hideSystemUI(decorView)
        testCondition =
            testConditionList[4] //
        u_setTrialLimit("5")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_autorecord, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            autorecordFragment =
                this@AutorecordFragment //使用listenser binding，用UI button 在xml中設定onclick
        }

        navControllerKIN = findNavController() //必須，用於從public function呼叫navControllerKIN

        //以下三行為 global function需要的input
        mainViewModel = sharedViewModel  // 這行是為了讓public可以讀到ViewModel 且不需要重新 initiate
        mActivityKIN = requireActivity()
        mContextKIN = requireActivity().applicationContext

        start = requireView().findViewById<TextView>(R.id.performance_start_position)
        test = requireView().findViewById<TextView>(R.id.performance_test_position)
        rest = requireView().findViewById<TextView>(R.id.performance_rest_position)
        response =
            requireView().findViewById<TextView>(R.id.performance_response_position) //測驗次數textView
        trialCountView = requireView().findViewById<TextView>(R.id.trial_count) //找到測驗按鈕
        recordingButton = requireView().findViewById<Button>(R.id.record_position) //找到指導語textView
        instructionText = requireView().findViewById<TextView>(R.id.instruction_demonstration)


        // 11/15調整，Context & Spinner宣告位置
        com.example.kinesthesia_first_attempt.trialInputSpinner =
            requireView().findViewById<View>(R.id.trialInput_list) as Spinner
        com.example.kinesthesia_first_attempt.contextSpinner =
            requireView().findViewById<View>(R.id.context_list) as Spinner
        com.example.kinesthesia_first_attempt.directionSpinner =
            requireView().findViewById<View>(R.id.direction_list) as Spinner
        stimuliTypeSpinner = requireView().findViewById<View>(R.id.stimuliType_list) as Spinner


        fingerTarget = requireView().findViewById<ImageView>(R.id.target)
        fingerStartPoint = requireView().findViewById<ImageView>(R.id.start_point)
        fingerDownArrow = requireView().findViewById<ImageView>(R.id.down_arrow)
        penTarget = requireView().findViewById<ImageView>(R.id.pen_target)
        penStartPoint = requireView().findViewById<ImageView>(R.id.pen_start_point)
        penDownArrow = requireView().findViewById<ImageView>(R.id.pen_down_arrow)

        //此段重要，為測驗方向和目標的View宣告，正式測驗中，需要新增斜向箭頭
        penUpArrow = requireView().findViewById<ImageView>(R.id.pen_up_arrow)
        penUpLeftArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_up_left)
        penUpRightArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_up_right)
        penDownLeftArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_down_left)
        penDownRightArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_down_right)

        fingerUpArrow = requireView().findViewById<ImageView>(R.id.up_arrow)
        fingerUpLeftArrow = requireView().findViewById<ImageView>(R.id.arrow_to_up_left)
        fingerUpRightArrow = requireView().findViewById<ImageView>(R.id.arrow_to_up_right)
        fingerDownLeftArrow = requireView().findViewById<ImageView>(R.id.arrow_to_down_left)
        fingerDownRightArrow = requireView().findViewById<ImageView>(R.id.arrow_to_down_right)
// 箭頭
        fingerRandomTargetView = requireView().findViewById<ImageView>(R.id.random_target)
        penRandomTargetView = requireView().findViewById<ImageView>(R.id.pen_random_target)

        countAndHint =
            requireView().findViewById<TextView>(R.id.text1)         //11/21 計時用 text view

        //11/21 manageVisibility
        selectButton = requireView().findViewById(R.id.confirm_trial) as Button
        currentPosition =
            requireView().findViewById<TextView>(R.id.current_position_field)  //11/17 優化
        inAirText = requireView().findViewById<TextView>(R.id.in_air_testing) //11/17 優化
        touchBoard = requireView().findViewById(R.id.view) as TouchBoard //11/17 優化
        Score =
            requireView().findViewById<TextView>(R.id.performance_current_trial_score) //11/17 優化

        //11/28 新增表現分數標題
        performanceTitle = requireView().findViewById<TextView>(R.id.performance_title)
        titleParams = performanceTitle.layoutParams as ViewGroup.MarginLayoutParams
        penTargetParams = penTarget.layoutParams as ViewGroup.MarginLayoutParams
        penStartParams = penStartPoint.layoutParams as ViewGroup.MarginLayoutParams
        fingerTargetParams = fingerTarget.layoutParams as ViewGroup.MarginLayoutParams
        fingerStartParams = fingerStartPoint.layoutParams as ViewGroup.MarginLayoutParams

        fingerRandomTargetParams =
            fingerRandomTargetView.layoutParams as ViewGroup.MarginLayoutParams
        penRandomTargetParams = penRandomTargetView.layoutParams as ViewGroup.MarginLayoutParams


        TargetArea = requireView().findViewById<ImageView>(R.id.target_square_white)
        TargetAreaFrame = requireView().findViewById<ImageView>(R.id.target_square_black)
        u_setSquareOfTargetArea()

        u_changeInAriText()     // DEFAULT inAir文字
        //* new 下筆時的紀錄
        touchBoard.setOnTouchListener { _, _ ->

            if (com.example.kinesthesia_first_attempt.buttonPressedCountsInATrial == 3 && com.example.kinesthesia_first_attempt.currentTestContext == "Pen") {
                u_arrangeInAirData()
            }
            u_changeInAriText()
            false
        }

        //* new 提筆時的紀錄
        touchBoard.setOnHoverListener { _, _ ->

            if (com.example.kinesthesia_first_attempt.buttonPressedCountsInATrial == 3 && com.example.kinesthesia_first_attempt.currentTestContext == "Pen") {
                u_arrangeInAirData()
            }
            u_changeInAriText()
            false
        }
        //* new


        //更新文字view
        u_changeText()
        u_displayScoreInText(
            com.example.kinesthesia_first_attempt.scoreListForDisplay,
            0,
            Score
        )  // flag = 0  顯示預設文字

        u_launchTrialInputSpinner()
        u_launchDirectionSpinner()
        u_launchContextSpinner()   //設定測驗情境(手指或握筆) > 必須
        u_launchStimuliTypeSpinner()


        u_checkContextAndLaunchView(com.example.kinesthesia_first_attempt.currentTestContext)

        // 確認人口學資料
        u_checkDemographicInputAndUpdateDefault(mActivityKIN, mContextKIN)


        trialInputSpinner.visibility = View.GONE
        contextSpinner.visibility = View.GONE
        directionSpinner.visibility = View.GONE
        stimuliTypeSpinner.visibility = View.GONE

        Log.d("lifeCycle", "AutoRecordFragment created!")
    }


} //Fragment End


//TODO: 新增 隨機測驗方向的 function，可以連續自動執行測驗
// step 1 在進入頁面，決定 刺激/情境/方向 的隨機順序 (要counter balanced)
// step 2 將這些順序 暫存在某個 List
// step 3 將這些 List 顯示在對話框 並依據測驗進度 標示 [] or [V]
// call setStimuli / setcontext / setdirection = 輸入目前的測驗參數，以啟動view


// 用 confirmSelection 自動執行目前選擇的結果 > 用目前已經寫好測驗限制判斷式(checktrailLimit等等)，依序判斷目前測驗進度
// 並在每次按下positive按鈕時，再次呼叫confirmSelection
// >> 或許寫一個新的對話框最快，不要共用之前的判斷對話框


var randomizedStimuliOrder = listOf<Int>(0, 1, 2)
var randomizedContextOrder = listOf<Int>(0, 1)
var randomizedDirectionOrder = listOf<Int>(0, 1, 2, 3)

fun executeAutoTestList() {
    //此函式 用於在確認進度後，輸入指定新測驗參數 開始下一輪測驗

    checkAutoTestProgress()  //確認完各種參數進度後 > 直接訂出目前要測的參數 > 開始正式測驗

    // 以下模仿 confirm selection
    val stimuliChecked = u_checkStimuliInput()
    val contextChecked = u_checkContextInput()
    val directionChecked = u_checkDirectionInput()

    if (stimuliChecked == 1) {
        if (contextChecked == 1) {
            if (directionChecked == 1) {
                u_randomThePosition()
                u_setTargetPosition()
                u_changeText()
                u_manageVisibility(0)  //顯示觸控板及記錄紐
                Toast.makeText(
                    mContextKIN,
                    "開始測驗，項目： $stimuliType & $currentTestContext & $currentTestDirection ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

fun resetAutoTestList() {
    randomizedStimuliOrder = listOf<Int>(0, 1, 2)
    randomizedContextOrder = listOf<Int>(0, 1)
    randomizedDirectionOrder = listOf<Int>(0, 1, 2, 3)
}

fun u_randomTheAutoTestList() {
    val stimuliOrder = listOf(0, 1, 2)
    val contextOrder = listOf(0, 1)
    val directionOrder = listOf(0, 1, 2, 3)
    randomizedStimuliOrder = stimuliOrder.shuffled()
    randomizedContextOrder = contextOrder.shuffled()
    randomizedDirectionOrder = directionOrder.shuffled()
}


//目前的近測驗進度
var stimuliProgress = 0   //0 1 2
var contextProgress = 0   //0 1
var directionProgress = 0  //0 1 2 3

fun checkAutoTestProgress() {
    //此函式用於檢查目前自動化測驗整體測驗進度

    val stimuliCheckList = arrayListOf<String>("VAP2AP", "AP2AP", "PP2AP")
    val contextCheckList = arrayListOf<String>("Finger", "Pen")
    val directionCheckList = arrayListOf<String>("L_Up", "L_Up_Right", "R_Up", "R_Up_Left")

    if (finishedStimuliList.contains(stimuliType)) {
        stimuliProgress++

        when(stimuliProgress){
            2->{ Log.d("Progress-Stimuli", "進行自動測驗中，最後一種刺激") }
            3->{ Log.d("Progress-Stimuli", "超出指定數值，請檢視程式碼錯誤") }
        }
    } else { }

    if (finishedContextList.contains(currentTestContext)) {
        contextProgress++

        when(stimuliProgress){
            1->{ Log.d("Progress-Context", "進行指定刺激下，最後一種情境") }
            2->{ Log.d("Progress-Context", "超出指定數值，請檢視程式碼錯誤") }
        }

    } else { }

    if (TestingFinishedList.contains(currentTestDirection)) {
        directionProgress++

        when(directionProgress){
            3->{ Log.d("Progress-Context", "進行指定刺激情境下，最後一種方向") }
            4->{ Log.d("Progress-Context", "超出指定數值，請檢視程式碼錯誤") }
        }
    } else { }


    u_setStimuliType(stimuliCheckList[randomizedStimuliOrder[stimuliProgress]])
    u_setContext(contextCheckList[randomizedContextOrder[contextProgress]])
    u_setDirection(directionCheckList[randomizedDirectionOrder[directionProgress]])

    Log.d("Progress-Stimuli", "目前刺激:" + stimuliType + "刺激已完成:" + finishedStimuliList.toString())
    Log.d(
        "Progress-Context",
        "目前情境:" + currentTestContext + "情境已完成:" + finishedContextList.toString()
    )
    Log.d(
        "Progress-Direction",
        "目前方向:" + currentTestDirection + "方向已完成:" + TestingFinishedList.toString()
    )
    Log.d("Progress-Direction", "目前次數:" + currentTrial + "已完成次數:" + trialCount.toString())
}

//以下自動確認測驗進度
fun auto_checkTrialLimit() {
    maxTrailDesire = MAX_FORMAL_TRIAL
    if (trialCount >= maxTrailDesire) {
        //當前方向-完成指定測驗次數，完成當前測驗方向
        //將當前測驗方向新增到已測驗方向
        TestingFinishedList.add(currentTestDirection)

        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message)
            ) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_condition)) { _, _ ->
                u_savePerformanceToCSV() //儲存測驗表現
                u_clearRecord()          //清除測驗表現
                trialCountView.text = "測驗次數: $currentTrial / $maxTrailDesire "
                //u_manageVisibility(1)
                //TODO: 從這邊繞過選項輸入 confirm selection 或 輸入指定測驗刺激/情境/方向，直接進入下一次測驗
                auto_checkDirectionTested() // 確認完成所有測驗方向
                executeAutoTestList()
                Toast.makeText(mContextKIN, "更換方向", Toast.LENGTH_SHORT).show()
            }
            .show() //creates and then displays the alert dialog.
    } else {
        //尚未完成該方向第五次測驗，繼續測驗
        //Do nothing
    }
}

fun auto_checkDirectionTested() {
    val directionCheckList = arrayListOf<String>("L_Up", "L_Up_Right", "R_Up", "R_Up_Left")
    if (TestingFinishedList.toSet() == directionCheckList.toSet()) {
        //四種方向-皆測驗完成，完成一種測驗情境
        //將當前測驗情境新增到已測驗情境
        finishedContextList.add(currentTestContext)
        //跳出對話框，更換測驗情境。
        //更換情境後，重置已測驗方向。
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_direction))
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_context))
            { _, _ ->
                TestingFinishedList = arrayListOf<String>() //重置已測驗方向。
                directionProgress = 0
                auto_checkContextTested()
                Toast.makeText(mContextKIN, "已完成該測驗情境中之所有測驗方向，請更換情境", Toast.LENGTH_SHORT).show()
                //executeAutoTestList()
            }
            .show() //creates and then displays the alert dialog.
    } else {
        //四種方向-未測驗完成
        //繼續更換測驗方向
    }
}

fun auto_checkContextTested() {
    val contextCheckList = arrayListOf<String>("Finger", "Pen")
    if (finishedContextList.toSet() == contextCheckList.toSet()) {
        //兩種情境-皆測驗完成，完成一種測驗刺激。
        //將當前測驗刺激新增到已測驗刺激
        finishedStimuliList.add(stimuliType)
        //跳出對話框，更換測驗刺激。
        //更換情境後，重置已測驗情境，重置已測驗方向。
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_context)
            ) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_stimuli)) { _, _ ->
                finishedContextList = arrayListOf<String>()
                contextProgress = 0
                TestingFinishedList = arrayListOf<String>()
                directionProgress = 0
                auto_checkStimuliTested()
                Toast.makeText(mContextKIN, "已完成該刺激方式中，兩種測驗情境，請更換測驗刺激", Toast.LENGTH_SHORT).show()
                //executeAutoTestList()
            }
            .show() //creates and then displays the alert dialog.
    } else {
        //兩種情境-未測驗完成，更情境或測驗方向
    }
}

fun auto_checkStimuliTested() {
    val stimuliCheckList = arrayListOf<String>("VAP2AP", "AP2AP", "PP2AP")
    if (finishedStimuliList.toSet() == stimuliCheckList.toSet()) {
        //三種刺激-皆測驗完成，已完成自動化測驗。
        //跳出對話框，返回測驗選單。
        //清除已測驗順序，重置測驗隨機順序
        //返回主頁面
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_stimuliType)
            ) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.dialog_back_to_menu)) { _, _ ->
                Toast.makeText(mContextKIN, "請查驗資料或補充測驗", Toast.LENGTH_SHORT).show()
                TestingFinishedList = arrayListOf<String>() //方向
                directionProgress = 0
                finishedContextList = arrayListOf<String>() //情境
                contextProgress = 0
                finishedStimuliList = arrayListOf<String>() //刺激
                stimuliProgress = 0
                resetAutoTestList()
                u_goBackToMenu()
            }
            .show() //creates and then displays the alert dialog.
    } else {
        //三種刺激-未測驗完成，繼續測驗。更換刺激或情境
    }
}

















