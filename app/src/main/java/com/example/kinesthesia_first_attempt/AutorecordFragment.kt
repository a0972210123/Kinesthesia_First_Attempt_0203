package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
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
        mContextKIN = requireActivity().applicationContext
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
        //mContextKIN = requireActivity().applicationContext

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
            if (buttonPressedCountsInATrial == 3) {
                u_arrangeInAirData()
            }
            u_changeInAriText()
            false
        }
        //* new 提筆時的紀錄
        touchBoard.setOnHoverListener { _, _ ->

            if (buttonPressedCountsInATrial == 3) {
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


var randomizedStimuliOrder = listOf<Int>(0, 1, 2)
var randomizedContextOrder = listOf<Int>(0, 1)
var randomizedDirectionOrder = listOf<Int>(0, 1, 2, 3)

fun executeAutoTestList() {
    //新增 隨機測驗方向的 function，可以連續自動執行測驗
// step 1 在進入頁面，決定 刺激/情境/方向 的隨機順序 (要counter balanced)
// step 2 將這些順序 暫存在某個 List
// step 3 將這些 List 顯示在對話框 並依據測驗進度 標示 [] or [V]
// call setStimuli / setcontext / setdirection = 輸入目前的測驗參數，以啟動view

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

        when (stimuliProgress) {
            2 -> {
                Log.d("Progress-Stimuli", "進行自動測驗中，最後一種刺激")
            }
            3 -> {
                stimuliProgress = 2
                Log.d("Progress-Stimuli", "完成最後一種刺激，將回到測驗選單")
            }
        }
    } else {
    }

    if (finishedContextList.contains(currentTestContext)) {
        contextProgress++

        when (contextProgress) {
            1 -> {
                Log.d("Progress-Context", "進行指定刺激下，最後一種情境")
            }
            2 -> {
                contextProgress = 1
                Log.d("Progress-Context", "完成最後一種情境，將更換刺激")
            }
        }

    } else {
    }

    if (TestingFinishedList.contains(currentTestDirection)) {
        directionProgress++

        when (directionProgress) {
            3 -> {
                Log.d("Progress-Context", "進行指定刺激情境下，最後一種方向")
            }
            4 -> {
                directionProgress = 3
                Log.d("Progress-Context", "完成最後一種方向，將更換情境")
            }
        }
    } else {
    }



    u_setStimuliType(stimuliCheckList[randomizedStimuliOrder[stimuliProgress]])
    u_setContext(contextCheckList[randomizedContextOrder[contextProgress]])
    u_setDirection(directionCheckList[randomizedDirectionOrder[directionProgress]])

    printProgress()
}

var progressText = StringBuffer()
var progressTextChi = StringBuffer()
var wholeProgress = 0

fun printProgress() {
    // Log
    Log.d("Progress-Stimuli", "目前刺激:$stimuliType ； 刺激已完成:$finishedStimuliList")
    Log.d(
        "Progress-Context",
        "目前情境:$currentTestContext ； 情境已完成:$finishedContextList"
    )
    Log.d(
        "Progress-Direction",
        "目前方向:$currentTestDirection ； 方向已完成:$TestingFinishedList"
    )
    Log.d("Progress-Direction", "目前次數:$currentTrial ； 已完成次數:$trialCount")

    //===========================================================================================================
    progressText.setLength(0)
    progressText.append("Stimuli | Context | Direction   | Tested")
    progressText.append("\n")
    val stimuliList = arrayListOf<String>("VAP2AP ", "AP2AP  ", "PP2AP  ")  //7bit
    val contextList = arrayListOf<String>("Finger ", "Pen    ") // 7bit
    val directionList =
        arrayListOf<String>("L_Up       ", "L_Up_Right ", "R_Up       ", "R_Up_Left  ") // 11 bit
    // 放入標題，使用迴圈，避免前後出現[]
    for (layerA in 0..(stimuliList.size - 1)) {
        for (layerB in 0..(contextList.size - 1)) {
            for (layerC in 0..(directionList.size - 1)) {

                var currentIndex = 8 * layerA + 4 * layerB + 1 * layerC
                var checked: String = ""
                if (currentIndex < wholeProgress) {
                    checked = "[V]"
                } else {
                    checked = "[ ]"
                }

                var stimuliText: String = ""
                var contextText: String = ""
                var directionText: String = ""

                when (currentIndex) {
                    0 -> {
                        stimuliText = stimuliList[randomizedStimuliOrder[layerA]]
                        contextText = contextList[randomizedContextOrder[layerB]]
                    }
                    8 -> {
                        stimuliText = stimuliList[randomizedStimuliOrder[layerA]]
                        contextText = contextList[randomizedContextOrder[layerB]]
                    }
                    16 -> {
                        stimuliText = stimuliList[randomizedStimuliOrder[layerA]]
                        contextText = contextList[randomizedContextOrder[layerB]]
                    }
                    4 -> {
                        stimuliText = "       "
                        contextText = contextList[randomizedContextOrder[layerB]]
                    }
                    12 -> {
                        stimuliText = "       "
                        contextText = contextList[randomizedContextOrder[layerB]]
                    }
                    20 -> {
                        stimuliText = "       "
                        contextText = contextList[randomizedContextOrder[layerB]]
                    }
                    else -> {
                        stimuliText = "       "  //7bit
                        contextText = "       "  //7bit
                    }
                }
                directionText = directionList[randomizedDirectionOrder[layerC]]

                progressText.append("$stimuliText | $contextText | $directionText | $checked")
                if (layerB == 1) {
                    if (layerC == 3) {
                        progressText.append("\n")
                        progressText.append("------- | ------- | ----------- | ---")
                    }
                }
                progressText.append("\n")
            }
        }
    }
    //Log.d("Progress-Direction", "\n" +"$progressText")
    //============================================================================================
    progressTextChi.setLength(0)
    progressTextChi.append("測驗刺激　|　測驗情境　|　測驗方向　|　測驗進度")
    progressTextChi.append("\n")

    val stimuliListChi = arrayListOf<String>("視覺主動", "主動移動", "被動移動")
    val contextListChi = arrayListOf<String>("　手指　", "　握筆　")
    val directionListChi = arrayListOf<String>("左下左上", "左下右上", "右下右上", "右下左上")

    // 放入標題，使用迴圈，避免前後出現[]
    for (layerA in 0..(stimuliListChi.size - 1)) {
        for (layerB in 0..(contextListChi.size - 1)) {
            for (layerC in 0..(directionListChi.size - 1)) {

                var currentIndex = 8 * layerA + 4 * layerB + 1 * layerC
                var checked: String = ""
                if (currentIndex < wholeProgress) {
                    checked = "［Ｖ］"
                } else {
                    checked = "［　］"
                }

                var stimuliTextChi: String = ""
                var contextTextChi: String = ""
                var directionTextChi: String = ""

                when (currentIndex) {
                    0 -> {
                        stimuliTextChi = stimuliListChi[randomizedStimuliOrder[layerA]]
                        contextTextChi = contextListChi[randomizedContextOrder[layerB]]
                    }
                    8 -> {
                        stimuliTextChi = stimuliListChi[randomizedStimuliOrder[layerA]]
                        contextTextChi = contextListChi[randomizedContextOrder[layerB]]
                    }
                    16 -> {
                        stimuliTextChi = stimuliListChi[randomizedStimuliOrder[layerA]]
                        contextTextChi = contextListChi[randomizedContextOrder[layerB]]
                    }
                    4 -> {
                        stimuliTextChi = "　　　　"
                        contextTextChi = contextListChi[randomizedContextOrder[layerB]]
                    }
                    12 -> {
                        stimuliTextChi = "　　　　"
                        contextTextChi = contextListChi[randomizedContextOrder[layerB]]
                    }
                    20 -> {
                        stimuliTextChi = "　　　　"
                        contextTextChi = contextListChi[randomizedContextOrder[layerB]]
                    }
                    else -> {
                        stimuliTextChi = "　　　　"
                        contextTextChi = "　　　　"
                    }
                }
                directionTextChi = directionListChi[randomizedDirectionOrder[layerC]]

                progressTextChi.append("$stimuliTextChi　|　$contextTextChi　|　$directionTextChi　|　$checked")
                if (layerB == 1) {
                    if (layerC == 3) {
                        progressTextChi.append("\n")
                        progressTextChi.append("－－－－　|　－－－－　|　－－－－　|　－－－")
                    }
                }
                progressTextChi.append("\n")
            }
        }
    }

    //Log.d("Progress-Direction", "\n" +"$progressTextChi")
}


//設定預設的test存檔路徑，避免CRASH
@SuppressLint("SuspiciousIndentation")
fun u_saveTestOrder() {
    // 整理測驗順序資料
//    val outputName =
//        mContextKIN.resources.getString(R.string.your_name, mainViewModel.name.value)
//    val outputSex =
//        mContextKIN.resources.getString(R.string.your_sex, mainViewModel.sex.value)
//    val outputBirthdate = mContextKIN.resources.getString(
//        R.string.your_birthdate,
//        mainViewModel.birthdate.value
//    )
//    val outputTestDate =
//        mContextKIN.resources.getString(R.string.your_testDate, mainViewModel.testDate.value)
//    val outputHand = mContextKIN.resources.getString(
//        R.string.your_handedness,
//        mainViewModel.handedness.value
//    )
//    val outputGrade =
//        mContextKIN.resources.getString(R.string.your_grade, mainViewModel.grade.value)
//    val outputCity =
//        mContextKIN.resources.getString(R.string.your_city, mainViewModel.city.value)
//    val outputCode =
//        mContextKIN.resources.getString(R.string.your_code, mainViewModel.clientCode.value)

    //stimuliCheckList[randomizedStimuliOrder[stimuliProgress]]
    //contextCheckList[randomizedContextOrder[contextProgress]]
    //directionCheckList[randomizedDirectionOrder[directionProgress]]
    printProgress()

    val emp = "\n"  //換行字串 "\n"
    val txtFile: File  //創建檔案
    val filePathConstructCode = mainViewModel.clientCode.value.toString()

    val timeStamp: String //避免資料夾個案編號資料重複的額外後接編號
    val timeStampFormatter =
        SimpleDateFormat("HH_mm_ss", Locale.getDefault()) //H 時 在一天中 (0~23) // m 分 // s 秒
    val timeStampCalendar = Calendar.getInstance()
    timeStamp = timeStampFormatter.format(timeStampCalendar.time).toString() //當日時間  //需傳到viewModel

    /////建立檔案資料夾段落
    //資料夾名稱/路徑
    val filePath = File(mActivityKIN.getExternalFilesDir("").toString(), filePathConstructCode)

    filePath.mkdir()
    txtFile = File(filePath, filePathConstructCode + "_AutoTestOrder_" + ".txt")
    filePathStr = filePath.path      //更新全域變數，用於後續存檔

    //儲存測驗順序相關參數
    val out = FileOutputStream(txtFile, true)
    out.write(progressText.toString().toByteArray())
    out.write(emp.toByteArray())
    out.write(progressTextChi.toString().toByteArray())
    out.write(emp.toByteArray())
//    out.write(outputSex.toByteArray())
//    out.write(emp.toByteArray())
//    out.write(outputBirthdate.toByteArray())
//    out.write(emp.toByteArray())
//    out.write(outputTestDate.toByteArray())
//    out.write(emp.toByteArray())
//    out.write(outputHand.toByteArray())
//    out.write(emp.toByteArray())
//    out.write(outputGrade.toByteArray())
//    out.write(emp.toByteArray())
//    out.write(outputCity.toByteArray())
//    out.write(emp.toByteArray())
//    out.write(outputCode.toByteArray())
    out.flush()
    out.close()
    Toast.makeText(mContextKIN, "AutoTestOrder Save Success", Toast.LENGTH_SHORT).show()

}  // 存檔function END


//以下自動確認測驗進度
fun auto_checkTrialLimit() {
    maxTrailDesire = MAX_FORMAL_TRIAL
    if (trialCount >= maxTrailDesire) {
        //當前方向-完成指定測驗次數，完成當前測驗方向
        //將當前測驗方向新增到已測驗方向
        TestingFinishedList.add(currentTestDirection)
        wholeProgress++
        printProgress()

        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message)
                        + "\n" + progressTextChi
            ) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_condition)) { _, _ ->
                u_savePerformanceToCSV() //儲存測驗表現
                u_clearRecord()          //清除測驗表現
                trialCountView.text = "測驗次數: $currentTrial / $maxTrailDesire "
                //u_manageVisibility(1)
                //TODO: 從這邊繞過選項輸入 confirm selection 或 輸入指定測驗刺激/情境/方向，直接進入下一次測驗
                auto_checkDirectionTested() // 確認完成所有測驗方向
                Toast.makeText(mContextKIN, "更換方向", Toast.LENGTH_SHORT).show()
            }
            .show() //creates and then displays the alert dialog.
    } else {
        //尚未完成該方向第五次測驗，繼續測驗
        executeAutoTestList()
    }
}

