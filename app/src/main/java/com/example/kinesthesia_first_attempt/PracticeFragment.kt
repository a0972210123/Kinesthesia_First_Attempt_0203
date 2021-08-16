package com.example.kinesthesia_first_attempt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.text.SimpleDateFormat
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
        buttonPressedCountsInATrial = 0

    }

    fun addTrialsCount() {
        practiceTrialsCount++
    }


    var trialCondition =
        listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")

    // 記錄當下手指位置，並管理測驗流程
    fun recordPosition() {
        //單次測驗紀錄區段
        buttonPressedCountsInATrial++ //每按一次按鈕+1  (要debounce?)

        when (buttonPressedCountsInATrial) {
            1 -> {
                var condition = trialCondition[0]
                startPositionX = 0f
                startPositionY = 0f
            }
            2 -> {
                var condition = trialCondition[1]
                testPositionX = 20f
                testPositionY = 20f
            }
            3 -> {
                var condition = trialCondition[2]
                restPositionX = 30f
                restPositionY = 30f
            }
            4 -> {
                var condition = trialCondition[3]
                responsePositionX = 40f
                responsePositionY = 40f
            }

            5 -> {
                saveCurrentTrialRecord()   // 將單次反應存入?LIST? SET?
                clearCurrentTrialRecord()  //清除單次表現
                addTrialsCount()           // 完成一次測驗練習
                checkPracticeLimit()       //檢查是否達到練習次數
            }

        }
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

        //確認目前practiceTrialsCount: android 可以用 eval() ?
        when (practiceTrialsCount) {
            1 -> {
                val Trial_1 = saveTrialToMap()
                val Trial_1_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_1.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
            }

            2 -> {
                val Trial_2 = saveTrialToMap()
                val Trial_2_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_2.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
            }

            3 -> {
                val Trial_3 = saveTrialToMap()
                val Trial_3_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_3.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
            }

            4 -> {
                val Trial_4 = saveTrialToMap()
                val Trial_4_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_4.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
            }

            5 -> {
                val Trial_5 = saveTrialToMap()
                val Trial_5_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_5.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
            }

            6 -> {
                val Trial_6 = saveTrialToMap()
                val Trial_6_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_6.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
            }

            7 -> {
                val Trial_7 = saveTrialToMap()
                val Trial_7_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_7.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
            }

            8 -> {
                val Trial_8 = saveTrialToMap()
                val Trial_8_list = saveTrialToList()
                Log.d(
                    "PracticeFragment",
                    Trial_8.map { "${it.key} is ${it.value}" }.joinToString(",\n")
                )
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
        Log.d("PracticeFragment","開始整理資料")





        // 存檔
        outputCsv(outputFileName,test,0)
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




    //用於抓按鈕按下時間和計時
    fun getTime(): Float {
        val formatter = SimpleDateFormat("SSSS", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.time).toString().toFloat() //點按時間點
    }

    //須測試哪一個不會ERROR
    fun getTime_2(): Float {
        return System.currentTimeMillis().toFloat()
    }

    // 注意目前寫法，會停在此function不出來4秒
    fun checkTime() {
        var T1 = getTime()
        while (getTime() - T1 <= 4000) {
            var timeCountDown = 4000 - (getTime() - T1)

            when (timeCountDown.toInt()) {
                4000 -> {
                    Log.d("PracticeFragment", "TimeCountDown:4")
                }
                3000 -> {
                    Log.d("PracticeFragment", "TimeCountDown:3")
                }
                2000 -> {
                    Log.d("PracticeFragment", "TimeCountDown:2")
                }
                1000 -> {
                    Log.d("PracticeFragment", "TimeCountDown:1")
                }
                0 -> {
                    Log.d("PracticeFragment", "TimeCountDown:0")
                }
            }

        } //while loop end

        // 4秒時間到
        // beep
        // show button
    }


    // 清除所有參數!，還沒寫完!
    fun clearRecord() {
        practiceTrialsCount = 0
    }


    fun checkPracticeLimit() {
        if (practiceTrialsCount > MAX_PRACTICE_TRIAL) {
            practiceTime ++
            binding.viewModel!!.setPracticeTime(practiceTime) //更新總練習次數

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.practice_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.practice_dialog_message)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                .setNegativeButton(getString(R.string.practice_dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                    savePracticePerformanceToCSV()//儲存測驗表現
                    clearRecord()  //清除測驗表現
                    Toast.makeText(activity, "再試一次", Toast.LENGTH_SHORT)
                }
                .setPositiveButton(getString(R.string.practice_dialog_back_to_menu)) { _, _ ->
                    savePracticePerformanceToCSV()// 儲存測驗表現
                    clearRecord()  // 清除測驗表現
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











    //按鈕後執行
    fun printData(): List<Float>{
        Log.d("X/Y/面積/長軸/短軸：inFragment", "$startX  $startY  $bb  $b1  $b2")
        changeText()
        return listOf(startX,startY,bb,b1,b2)  //outPutCoordinate()
    }

    fun changeText(){
        val text1 = requireView()!!.findViewById<TextView>(R.id.text1)
        val text3 = requireView()!!.findViewById<TextView>(R.id.text3)

        if (text1.text.toString()=="第一次：-999") {
            text1.text = "第一次：X= $startX ; Y= $startY"
        }
        text3.text = "最後一次：X= $startX ; Y= $startY"

    }




} // fragment end