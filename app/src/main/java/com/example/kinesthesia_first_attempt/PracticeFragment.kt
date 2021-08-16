package com.example.kinesthesia_first_attempt

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentPracticeBinding
import com.example.kinesthesia_first_attempt.ui.main.MAX_PRACTICE_TRIAL
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.util.*


//全域變數宣告，不然無法讀取到class給的資料
var startX: Float = 0f
var startY: Float = 0f
var bb: Float = 0f
var b1: Float = 0f
var b2: Float = 0f


class PracticeFragment : Fragment() {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentPracticeBinding


    //測驗相關變數宣告

    var A = MAX_PRACTICE_TRIAL

    var buttonPressedCountsInATrial: Int = 0  //按鈕次數

    var practiceTrialsCount: Int = 0  //練習次數

    var practiceTime: Int = 0  //完整練習測驗進行次數


    // 測驗表現  >>> 要改成float?
    var startPositionX: Float = 0f
    var startPositionY: Float = 0f
    var testPositionX: Float = 0f
    var testPositionY: Float = 0f
    var restPositionX: Float = 0f
    var restPositionY: Float = 0f
    var responsePositionX: Float = 0f
    var responsePositionY: Float = 0f


    fun clearCurrentTrialRecord() {
        startPositionX = 0f
        startPositionY = 0f
        testPositionX = 0f
        testPositionY = 0f
        restPositionX = 0f
        restPositionY = 0f
        responsePositionX = 0f
        responsePositionY = 0f
        //buttonPressedCountsInATrial = 0
        condition = ""
        startX = 0f
        startY = 0f
    }

    fun addTrialsCount() {
        practiceTrialsCount++
    }


    // 儲存方法1
    fun saveTrialToMap(): MutableMap<String, Float> {
        // 將當前儲存的trialData轉換成map
        return mutableMapOf(
            "startPositionX" to startPositionX,
            "startPositionY" to startPositionY,
            "testPositionX" to testPositionX,
            "testPositionY" to testPositionY,
            "restPositionX" to restPositionX,
            "restPositionY" to restPositionY,
            "responsePositionX" to responsePositionX,
            "responsePositionY" to responsePositionY
        )
    }

    // 儲存方法2: 此方法list內容順序有差
    fun saveTrialToList(): List<Float> {
        // 將當前儲存的trialData轉換成map
        return listOf<Float>(
            startPositionX,
            startPositionY,
            testPositionX,
            testPositionY,
            restPositionX,
            restPositionY,
            responsePositionX,
            responsePositionY
        )
    }


    fun saveCurrentTrialRecord() { // 將單次反應存入?LIST? SET?
        val text1 = requireView()!!.findViewById<TextView>(R.id.text1)

        //確認目前practiceTrialsCount: android 可以用 eval() ?
        when (practiceTrialsCount) {
            1 -> {
                val Trial_1 = saveTrialToMap()
                val Trial_1_list = saveTrialToList()
            }

            2 -> {
                val Trial_2 = saveTrialToMap()
                val Trial_2_list = saveTrialToList()

            }

            3 -> {
                val Trial_3 = saveTrialToMap()
                val Trial_3_list = saveTrialToList()

            }

            4 -> {
                val Trial_4 = saveTrialToMap()
                val Trial_4_list = saveTrialToList()

            }

            5 -> {
                val Trial_5 = saveTrialToMap()
                val Trial_5_list = saveTrialToList()

            }

            6 -> {
                val Trial_6 = saveTrialToMap()
                val Trial_6_list = saveTrialToList()

            }

            7 -> {
                val Trial_7 = saveTrialToMap()
                val Trial_7_list = saveTrialToList()

            }

            8 -> {
                val Trial_8 = saveTrialToMap()
                val Trial_8_list = saveTrialToList()

            }
        }


    }


    val titleList = arrayListOf(
        "Trial",
        "Start Position X", "Start Position Y",
        "Test Position X", "Test Position Y",
        "Rest Position X", "Rest Position Y",
        "Response Position X", "Response Position Y"
    )

    // 注意 titleList practiceTrialCount dataList 還沒整合成arraay


