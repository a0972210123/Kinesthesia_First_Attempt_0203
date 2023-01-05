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
            autorecordFragment = this@AutorecordFragment //使用listenser binding，用UI button 在xml中設定onclick
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
        com.example.kinesthesia_first_attempt.trialInputSpinner = requireView().findViewById<View>(R.id.trialInput_list) as Spinner
        com.example.kinesthesia_first_attempt.contextSpinner = requireView().findViewById<View>(R.id.context_list) as Spinner
        com.example.kinesthesia_first_attempt.directionSpinner = requireView().findViewById<View>(R.id.direction_list) as Spinner
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
        currentPosition = requireView().findViewById<TextView>(R.id.current_position_field)  //11/17 優化
        inAirText = requireView().findViewById<TextView>(R.id.in_air_testing) //11/17 優化
        touchBoard = requireView().findViewById(R.id.view) as TouchBoard //11/17 優化
        Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score) //11/17 優化

        //11/28 新增表現分數標題
        performanceTitle = requireView().findViewById<TextView>(R.id.performance_title)
        titleParams = performanceTitle.layoutParams as ViewGroup.MarginLayoutParams
        penTargetParams = penTarget.layoutParams as ViewGroup.MarginLayoutParams
        penStartParams = penStartPoint.layoutParams as ViewGroup.MarginLayoutParams
        fingerTargetParams = fingerTarget.layoutParams as ViewGroup.MarginLayoutParams
        fingerStartParams = fingerStartPoint.layoutParams as ViewGroup.MarginLayoutParams

        fingerRandomTargetParams = fingerRandomTargetView.layoutParams as ViewGroup.MarginLayoutParams
        penRandomTargetParams = penRandomTargetView.layoutParams  as ViewGroup.MarginLayoutParams


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
        u_displayScoreInText(com.example.kinesthesia_first_attempt.scoreListForDisplay, 0, Score)  // flag = 0  顯示預設文字

        u_launchTrialInputSpinner()
        u_launchDirectionSpinner()
        u_launchContextSpinner()   //設定測驗情境(手指或握筆) > 必須
        u_launchStimuliTypeSpinner()

        u_checkContextAndLaunchView(com.example.kinesthesia_first_attempt.currentTestContext)

        // 確認人口學資料
        u_checkDemographicInputAndUpdateDefault(mActivityKIN, mContextKIN)

        Log.d("lifeCycle", "AutoRecordFragment created!")
    }


} //Fragment End





