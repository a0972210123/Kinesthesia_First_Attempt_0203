package com.example.kinesthesia_first_attempt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.kinesthesia_first_attempt.databinding.FragmentAutoCalibrationBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel


class AutoCalibrationFragment : Fragment() {
    private val sharedViewModel: MainViewModel by activityViewModels()
    lateinit var binding: FragmentAutoCalibrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val decorView = requireActivity().window.decorView
        u_hideSystemUI(decorView) //11/11 測試
        testCondition =
            testConditionList[5] //  val testConditionList =   listOf<String>("Practice", "Formal", "Addition", "Non_dominant", "AutoRecord","AutoCalibration","AutoVAP2AP","AutoAP2AP","AutoPP2AP")
        // 進入新測驗時，先清掉，避免干擾
        TestingFinishedList = arrayListOf<String>()
        finishedContextList = arrayListOf<String>()
        printScreenParameters()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContextKIN = requireActivity().applicationContext
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auto_calibration, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            autoCalibrationFragment = this@AutoCalibrationFragment //使用listenser binding，用UI button 在xml中設定onclick
            maxPracticeTrial = MAX_PRACTICE_TRIAL //用於更新練習次數文字
        }
        currentPosition =
            requireView().findViewById<TextView>(R.id.current_position_field)
        inAirText = requireView().findViewById<TextView>(R.id.in_air_testing)
        touchBoard = requireView().findViewById(R.id.view) as TouchBoard
        touchBoard.visibility = View.VISIBLE
        touchBoard.setOnTouchListener { _, _ ->
            u_changeInAriText()
            false
        }
        //* new 提筆時的紀錄
        touchBoard.setOnHoverListener { _, _ ->
            u_changeInAriText()
            false
        }
    }
}