fun auto_checkDirectionTested() {
    val directionCheckList = arrayListOf<String>("L_Up", "L_Up_Right", "R_Up", "R_Up_Left")
    if (TestingFinishedList.toSet() == directionCheckList.toSet()) {
        //四種方向-皆測驗完成，完成一種測驗情境
        //將當前測驗情境新增到已測驗情境
        finishedContextList.add(currentTestContext)
        printProgress()
        //跳出對話框，更換測驗情境。
        //更換情境後，重置已測驗方向。
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_direction)
                        + "\n" + progressTextChi
            )
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
        executeAutoTestList()
    }
}

fun auto_checkContextTested() {
    val contextCheckList = arrayListOf<String>("Finger", "Pen")
    if (finishedContextList.toSet() == contextCheckList.toSet()) {
        //兩種情境-皆測驗完成，完成一種測驗刺激。
        //將當前測驗刺激新增到已測驗刺激
        finishedStimuliList.add(stimuliType)
        printProgress()
        //跳出對話框，更換測驗刺激。
        //更換情境後，重置已測驗情境，重置已測驗方向。
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_context)
                        + "\n" + progressTextChi
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
        executeAutoTestList()
    }
}

fun auto_checkStimuliTested() {
    val stimuliCheckList = arrayListOf<String>("VAP2AP", "AP2AP", "PP2AP")
    if (finishedStimuliList.toSet() == stimuliCheckList.toSet()) {
        //三種刺激-皆測驗完成，已完成自動化測驗。
        printProgress()
        //跳出對話框，返回測驗選單。
        //清除已測驗順序，重置測驗隨機順序
        //返回主頁面
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.autoTest_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_stimuliType)
                        + "\n" + progressTextChi
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
                wholeProgress = 0
                u_saveTestOrder()
                progressText.setLength(0)
                progressTextChi.setLength(0)
                resetAutoTestList()
                u_goBackToMenu()
            }
            .show() //creates and then displays the alert dialog.
    } else {
        //三種刺激-未測驗完成，繼續測驗。更換刺激或情境
        executeAutoTestList()
    }
}

















