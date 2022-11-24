package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentPracticeBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


// TODO: 11/22 & 23
//  ()1. 修改函式的input，嘗試去除View輸入
//  ()2. 確認XML中能否CALL u_pressButton & u_confirmSelection
//  ()3. 確認 pressbutton 是否能改為 GLOBAL
//  (V)4. 確認 gobacktoMenu 可以改為global
//  (V)5. 確認 confirmSelection，是否能改為 GLOBAL
//  () 6. 確認 checkpracticeLimit，是否能改為 GLOBAL  >這個改完 才能改pressbutton
//  () 7. 確認 getString 是否能在public中使用 > 這個改完，才能用checkpracticeLimit



//var activityKIN = view.getContext() as FragmentActivity
//var manager: FragmentManager = activity.supportFragmentManager

class PracticeFragment : Fragment() {
    public val sharedViewModel: MainViewModel by activityViewModels()
    public lateinit var binding: FragmentPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val decorView = requireActivity().window.decorView
        u_hideSystemUI(decorView) //11/11 測試
        // hideSystemUI() //修改前
        testCondition = testConditionList[0] //  val testConditionList = listOf<String>("Practice","Formal")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val fragmentBinding = FragmentPracticeBinding.inflate(inflater, container, false)
        //binding = fragmentBinding
        //return fragmentBinding.root
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_practice, container, false)
        //binding.lifecycleOwner = this
       return binding.root

    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            practiceFragment = this@PracticeFragment //使用listenser binding，用UI button 在xml中設定onclick
            maxPracticeTrial = MAX_PRACTICE_TRIAL //用於更新練習次數文字
        }


        navControllerKIN = findNavController() //必須，用於從public function呼叫navControllerKIN

        mContextKIN = requireActivity().applicationContext
        // 11/15調整，View宣告位置
        // 11/15 View 相關宣告測試，嘗試避免重複宣告 requireView().findViewById
        start = requireView().findViewById<TextView>(R.id.performance_start_position)
        test = requireView().findViewById<TextView>(R.id.performance_test_position)
        rest = requireView().findViewById<TextView>(R.id.performance_rest_position)
        response = requireView().findViewById<TextView>(R.id.performance_response_position) //測驗次數textView
        trialCountView = requireView().findViewById<TextView>(R.id.trial_count) //找到測驗按鈕
        recordingButton = requireView().findViewById<Button>(R.id.record_position) //找到指導語textView
        instructionText = requireView().findViewById<TextView>(R.id.instruction_demonstration)

        // 11/15調整，Context & Spinner宣告位置
        trialInputSpinner = requireView().findViewById<View>(R.id.trialInput_list) as Spinner
        contextSpinner = requireView().findViewById<View>(R.id.context_list) as Spinner
        // Todo:>>> 待新增測驗方向Spinner
        // Todo:>>> 待新增測驗方法Spinner (VAP2AP & AP2AP & PP2AP)
        // Todo:>>> 測驗方向和目標的View宣告，正式測驗中，需要新增斜向箭頭

        fingerTarget = requireView().findViewById<ImageView>(R.id.target)
        fingerStartPoint = requireView().findViewById<ImageView>(R.id.start_point)
        fingerDownArrow = requireView().findViewById<ImageView>(R.id.down_arrow)
        penTarget = requireView().findViewById<ImageView>(R.id.pen_target)
        penStartPoint = requireView().findViewById<ImageView>(R.id.pen_start_point)
        penDownArrow = requireView().findViewById<ImageView>(R.id.pen_down_arrow)

        countAndHint = requireView().findViewById<TextView>(R.id.text1)         //11/21 計時用 text view

        //11/21 manageVisibility
        selectButton = requireView().findViewById(R.id.confirm_trial) as Button
        randomTargetView = requireView().findViewById<ImageView>(R.id.random_target)
        currentPosition = requireView().findViewById<TextView>(R.id.current_position_field)  //11/17 優化
        inAirText = requireView().findViewById<TextView>(R.id.in_air_testing) //11/17 優化
        touchBoard = requireView().findViewById(R.id.view) as TouchBoard //11/17 優化
        Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score) //11/17 優化


        u_changeInAriText()     // DEFAULT inAir文字
        //* new 下筆時的紀錄
        touchBoard.setOnTouchListener { _, _ ->

            if (buttonPressedCountsInATrial == 3 && currentTestContext == "Pen") {
                u_arrangeInAirData()
            }
            u_changeInAriText()
            false
        }

        //* new 提筆時的紀錄
        touchBoard.setOnHoverListener { _, _ ->

            if (buttonPressedCountsInATrial == 3 && currentTestContext == "Pen") {
                u_arrangeInAirData()
            }
            u_changeInAriText()
            false
        }
        //* new

        //更新文字view
        u_changeText()
        u_displayScoreInText(scoreListForDisplay,0, Score)  // flag = 0  顯示預設文字

        // Todo: 需要新增一個測驗方法spinner，VAP2AP & AP2AP & PP2AP，並新增根據選擇結果的，view調整判斷式，存檔名稱調整判斷式
        u_launchTrialInputSpinner()
        u_launchContextSpinner()
        u_checkContextAndLaunchView(currentTestContext)


    }



   fun pressButton() {
        buttonPressedCountsInATrial++      //每按一次按鈕+1
        Log.d("X/Y/面積/長軸/短軸：inFragment", "$startX  $startY  $bb  $b1  $b2")
        u_recordPosition()   //儲存位置，並管理測驗流程，直接讀取全域變數
        u_changeText()       //更動text   //11/23改為global
//        u_changeText(currentTrial,maxTrailDesire,condition,trialCountView,instructionText,
//            start,test,rest,response,
//            recordingButton)

        u_checkTime() // 11/21 改global
        //checkTime()  //計時

        if (buttonPressedCountsInATrial == 4) {

            // Todo: 這邊之後要加入 given position來計算
            scoreListForDisplay = u_calculateTrialScoreP()   //計算測驗表現 (RE*2，AE*3)
            //scoreListForDisplay = calculateTrialScoreP()  // 11/21 更新為 global

            //displayScoreInText(scoreListForDisplay, 1)       //更新text內容
            u_displayScoreInText(scoreListForDisplay,1, Score)  //11/21 更新為 global
            //clearScoreList()
            u_clearScoreList()
        } else {
            //displayScoreInText(scoreListForDisplay, 0)
            u_displayScoreInText(scoreListForDisplay,0, Score)  //11/21 更新為 global
        }


        if (buttonPressedCountsInATrial == 5) {

            //0912測試存InAir
                if (currentTestContext == "Pen"){
                    u_saveInAirDataToCSV(inAirData)
                    // saveInAirDataToCSV() 11/11新版，未驗證
                }

            u_clearInAir() // 11/21
            //clearInAir() // 11/11前舊版

            //11/11前舊版
            //addTrialsCount()
            //saveCurrentTrialRecord()   //將單次反應存入LIST(包含分數計算)
            //clearCurrentTrialRecord()  //清除單次表現、歸零座標、重設測驗情境

            //11/11新版，未驗證
            u_addTrialsCount() // 完成一次測驗練習   //11/21
            u_saveCurrentTrialRecord()
            u_clearCurrentTrialRecord() //11/11新版，未驗證


            u_checkPracticeLimit()       //檢查是否達到練習次數
            buttonPressedCountsInATrial = 0
        }
        return
    }




    // 此函式不改成 global，維持原local，但把內部其他funtion改為global
    @SuppressLint("SetTextI18n")
   fun checkPracticeLimit() {
        if (practiceTrialsCount >= maxTrailDesire) {  // practiceTrialsCount > MAX_PRACTICE_TRIAL
            practiceTime++  //增加練習次數
            //binding.viewModel!!.setPracticeTime(practiceTime) //更新總練習次數
            u_updatePracticeTimeToViewModel()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.practice_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.practice_dialog_message)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                .setNegativeButton(getString(R.string.practice_dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods

                    u_savePracticePerformanceToCSV()//儲存測驗表現
                    u_clearRecord()  // 清除測驗表現>> 還沒寫完
                    trialCountView.text = "練習次數: $currentTrial / $maxTrailDesire"
                    u_manageVisibility(1)

                    Toast.makeText(requireContext(), "再試一次", Toast.LENGTH_SHORT).show()
                }
                .setPositiveButton(getString(R.string.practice_dialog_back_to_menu)) { _, _ ->
                    u_savePracticePerformanceToCSV()// 儲存測驗表現
                    u_clearRecord()
                    u_goBackToMenu()
                }
                .show() //creates and then displays the alert dialog.
        }
    }


} // fragment end