    fun calculateTrialScore() {
        // Absolute Error: AP、ML、(AP^2+ML^2)^1/2

        // Relative Error: AP、ML

        // Variable Error: AP、ML、(AP^2+ML^2)^1/2  >> 需每個方向全部測完才能算
    }


    fun savePracticePerformanceToCSV() {
        // 存檔前置參數宣告

        //檔案路徑與名稱段落
        val outputPath = binding.viewModel!!.outputFilePath.toString()  //讀取存在viewModel的路徑
        val outputFileName = "Practice_Performance_$practiceTime.csv"
        // val outputCsvFile = File(outputPath, "Practice_Performance" + ".csv")         //儲存在這個fragment需要的檔案

        //整理表現資料>> 在這邊要把LIST排好
        val test = listOf<String>("0")
        // 這邊需要測試
        Log.d("PracticeFragment", "開始整理資料")


        // 存檔
        outputCsv(outputFileName, test, 0)
    }


    // sample from HW
    // List要先排好
    //需要: 1. 檔名、檔案路徑  2. list   3. Int = 0

    fun outputCsv(fileName: String, kkk: List<String>, flag: Int) {
        val tmp = StringBuffer()//必需
        val file = File(filePathStr, fileName)         //必需，可加時間末碼
        val os = FileOutputStream(file, true)   // 這邊給的字串要有檔案類型 >> 看引用的地方
        os.write(tmp.toString().toByteArray())
        os.flush()
        os.close()
        tmp.setLength(0) //clean buffer
    }  // sample from HW


    // 注意目前寫法，會停在此function不出來4秒
    fun checkTime() {
        // 找到關聯的view
        val recordingButton = requireView()!!.findViewById<Button>(R.id.practice_record_position)
        val text1 = requireView()!!.findViewById<TextView>(R.id.text1)
        // hide button
        recordingButton.visibility = View.INVISIBLE

        var millisInFuture:Long = 4000
        //確認是否需要倒數  millisInFuture
        if (buttonPressedCountsInATrial == 5) {
            millisInFuture = 0
        } else {
            millisInFuture = 4000
        }

        //計時器宣告
        val timer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                text1.text = "seconds remaining: " + millisUntilFinished / 1000
            }

