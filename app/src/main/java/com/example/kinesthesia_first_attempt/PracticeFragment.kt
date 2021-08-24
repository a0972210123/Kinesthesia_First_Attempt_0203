package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.ToneGenerator
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
import kotlin.math.sqrt







//全域變數宣告，不然無法讀取到class給的資料
var startX: Float = 0f
var startY: Float = 0f

var bb: Float = 0f
var b1: Float = 0f
var b2: Float = 0f



class PracticeFragment : Fragment(){
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentPracticeBinding


    //測驗相關變數宣告
    var buttonPressedCountsInATrial: Int = 0  //按鈕次數
    var practiceTrialsCount: Int = 0  //已練習次數
    var currentTrial:Int = 1  //當前Trial

    var practiceTime: Int = 0  //完整練習測驗進行次數

    // 測驗表現
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
        condition = ""
        startX = 0f
        startY = 0f
    }

    fun addTrialsCount() {
        practiceTrialsCount++
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


    // 存檔相關變數宣告

    private val positionData = StringBuffer()

    private lateinit var arrayListOf8Trial :ArrayList<List<Float>>
    private var trial1list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f) //四次表現(X/Y) + 5個表現參數  = 13
    private var trial2list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial3list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial4list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial5list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial6list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial7list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial8list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)


    fun reset8Trial(){
        for (n in 0..7){
            arrayListOf8Trial[n] = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        }
    }


    // 將單次反應存入LIST
    fun saveCurrentTrialRecord() {
        //確認目前practiceTrialsCount
        when (practiceTrialsCount) {
            1 -> { trial1list = saveTrialToList() }
            2 -> { trial2list = saveTrialToList() }
            3 -> { trial3list = saveTrialToList() }
            4 -> { trial4list = saveTrialToList() }
            5 -> { trial5list = saveTrialToList() }
            6 -> { trial6list = saveTrialToList() }
            7 -> { trial7list = saveTrialToList() }
            8 -> { trial8list = saveTrialToList() }
        }
    }


    fun combineList(): ArrayList<List<Float>> {
        return arrayListOf(
            trial1list,
            trial2list,
            trial3list,
            trial4list,
            trial5list,
            trial6list,
            trial7list,
            trial8list
        )
    }  //這邊的list 最後要清掉

    // 將List排進Buffer (還沒加上標)
    fun arrangeData(TargetStringBuffer:StringBuffer):StringBuffer{
        for(index in 0..7){  //選一個trial
            TargetStringBuffer.append(index + 1) //trial
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][0])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][1])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][2])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][3])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][4])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][5])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][6])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][7])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][8])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][9])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][10])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][11])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append(arrayListOf8Trial[index][12])
            TargetStringBuffer.append(",")
            TargetStringBuffer.append("\r\n")
        }
        return TargetStringBuffer
    }


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
    }


    var scoreListForDisplay =  listOf<Float>(0f, 0f, 0f, 0f, 0f)
    //update when buttonPressed 5 times
    //clear the List After Display   // scoreListForDisplay =  (0f, 0f, 0f, 0f, 0f)

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
                        "Relative Error AP: " + inputScoreList[0].toString() + " " + performanceDescriptionAP + "\n" +
                        "Relative Error ML: " + inputScoreList[1].toString() + " " + performanceDescriptionML + "\n" +
                        "Absolute Error AP: " + inputScoreList[2].toString() + "\n" +
                        "Absolute Error ML: " + inputScoreList[3].toString() + "\n" +
                        "Absolute Error T2R: "+ inputScoreList[4].toString() )
                Score.text = modifyString
            }
            0 -> {
                val modifyString:String = (  "Score" + "\n"+
                        "Relative Error AP: " + "" + "\n"+
                        "Relative Error ML: " + "" + "\n"+
                        "Absolute Error AP: " + "" + "\n"+
                        "Absolute Error ML: " + "" + "\n"+
                        "Absolute Error T2R: "+ "" )
                Score.text = modifyString
            }
        }
    }

    fun clearScoreList(){
        scoreListForDisplay = listOf<Float>(0f, 0f, 0f, 0f, 0f)
    }



    fun savePracticePerformanceToCSV() {
        //call 整理8trialData
        arrayListOf8Trial = combineList()
        //call function 將List排進buffer
        arrangeData(positionData)
        //切割buffer
        val outputPositionData = positionData.toString().replace("\r", "").split("\n")
        //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
        val outputFileName = "Practice_Performance_$practiceTime.csv"
        // 存檔: name,List,flag
        outputCsv(outputFileName, outputPositionData , 0)
    }



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
        Toast.makeText(activity, "練習題測驗表現儲存成功", Toast.LENGTH_SHORT)
        Log.d("data","outCSV Success")
    }  // sample from HW


    fun checkTime() {
        // 找到關聯的view
        val recordingButton = requireView().findViewById<Button>(R.id.practice_record_position)
        val text1 = requireView().findViewById<TextView>(R.id.text1)
        // hide button
        recordingButton.visibility = View.INVISIBLE

        //確認是否需要倒數  millisInFuture
        var millisInFuture: Long = 4000
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


    // 清除所有參數!，還沒寫完!
    fun clearRecord() {
        clearCurrentTrialRecord() // 四個位置、startX/Y的全域變數
        practiceTrialsCount = 0   //進入測驗練習後的練習次數
        currentTrial = 1
        reset8Trial()             // 清除所有trial的紀錄
        positionData.setLength(0) //clean buffer
    }


    fun checkPracticeLimit() {
        if (practiceTrialsCount >= 2) {  // practiceTrialsCount > MAX_PRACTICE_TRIAL
            practiceTime++  //增加練習次數
            binding.viewModel!!.setPracticeTime(practiceTime) //更新總練習次數

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.practice_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.practice_dialog_message)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                .setNegativeButton(getString(R.string.practice_dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                    savePracticePerformanceToCSV()//儲存測驗表現
                    clearRecord()  // 清除測驗表現>> 還沒寫完
                    Toast.makeText(requireContext(), "再試一次", Toast.LENGTH_SHORT)
                }
                .setPositiveButton(getString(R.string.practice_dialog_back_to_menu)) { _, _ ->
                    savePracticePerformanceToCSV()// 儲存測驗表現
                    clearRecord()  // 清除測驗表現>> 還沒寫完
                    goBackToMenu() // 前往測驗選單
                }
                .show() //creates and then displays the alert dialog.
        }
    }


    fun goBackToMenu() {
        Toast.makeText(activity, "回到測驗選單", Toast.LENGTH_SHORT)
        findNavController().navigate(R.id.action_practiceFragment_to_testMenuFragment)
    }






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        //val fragmentBinding = FragmentPracticeBinding.inflate(inflater, container, false)
        //binding = fragmentBinding
        //return fragmentBinding.root
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_practice, container, false)
        //binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            practiceFragment = this@PracticeFragment //使用listenser binding，用UI button 在xml中設定onclick

            maxPracticeTrial = MAX_PRACTICE_TRIAL //用於更新練習次數文字
        }


        val currentPosition = requireView().findViewById<TextView>(R.id.current_position_field)
        val touchBoard = requireView().findViewById(R.id.view) as TouchBoard

        //0824可以讀到即時觸碰位置
        touchBoard.setOnTouchListener(View.OnTouchListener { v, event ->
            currentPosition.text = ("Current Position: X= $startX ,Y= $startY")
            //邏輯寫在這裡
            false
        })



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
            checkPracticeLimit()       //檢查是否達到練習次數
            buttonPressedCountsInATrial = 0
        }
        return
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

    @SuppressLint("SetTextI18n")
    fun changeText() {
        // 找到測驗表現textView
        val start = requireView().findViewById<TextView>(R.id.performance_start_position)
        val test = requireView().findViewById<TextView>(R.id.performance_test_position)
        val rest = requireView().findViewById<TextView>(R.id.performance_rest_position)
        val response = requireView().findViewById<TextView>(R.id.performance_response_position)
        //測驗次數textView
        val practiceCount = requireView().findViewById<TextView>(R.id.practice_count)
        //找到指導語textView
        val instructionText = requireView().findViewById<TextView>(R.id.instruction_demonstration)
        //找到測驗按鈕
        val recordingButton = requireView().findViewById<Button>(R.id.practice_record_position)
        //顯示已完成練習次數
        practiceCount.text = "練習次數: $currentTrial / $MAX_PRACTICE_TRIAL"

        //判斷測驗情境，並更新對應的Text
        when (condition) {
            "Start Position" -> {
                start.text = "Start Position：X= $startPositionX ; Y= $startPositionY"
                instructionText.text = "將受試者的手指或筆移動到'目標位置'上，確認停止後按下紀錄"
            }
            "Test Position" -> {
                test.text = "Test Position：X= $testPositionX ; Y= $testPositionY"
                instructionText.text = "將受試者的手指或筆移動回'預備位置'上，確認停止後按下紀錄"
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
                instructionText.text = "請將受試者手指或筆尖放在下方'預備位置'上，確認停止後按下紀錄"
                recordingButton.text = getString(R.string.record_position)
                recordingButton.textSize = 30.toFloat()
            }
        }
    }

} // fragment end


