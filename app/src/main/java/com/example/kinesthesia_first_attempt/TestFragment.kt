package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentTestBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.math.sqrt


class TestFragment : Fragment() {
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentTestBinding
    private lateinit var mContext_demo: Context
    lateinit var directionSpinner : Spinner

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_test, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            testFragment = this@TestFragment //使用listenser binding，用UI button 在xml中設定onclick
        }

        //0824可以讀到即時觸碰位置
        val currentPosition = requireView().findViewById<TextView>(R.id.current_position_field)
        val touchBoard = requireView().findViewById(R.id.view) as TouchBoard
        touchBoard.setOnTouchListener(View.OnTouchListener { v, event ->
            currentPosition.text = ("Current Position: X= $startX ,Y= $startY")
            //邏輯寫在這裡
            false
        })


        /// direction spinner
        mContext_demo = requireActivity().applicationContext
        directionSpinner = requireView()!!.findViewById<View>(R.id.direction_list) as Spinner

        val adapter = ArrayAdapter.createFromResource(
            mContext_demo,
            R.array.direction_list,
            android.R.layout.simple_spinner_dropdown_item
        )
        directionSpinner.adapter = adapter
        directionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //binding.viewModel?.setCity(cityList[position])
               setDirection(directionList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
        //以上:z; 方向選單CODE: arrayList已經移置string ,name: direction_list

    }

    //變數宣告
    ////////////////////////////////////////////////////////////////////////////////////

    //測驗相關變數宣告
    var buttonPressedCountsInATrial: Int = 0  //按鈕次數
    var trialsCount: Int = 0  //已練習次數
    var currentTrial:Int = 1  //當前Trial

    // 測驗情境變數
    var trialCondition =
        listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")
    var condition: String = ""

    var currentTestDirection : String =""
    val directionList = arrayListOf<String>("請選方向", "L2L", "L2R", "R2R","R2L")
    var TestingFinishedList = arrayListOf<String>("","","")


    // 單次測驗表現
    var startPositionX: Float = 0f
    var startPositionY: Float = 0f
    var testPositionX: Float = 0f
    var testPositionY: Float = 0f
    var restPositionX: Float = 0f
    var restPositionY: Float = 0f
    var responsePositionX: Float = 0f
    var responsePositionY: Float = 0f

    //顯示表現用暫存LIST
    var scoreListForDisplay =  listOf<Float>(0f, 0f, 0f, 0f, 0f)

    // 存檔相關變數宣告
    private val positionData = StringBuffer()
    private lateinit var arrayListOf5Trial : ArrayList<List<Float>>
    private var trial1list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f) //四次表現(X/Y) + 5個表現參數  = 13
    private var trial2list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial3list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial4list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial5list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)



    //此段落: 用於condition選擇 (參考city list)

    fun setDirection(directionInput:String){
        currentTestDirection = directionInput  //儲存目前選擇方向

        //選用後立即調整版面 ( 參考 unscramble判斷方式



    }

    fun confirmSelection(){
        manageVisibility(0)  //顯示觸控板及記錄紐

        //此段落: 用於判斷condition是否選用過


        //並更新測驗紀錄
        //var TestingFinishedList = listOf<String>("","","")

        //根據情境顯示測驗view
        Toast.makeText(requireContext(), "開始進行 $currentTestDirection 測驗", Toast.LENGTH_SHORT)
    }   //確認選擇方向、並更新測驗紀錄

    fun manageVisibility(flag:Int){
        //找到相關的View
        val touchBoard = requireView().findViewById(R.id.view) as TouchBoard
        directionSpinner = requireView()!!.findViewById<View>(R.id.direction_list) as Spinner
        val selectButton = requireView().findViewById(R.id.select_direction) as Button
        val recordingButton = requireView().findViewById<Button>(R.id.record_position)

        val Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score)

        when(flag){
            0 ->{
                //顯示測驗進行VIEW
                recordingButton.visibility = View.VISIBLE
                touchBoard.visibility = View.VISIBLE
                //隱藏方向選擇VIEW
                directionSpinner.visibility =  View.INVISIBLE
                selectButton.visibility =  View.INVISIBLE
            }

            1 ->{    //放在每5次結束
                //隱藏測驗進行VIEW
                recordingButton.visibility = View.INVISIBLE
                touchBoard.visibility = View.INVISIBLE
                //顯示方向選擇VIEW
                directionSpinner.visibility =  View.VISIBLE
                selectButton.visibility =  View.VISIBLE
                //
                Score.text = "Score"
            }
        }
    }  //管理測驗相關View顯示、可觸控與否


    //按鈕後執行
    //1.增加按鈕次數 2.call recordPosition 3.call changeText 4. 計時 5.歸零按鈕次數
    fun pressButton() {
        buttonPressedCountsInATrial++      //每按一次按鈕+1
        Log.d("X/Y/面積/長軸/短軸：inFragment", "$startX  $startY  $bb  $b1  $b2")
        recordPosition()   //儲存位置，並管理測驗流程
        changeText()       //更動text
        checkTime()        //計時

        if(buttonPressedCountsInATrial == 4){
            scoreListForDisplay = calculateTrialScore()   //計算測驗表現 (RE*2，AE*3)
            displayScoreInText(scoreListForDisplay,1)       //更新text內容
            clearScoreList()
        }else{
            displayScoreInText(scoreListForDisplay,0)
        }


        if (buttonPressedCountsInATrial == 5) {
            addTrialsCount()           // 完成一次測驗練習
            saveCurrentTrialRecord()   //將單次反應存入LIST(包含分數計算)
            clearCurrentTrialRecord()  //清除單次表現、歸零座標、重設測驗情境
            checkTrialLimit()       //檢查是否達到練習次數
            buttonPressedCountsInATrial = 0
        }
        return
    }

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
    }  // 記錄當下手指位置，並管理測驗流程  1.將XY存入對應情境的變數中 2.log.d中印出對應情境的資料

    // 更新測驗表現、指導語
    @SuppressLint("SetTextI18n")
    fun changeText() {
        // 找到測驗表現textView
        val start = requireView().findViewById<TextView>(R.id.performance_start_position)
        val test = requireView().findViewById<TextView>(R.id.performance_test_position)
        val rest = requireView().findViewById<TextView>(R.id.performance_rest_position)
        val response = requireView().findViewById<TextView>(R.id.performance_response_position)
        //測驗次數textView
        val formalTrialCount = requireView().findViewById<TextView>(R.id.trial_count)

        //找到測驗按鈕
        val recordingButton = requireView().findViewById<Button>(R.id.record_position)
        //顯示已完成練習次數
        formalTrialCount.text = "測驗次數: $currentTrial / 5 "

        //判斷測驗情境，並更新對應的Text
        when (condition) {
            "Start Position" -> {
                start.text = "Start Position：X= $startPositionX ; Y= $startPositionY"
            }
            "Test Position" -> {
                test.text = "Test Position：X= $testPositionX ; Y= $testPositionY"
            }
            "Rest Position" -> {
                rest.text = "Rest Position：X= $restPositionX ; Y= $restPositionY"
            }
            "Response Position" -> {
                response.text = "Response Position：X= $responsePositionX ; Y= $responsePositionY"
                recordingButton.text = getString(R.string.next_trial)
                recordingButton.textSize = 24.toFloat()
            }
            "" -> {
                start.text = "Start Position："
                test.text = "Test Position："
                rest.text = "Rest Position："
                response.text = "Response Position："
                recordingButton.text = getString(R.string.record_position)
                recordingButton.textSize = 30.toFloat()
            }
        }
    }

    fun clearCurrentTrialRecord() {
        startPositionX = 0f
        startPositionY = 0f
        testPositionX = 0f
        testPositionY = 0f
        restPositionX = 0f
        restPositionY = 0f
        responsePositionX = 0f
        responsePositionY = 0f
        condition = ""
        startX = 0f
        startY = 0f
    }

    fun addTrialsCount() {
        trialsCount++
        currentTrial++
    }

    fun saveTrialToList(): List<Float> {
        val tempScore = calculateTrialScore()  //計算當前trial表現Error
        // 將當前儲存的trialData轉換成List
        return listOf<Float>(
            startPositionX,
            startPositionY,
            testPositionX,
            testPositionY,
            restPositionX,
            restPositionY,
            responsePositionX,
            responsePositionY,
            tempScore[0],  //RE AP
            tempScore[1],  //RE ML
            tempScore[2],  //AE AP
            tempScore[3],  //AE ML
            tempScore[4]   //AE T2R
        )
    }

    fun reset5Trial(){
        for (n in 0..4){
            arrayListOf5Trial[n] = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        }
    } //清除5trial

    fun clearRecord() {
        clearCurrentTrialRecord() // 四個位置、startX/Y的全域變數
        trialsCount = 0           //進入測驗練習後的練習次數
        currentTrial = 1
        reset5Trial()             // 清除所有trial的紀錄
        positionData.setLength(0) //clean buffer
    }

    fun saveCurrentTrialRecord() {
        //確認目前practiceTrialsCount
        when (trialsCount) {
            1 -> { trial1list = saveTrialToList() }
            2 -> { trial2list = saveTrialToList() }
            3 -> { trial3list = saveTrialToList() }
            4 -> { trial4list = saveTrialToList() }
            5 -> { trial5list = saveTrialToList() }
        }
    }  //依據trial數，存檔至特定list

    fun combineList(): ArrayList<List<Float>> {
        return arrayListOf(
            trial1list,
            trial2list,
            trial3list,
            trial4list,
            trial5list,
        )
    }  //將5個Trial組成array

    fun arrangeData(TargetStringBuffer:StringBuffer):StringBuffer{   // 將List排進Buffer
        for(index in 0..4){  //選一個trial
            TargetStringBuffer.append(index + 1) //trial
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][0])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][1])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][2])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][3])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][4])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][5])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][6])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][7])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][8])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][9])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][10])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][11])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf5Trial[index][12])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append("\r\n")
        }
        return TargetStringBuffer
    } // 將List排進Buffer

    fun calculateTrialScore(): List<Float> {
        //計算向量: 以startPosition為基準/原點
        //Vector( test2Response ) = Vector( start2Response ) - Vector( start2Test )

        //start2Test = Test(x,y) - Start(x,y)
        var start2TestX = testPositionX - startPositionX
        var start2TestY = testPositionY - startPositionY

        //start2Response = Response(x,y) - Start(x,y)
        var start2ResponseX = responsePositionX - startPositionX
        var start2ResponseY = responsePositionY - startPositionY

        //test2Response
        var test2ResponseAP = start2ResponseY - start2TestY
        var test2ResponseML = start2ResponseX - start2TestX

        //Relative Error: AP、ML
        var relativeErrorAP = test2ResponseAP
        var relativeErrorML = test2ResponseML
        //Absolute Error: AP、ML、T2R(AP^2+ML^2)^1/2
        var absoluteErrorAP = kotlin.math.abs(test2ResponseAP)
        var absoluteErrorML = kotlin.math.abs(test2ResponseML)

        val a = test2ResponseAP.toDouble()
        val b = test2ResponseML.toDouble()
        val c:Double = sqrt(a*a + b*b)
        var absoluteErrorT2R = c.toFloat()

        return listOf(
            relativeErrorAP,
            relativeErrorML,
            absoluteErrorAP,
            absoluteErrorML,
            absoluteErrorT2R
        )
        // Variable Error: AP、ML、(AP^2+ML^2)^1/2  >> 需每個方向全部測完才能算 >> 在這邊先不算
    }  //計算測驗分數

    fun clearScoreList(){
        scoreListForDisplay = listOf<Float>(0f, 0f, 0f, 0f, 0f)
    } //清除測驗分數

    fun displayScoreInText(inputScoreList:List<Float>,flag: Int){
        val Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score)

        var performanceDescriptionAP:String = ""  //Y軸
        var performanceDescriptionML:String = ""  //X軸

        when {
            inputScoreList[0] >0 -> {
                performanceDescriptionAP = "Underestimated"
            }
            inputScoreList[0] <0 -> {
                performanceDescriptionAP = "Overestimated"
            }
            inputScoreList[0] == 0f -> {
                performanceDescriptionAP = "Perfect Matched"
            }
        }

        when {
            inputScoreList[1] >0 -> {
                performanceDescriptionML = "Right Deviated"
            }
            inputScoreList[1] <0 -> {
                performanceDescriptionML = "Left  Deviated"
            }
            inputScoreList[1] == 0f -> {
                performanceDescriptionML = "Perfect Matched"
            }
        }

        when (flag){
            1 -> {
                val modifyString:String = (  "Score" + "\n"+
                        "Anterior-Posterior Direction: " + performanceDescriptionAP + "\n" +
                        "Medial - Lateral   Direction: " + performanceDescriptionML + "\n" +
                        "Relative Error  AP: " + inputScoreList[0].toString() + "\n" +
                        "Relative Error  ML: " + inputScoreList[1].toString() + "\n" +
                        "Absolute Error  AP: " + inputScoreList[2].toString() + "\n" +
                        "Absolute Error  ML: " + inputScoreList[3].toString() + "\n" +
                        "Absolute Error T2R: " + inputScoreList[4].toString() )
                Score.text = modifyString
            }
            0 -> {
                val modifyString:String = (  "Score" + "\n"+
                        "Anterior-Posterior Direction: " + "" + "\n" +
                        "Medial - Lateral   Direction: " + "" + "\n" +
                        "Relative Error  AP: " + "" + "\n"+
                        "Relative Error  ML: " + "" + "\n"+
                        "Absolute Error  AP: " + "" + "\n"+
                        "Absolute Error  ML: " + "" + "\n"+
                        "Absolute Error T2R: "+ "" )
                Score.text = modifyString
            }
        }
    }  //用於描述測驗表現

    fun savePracticePerformanceToCSV() {
        //call 整理8trialData
        arrayListOf5Trial = combineList()
        //call function 將List排進buffer
        arrangeData(positionData)
        //切割buffer
        val outputPositionData = positionData.toString().replace("\r", "").split("\n")

        //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
        val outputFileName = "Formal_Performance_$currentTestDirection.csv"

        // 存檔: name,List,flag
        outputCsv(outputFileName, outputPositionData , 0)
    }   //存檔要改成Direction名稱

    fun outputCsv(fileName: String, input: List<String>, flag: Int) {
        //檔案路徑: 目前直接讀在demographic的全域變數，有error再讀viewModel備用
        //val outputPath = binding.viewModel!!.outputFilePath.toString()  //讀取存在viewModel的路徑
        val output = StringBuffer()//必需

        val titleList = listOf(
            "Trial",
            "Start Position X", "Start Position Y",
            "Test Position X", "Test Position Y",
            "Rest Position X", "Rest Position Y",
            "Response Position X", "Response Position Y"
        )

        val scoreTitleList = listOf(
            "Relative Error AP",
            "Relative Error ML",
            "Absolute Error AP",
            "Absolute Error ML",
            "Absolute Error T2R"
        )

        //合併title
        val allTitle: MutableList<String> = mutableListOf()
        allTitle.addAll(titleList)
        allTitle.addAll(scoreTitleList)

        // 放入標題，使用迴圈，避免前後出現[]
        for (i in 0..13){
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
        Toast.makeText(activity, "正式測驗: "+currentTestDirection+" 儲存成功", Toast.LENGTH_SHORT)
        //Toast.makeText(activity, "正式測驗"+$direction+"儲存成功", Toast.LENGTH_SHORT)
        Log.d("data","outCSV Success")
    }  // sample from HW

    fun checkTime() {
        // 找到關聯的view
        val recordingButton = requireView().findViewById<Button>(R.id.record_position)
        val text1 = requireView().findViewById<TextView>(R.id.text1)
        // hide button
        recordingButton.visibility = View.INVISIBLE

        //確認是否需要倒數  millisInFuture
        var millisInFuture: Long = 4000
        if (buttonPressedCountsInATrial == 5) {
            millisInFuture = 0
        } else {
            millisInFuture = 1000 //暫時改
        }
        //計時器宣告
        val timer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                text1.text = "seconds remaining: " + millisUntilFinished / 1000
            }

            // 時間到時將會執行的任務
            override fun onFinish() {

                when (buttonPressedCountsInATrial) {
                    0 -> {
                        text1.text = "Get Ready!"
                    }
                    1 -> {
                        text1.text = "Time is up! Do Next Step!"
                    }
                    2 -> {
                        text1.text = "Time is up! Do Next Step!"
                    }
                    3 -> {
                        text1.text = "Time is up! Do Next Step!"
                    }
                    4 -> {
                        text1.text = "Click to Save this Trial"
                    }
                    5 -> {
                        text1.text = "Get Ready!"
                    }
                }
                //Beep sound
                val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
                if (buttonPressedCountsInATrial == 3) {
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 200)
                } else {
                }
                // show　button
                recordingButton.visibility = View.VISIBLE
            }
        }
        timer.start() //開始計時
    }

    fun checkTrialLimit() {
        val formalTrialCount = requireView().findViewById<TextView>(R.id.trial_count)

        if (trialsCount >= 5) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.test_dialog_message)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                .setNegativeButton(getString(R.string.test_dialog_next_condition)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                    savePracticePerformanceToCSV()//儲存測驗表現
                    clearRecord()  // 清除測驗表現>> 還沒寫完
                    formalTrialCount.text = "測驗次數: $trialsCount / 5 "
                    manageVisibility(1)
                    Toast.makeText(requireContext(), "更換情境", Toast.LENGTH_SHORT)
                }
                .setPositiveButton(getString(R.string.test_dialog_back_to_menu)) { _, _ ->
                    savePracticePerformanceToCSV()// 儲存測驗表現
                    clearRecord()  // 清除測驗表現>> 還沒寫完
                    goBackToMenu() // 前往測驗選單
                    // action_testFragment_to_testMenuFragment
                }
                .show() //creates and then displays the alert dialog.
        }
    }  //需修改成每完成一個方向，提供補測機會

    fun goBackToMenu() {
        Toast.makeText(activity, "回到測驗選單", Toast.LENGTH_SHORT)
        findNavController().navigate(R.id.action_testFragment_to_testMenuFragment)
    }




} //Fragment End