            // 時間到時將會執行的任務
            override fun onFinish() {
                text1.text = "Time is up! Do Next Step!"
                // beep
                // show　button
                recordingButton.visibility = View.VISIBLE
            }
        }
        timer.start() //開始計時
    }


    // 清除所有參數!，還沒寫完!
    fun clearRecord() {
        practiceTrialsCount = 0
    }


    fun checkPracticeLimit() {
        // practiceTrialsCount > MAX_PRACTICE_TRIAL
        if (practiceTrialsCount >=2) {
            practiceTime++
            binding.viewModel!!.setPracticeTime(practiceTime) //更新總練習次數

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.practice_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.practice_dialog_message)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                .setNegativeButton(getString(R.string.practice_dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                    savePracticePerformanceToCSV()//儲存測驗表現
                    //clearRecord()  // 清除測驗表現>> 還沒寫完
                    Toast.makeText(activity, "再試一次", Toast.LENGTH_SHORT)
                }
                .setPositiveButton(getString(R.string.practice_dialog_back_to_menu)) { _, _ ->
                    savePracticePerformanceToCSV()// 儲存測驗表現
                    //clearRecord()  // 清除測驗表現>> 還沒寫完
                    goBackToMenu() // 前往測驗選單
                }
                .show() //creates and then displays the alert dialog.
        }
    }


    fun goBackToMenu() {
        Toast.makeText(activity, "回到測驗選單", Toast.LENGTH_SHORT)
        findNavController().navigate(R.id.action_practiceFragment_to_testMenuFragment2)
    }


    ////////////////////////////////////////////////////////////////


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val fragmentBinding = FragmentPracticeBinding.inflate(inflater, container, false)
        //binding = fragmentBinding
        //return fragmentBinding.root
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_practice, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            //sendButton.setOnClickListener { sendOrder() }
            practiceFragment = this@PracticeFragment //使用listenser binding，用UI button 在xml中設定onclick
        }
        //tBoard = requireView()!!.findViewById<TouchBoard>(R.id.view)
        //detectTouch()
    }


    companion object {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////


    //按鈕後執行
    //1.增加按鈕次數
    //2.call recordPosition
    //3.call changeText
    //4. 計時
    //5.歸零按鈕次數
    fun pressButton() {
        buttonPressedCountsInATrial++      //每按一次按鈕+1
        Log.d("X/Y/面積/長軸/短軸：inFragment", "$startX  $startY  $bb  $b1  $b2")
        recordPosition()   //儲存位置，並管理測驗流程
        changeText()       //更動text
        checkTime()
        if (buttonPressedCountsInATrial == 5){
            addTrialsCount() // 完成一次測驗練習
            saveCurrentTrialRecord() // 將單次反應存入?LIST? SET?
            clearCurrentTrialRecord() // 清除單次表現、歸零座標、重設測驗情境
            checkPracticeLimit()    //檢查是否達到練習次數
            buttonPressedCountsInATrial =0
        }



        return //outPutCoordinate()
    }


    // 記錄當下手指位置，並管理測驗流程
    // recordPosition功能描述
    // 1.將XY存入對應情境的變數中
    // 2.log.d中印出對應情境的資料
    var trialCondition =
        listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")
    var condition: String = ""
    fun recordPosition() {

        when (buttonPressedCountsInATrial) {
            1 -> {
                condition = trialCondition[0]
                startPositionX = startX
                startPositionY = startY
                Log.d("$condition", "$startPositionX  $startPositionY ")
            }
            2 -> {
                condition = trialCondition[1]
                testPositionX = startX
                testPositionY = startY
                Log.d("$condition", "$testPositionX  $testPositionY ")
            }
            3 -> {
                condition = trialCondition[2]
                restPositionX = startX
                restPositionY = startY
                Log.d("$condition", "$restPositionX  $restPositionY  ")
            }
            4 -> {
                condition = trialCondition[3]
                responsePositionX = startX
                responsePositionY = startY
                Log.d("$condition", "$responsePositionX  $responsePositionY  ")
            }

            5 -> {
                condition = ""
                startX = 0f
                startY = 0f
            }


        }
    }


    // 更新測驗表現、指導語
    fun changeText() {
        // 找到測驗表現textView
        val start = requireView()!!.findViewById<TextView>(R.id.performance_start_position)
        val test = requireView()!!.findViewById<TextView>(R.id.performance_test_position)
        val rest = requireView()!!.findViewById<TextView>(R.id.performance_rest_position)
        val response = requireView()!!.findViewById<TextView>(R.id.performance_response_position)
        //找到指導語textView
        val instructionText = requireView()!!.findViewById<TextView>(R.id.instruction_demonstration)
        //找到測驗按鈕
        val recordingButton = requireView()!!.findViewById<Button>(R.id.practice_record_position)

        //判斷測驗情境，並更新對應的Text
        when (condition) {
            "Start Position" -> {
                start.text = "Start Position：X= $startPositionX ; Y= $startPositionY"
                instructionText.text = "將受試者的手指或筆移動到目標位置上，確認停止後按下紀錄"
            }
            "Test Position" -> {
                test.text = "Test Position：X= $testPositionX ; Y= $testPositionY"
                instructionText.text = "將受試者的手指或筆移動回預備位置上，確認停止後按下紀錄"
            }
            "Rest Position" -> {
                rest.text = "Rest Position：X= $restPositionX ; Y= $restPositionY"
                instructionText.text = "受試者聽到嗶聲後，需將自己的手或是筆移動回記憶中的目標位置，確認停止後按下紀錄"
            }
            "Response Position" -> {
                response.text = "Response Position：X= $responsePositionX ; Y= $responsePositionY"
                instructionText.text = "將受試者的手移開平板，確認資料正確後按下Save Trial"
                recordingButton.text = getString(R.string.next_trial)
                recordingButton.textSize = 24.toFloat()
            }
            "" -> {
                start.text = "Start Position："
                test.text = "Test Position："
                rest.text = "Rest Position："
                response.text = "Response Position："
                instructionText.text = "請將受試者手指或筆尖放在下方預備位置上，確認停止後按下紀錄"
                recordingButton.text = getString(R.string.record_position)
                recordingButton.textSize = 30.toFloat()
            }
        }
    }


} // fragment end

