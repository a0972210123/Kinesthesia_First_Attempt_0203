package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentPracticeBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel

class PracticeFragment : Fragment() {
    private val sharedViewModel: MainViewModel by activityViewModels()
    lateinit var binding: FragmentPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val decorView = requireActivity().window.decorView
        u_hideSystemUI(decorView) //11/11 測試
        // hideSystemUI() //修改前
        testCondition = testConditionList[0] //  val testConditionList = listOf<String>("Practice","Formal")

    }//onCreate end


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

    } //onCreateView end

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

        //以下三行為 global function需要的input
        mainViewModel = sharedViewModel  // 這行是為了讓public可以讀到ViewModel 且不需要重新 initiate
        mActivityKIN = requireActivity()
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

        // 確認人口學資料
        u_checkDemographicInputAndUpdateDefault(mActivityKIN,mContextKIN)

        Log.d("PracticeFragment", "PracticeFragment created!")
    } //onViewCreated end


} // fragment end


