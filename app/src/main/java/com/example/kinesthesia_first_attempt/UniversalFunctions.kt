package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

// 此Class目前暫時空白，如有資料需要pass給xml，可以放入這邊
class UniversalFunctions: AppCompatActivity() {
}


@SuppressLint("StaticFieldLeak") lateinit var mContextKIN: Context


//全域變數宣告，不然無法讀取到class給的資料
var inAirData = StringBuffer()     //new: inair檔案暫存處
var systemTimestamp: Long = 0      //new: 時間 用於存inair
var heightZ: Float = 0f            //new: 筆在z軸高度
var tipPressure: Float = 0f        //new: 筆尖壓力，用於後續分析筆是否在平板上以利裁切資料

var startX: Float = 0f
var startY: Float = 0f

var bb: Float = 0f
var b1: Float = 0f
var b2: Float = 0f
//全域變數宣告，不然無法讀取到class給的資料

// 常數定義區段 : 此段落放不會改變的參數
// 例如總測驗次數/練習次數等等，方格大小、等等
const val TARGET_BOX_SIZE = 2.00                   //待修改
const val RESPONSE_DOT_SIZE = 3.00
// 最大練習次數 或每個方向的測驗次數
const val MAX_PRACTICE_TRIAL = 8
const val MAX_FORMAL_TRIAL = 5


//測驗流程

// 記錄當下手指位置，並管理測驗流程
// recordPosition功能描述
//// 1.將XY存入對應情境的變數中
//// 2.log.d中印出對應情境的資料
var trialCondition = listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")
var condition: String = ""

//測驗選項
lateinit var testCondition: String  //進到各分頁後 重新宣告
//var testCondition: String = "Practice"
val testConditionList = listOf<String>("Practice","Formal")

//練習測驗次數上限變數
// trialCount
var maxTrailDesire: Int = 8
val practiceTrialCountList = arrayListOf<String>("8", "7", "6", "5", "4", "3", "2", "1")

//測驗方式
var currentTestContext: String = ""
val contextList = arrayListOf<String>("請選情境", "Finger", "Pen")


//測驗相關變數宣告
var buttonPressedCountsInATrial: Int = 0  //按鈕次數
var practiceTrialsCount: Int = 0  //已練習次數
var trialCount: Int = 0
var currentTrial: Int = 1  //當前Trial
var practiceTime: Int = 0

// 測驗表現
var startPositionX: Float = 0.0f
var startPositionY: Float = 0.0f
var testPositionX: Float = 0.0f
var testPositionY: Float = 0.0f
var restPositionX: Float = 0.0f
var restPositionY: Float = 0.0f
var responsePositionX: Float = 0.0f
var responsePositionY: Float = 0.0f


// 存檔相關變數宣告
val positionData = StringBuffer()

//顯示表現用暫存LIST
var scoreListForDisplay = listOf<Float>(0f, 0f, 0f, 0f, 0f)

lateinit var arrayListOf8Trial: ArrayList<List<Float>>

//Todo:11/22這邊的LIST 之後都要新增 四欄，放 given target / target XY)
var trial1list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f) //四次表現(X/Y) + 5個表現參數  = 13
var trial2list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial3list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial4list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial5list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial6list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial7list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial8list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)



// 宣告位置調整參考資料：https://medium.com/globant/why-oncreate-of-activity-and-oncreateview-of-fragment-is-not-needed-anymore-6cdfc331102
// 11/15 View 相關宣告測試，嘗試避免重複宣告 requireView().findViewById
@SuppressLint("StaticFieldLeak") lateinit var  start: TextView
@SuppressLint("StaticFieldLeak") lateinit var  test: TextView
@SuppressLint("StaticFieldLeak") lateinit var  rest: TextView
@SuppressLint("StaticFieldLeak") lateinit var  response: TextView
@SuppressLint("StaticFieldLeak") lateinit var  trialCountView: TextView     //測驗次數textView
@SuppressLint("StaticFieldLeak") lateinit var  recordingButton: Button        //找到測驗按鈕
@SuppressLint("StaticFieldLeak") lateinit var  instructionText: TextView      //找到指導語textView
@SuppressLint("StaticFieldLeak") lateinit var trialInputSpinner: Spinner
@SuppressLint("StaticFieldLeak") lateinit var contextSpinner: Spinner

//// Todo: 此段重要，為測驗方向和目標的View宣告，正式測驗中，需要新增斜向箭頭
@SuppressLint("StaticFieldLeak") lateinit var fingerTarget: ImageView
@SuppressLint("StaticFieldLeak") lateinit var fingerStartPoint: ImageView
@SuppressLint("StaticFieldLeak") lateinit var fingerDownArrow: ImageView

@SuppressLint("StaticFieldLeak") lateinit var penTarget: ImageView
@SuppressLint("StaticFieldLeak") lateinit var penStartPoint: ImageView
@SuppressLint("StaticFieldLeak") lateinit var penDownArrow: ImageView
//// Todo: 此段重要，為測驗方向和目標的View宣告，正式測驗中，需要新增斜向箭頭

//// 11/17 將原本onViewCreated中 沒有提前宣告的view 抓出來，減少後續需要呼叫時要重複call的問題
@SuppressLint("StaticFieldLeak") lateinit var currentPosition: TextView
@SuppressLint("StaticFieldLeak") lateinit var inAirText: TextView
@SuppressLint("StaticFieldLeak") lateinit var touchBoard: TouchBoard
@SuppressLint("StaticFieldLeak") lateinit var Score: TextView
//// 原本 checktime 中沒有提前宣告的view
@SuppressLint("StaticFieldLeak") lateinit var countAndHint: TextView
////manageVisibility 中沒有提前宣告的view
@SuppressLint("StaticFieldLeak") lateinit var selectButton:Button
@SuppressLint("StaticFieldLeak") lateinit var randomTargetView: ImageView
////
@SuppressLint("StaticFieldLeak") lateinit var navControllerKIN: NavController    //必須，用於從public function呼叫navControllerKIN








//以下嘗試放通用的function，供各個fragment使用

//val dateOptions = getPickupOptions()
fun getPickupOptions(): List<String> {
    val options = mutableListOf<String>()
    val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
    val calendar = Calendar.getInstance()

    repeat(4) {
        options.add(formatter.format(calendar.time))
        calendar.add(Calendar.DATE, 1)
    }
    return options
}

// 以下為隱藏/顯示選單用的功能
//參考資料 : https://stackoverflow.com/questions/37380587/android-how-to-hide-the-system-ui-properly
fun u_hideSystemUI(decorView:View) {
    //val decorView = requireActivity().window.decorView
    val uiOptions = decorView.systemUiVisibility
    var newUiOptions = uiOptions
    newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
    newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
    newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
    newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    decorView.systemUiVisibility = newUiOptions
}
fun u_showSystemUI(decorView:View) {
    //val decorView = requireActivity().window.decorView
    val uiOptions = decorView.systemUiVisibility
    var newUiOptions = uiOptions
    newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
    newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
    newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
    newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE.inv()
    newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
    decorView.systemUiVisibility = newUiOptions
}
// 以上為隱藏/顯示選單用的功能




//以下InAir相關

fun u_changeInAriText() {
    @SuppressLint("SetTextI18n")
    currentPosition.text =
        ("目前位置：X= " + String.format("%.2f", startX) + ",Y= " + String.format("%.2f", startY))
    @SuppressLint("SetTextI18n")
    inAirText.text =
        ("目前InAir :" + "\n" +
                "timeStamp = $systemTimestamp" + "\n" +
                "Z = $heightZ " + "\n" +
                "tipPressure = $tipPressure")
}


fun u_clearInAir() {
    systemTimestamp = 0  //
    heightZ = 0f
    tipPressure = 0f
    inAirData.setLength(0)
}  //歸零inair相關參數

fun u_arrangeInAirData() {
    inAirData.append(systemTimestamp)
    inAirData.append(",")
    inAirData.append(startX)
    inAirData.append(",")
    inAirData.append(startY)
    inAirData.append(",")
    inAirData.append(heightZ)
    inAirData.append(",")
    inAirData.append(tipPressure)
    inAirData.append("\r\n")
}

fun u_saveInAirDataToCSV(inAirData:StringBuffer) {
    val outputInAirData = inAirData.toString().replace("\r", "").split("\n")
    //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
    val outputFileName = "{$testCondition}_InAir_Trial_$currentTrial.csv"
    // 存檔: name,List,flag
    // Todo: 新增判斷式，若沒有檔名輸入，則存成TESTING
    u_outputInAirCsv(outputFileName, outputInAirData,0)
}

fun u_outputInAirCsv(fileName: String, input: List<String>, flag: Int) {
    //檔案路徑: 目前直接讀在demographic的全域變數，有error再讀viewModel備用
    //val outputPath = binding.viewModel!!.outputFilePath.toString()  //讀取存在viewModel的路徑
    val output = StringBuffer()//必需

    val titleList = listOf(
        "Time Stamp(ms)", "X(pixel)", "Y(pixel)", "Z(0~100)", "Tip pressure"
    )

    // 放入標題，使用迴圈，避免前後出現[]
    for (i in 0..4) {
        output.append(titleList[i])
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
    Toast.makeText( mContextKIN, "{$testCondition}_InAir_Trial_$currentTrial 儲存成功", Toast.LENGTH_SHORT).show()
    //Log.d("data", "outCSV Success")
}  // sample from HW
//以上InAir相關


//以下 CSV & 存檔相關

fun u_combineList(): ArrayList<List<Float>> {
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
}

fun u_arrangeData(TargetStringBuffer: StringBuffer): StringBuffer {
    for (index in 0..7) {  //選一個trial
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

fun u_savePracticePerformanceToCSV() {
    //call 整理8trialData
    arrayListOf8Trial = u_combineList()
    //call function 將List排進buffer
    u_arrangeData(positionData)
    //切割buffer
    val outputPositionData = positionData.toString().replace("\r", "").split("\n")
    //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
    // val outputFileName = "Practice_Performance_$practiceTime.csv"
    val outputFileName = "{$testCondition}_Performance_$practiceTime.csv"
    // 存檔: name,List,flag
    u_outputCsv(outputFileName, outputPositionData, 0)
}

fun u_outputCsv(fileName: String, input: List<String>, flag: Int) {
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
    for (i in 0..13) {
        output.append(allTitle[i])
        output.append(",")
    }
    output.append("\r\n")

    //輸入後 再次排列
    for (u in input) {  //這邊需要測試看outPut結果
        output.append(u)
        output.append("\r\n")
    }

    // TODO: 需要新增沒有檔名時的判斷式，直接存在固定的資料夾
    val file = File(filePathStr, fileName)
    val os = FileOutputStream(file, true)   // 這邊給的字串要有檔案類型
    os.write(output.toString().toByteArray())
    os.flush()
    os.close()
    output.setLength(0) //clean buffer
    Toast.makeText( mContextKIN, "練習題測驗表現儲存成功", Toast.LENGTH_SHORT).show()
    Log.d("data", "outCSV Success")
}  // sample from HW

//以上 CSV存檔相關










//以下測驗流程管理相關
// Todo: calculateTrialScoreP(); V 之後還要改輸入 要考量given position
// Todo:  checkPracticeLimit() V;
// Todo:  以及正式測驗中，隨機位置的function們

fun u_pressButton() {
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

        //TODO: 確認這邊u_checkPracticeLimit運作正常後，把xml中pressbutton改為 u_pressButton

        u_checkPracticeLimit()       //檢查是否達到練習次數
        buttonPressedCountsInATrial = 0
    }
    return
}





fun u_addTrialsCount(){
    trialCount ++
    practiceTrialsCount++
    currentTrial++
}

fun u_checkTime() {
    // 找到關聯的view
    //val recordingButton = requireView().findViewById<Button>(R.id.record_position)
    //val text1 = requireView().findViewById<TextView>(R.id.text1)
    // hide button
    recordingButton.visibility = View.INVISIBLE

    //確認是否需要倒數  millisInFuture
    var millisInFuture: Long
    millisInFuture = 1000

    when (buttonPressedCountsInATrial){
        0 -> { millisInFuture = 1000 }  //之後要看按鈕次數改回4000
        1 -> { millisInFuture = 1000 }
        2 -> { millisInFuture = 1000 }
        3 -> { millisInFuture = 1000 }
        4 -> { millisInFuture = 1000 }
        5 -> { millisInFuture = 0 }
    }

    //計時器宣告
    val timer = object : CountDownTimer(millisInFuture, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            countAndHint.text = "seconds remaining: " + millisUntilFinished / 1000
        }

        // 時間到時將會執行的任務
        override fun onFinish() {

            when (buttonPressedCountsInATrial) {
                0 -> {
                    countAndHint.text = "Get Ready!"
                }
                1 -> {
                    countAndHint.text = "Time is up! Do Next Step!"
                }
                2 -> {
                    countAndHint.text = "Time is up! Do Next Step!"
                }
                3 -> {
                    countAndHint.text = "Time is up! Do Next Step!"
                }
                4 -> {
                    countAndHint.text = "Click to Save this Trial"
                }
                5 -> {
                    countAndHint.text = "Get Ready!"
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

fun u_confirmSelection() {
    // TODO: 11/21 正式測驗還包含其他判斷式，需要新增
    // TODO: 11/21 此function 由XML中 運用 fragment binding呼叫，要到每一個對應的xml中，匯入 UniversalFunction

    Toast.makeText(mContextKIN , "開始測驗練習", Toast.LENGTH_SHORT).show()
//    u_changeText(currentTrial,maxTrailDesire,condition,trialCountView,instructionText,
//        start,test,rest,response,
//        recordingButton)
    u_changeText()

    u_manageVisibility(0)
//    u_manageVisibility(0,trialCountView,
//        recordingButton,
//        trialInputSpinner,
//        contextSpinner,
//        touchBoard,
//        Score,
//        countAndHint,
//        selectButton,
//        randomTargetView)
}


// 此函式不改成 global，維持原local，但把內部其他funtion改為global

//TODO: 確認u_checkPracticeLimit這邊是否可以用getString，或要改其他方法

@SuppressLint("SetTextI18n")
fun u_checkPracticeLimit() {
    if (practiceTrialsCount >= maxTrailDesire) {  // practiceTrialsCount > MAX_PRACTICE_TRIAL
        practiceTime++  //增加練習完成次數
        updatePracticeTimeToViewModel()

//        MaterialAlertDialogBuilder(mContextKIN)
//            .setTitle(getString(R.string.practice_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
//            .setMessage(getString(R.string.practice_dialog_message)) //Set the message to show the data
//            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
//
//            .setNegativeButton(getString(R.string.practice_dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
//                u_savePracticePerformanceToCSV()//儲存測驗表現
//                u_clearRecord()  // 清除測驗表現>> 還沒寫完
//                trialCountView.text = "練習次數: $currentTrial / $maxTrailDesire"
//                u_manageVisibility(1)
//                Toast.makeText(mContextKIN, "再試一次", Toast.LENGTH_SHORT).show()
//            }
//            .setPositiveButton(getString(R.string.practice_dialog_back_to_menu)) { _, _ ->
//                u_savePracticePerformanceToCSV()// 儲存測驗表現
//                u_clearRecord()  // 清除測驗表現>> 還沒寫完
//                u_goBackToMenu()// 前往測驗選單 ( 維持local) >>可以寫判斷式　改成when根據測驗頁面，換指令　
//            }
//            .show() //creates and then displays the alert dialog.
    }
}


fun u_goBackToMenu() {
    Toast.makeText(mContextKIN, "回到測驗選單", Toast.LENGTH_SHORT).show()
     //TODO: 後續 需要根據各情境，調整判斷式，才能正確回到頁面

    when (testCondition){
        "Practice" -> {
            navControllerKIN.navigate(com.example.kinesthesia_first_attempt.R.id.action_practiceFragment_to_testMenuFragment)
        }

        }

}






//以下位置記錄相關
// TODO: 注意後面所有 位置紀錄 & 檔案輸出，都要更新這四個參數
// TODO: 要新增 trueGivenStartPositionX & trueGivenStartPositionY
// TODO: 要新增 trueGivenTargetPositionX & trueGivenTargetPositionY
// TODO: 下面所有的 position 應該要新整成一個 float list


// 此函數只處理紀錄位置，不處理位置的隨機分配 & 目標位置給定，不給輸入，直接取用全域變數
fun u_recordPosition() {
    when (buttonPressedCountsInATrial) {
        1 -> {
            condition = trialCondition[0]
            startPositionX = startX
            startPositionY = startY
            Log.d(condition, "$startPositionX  $startPositionY ")
        }
        2 -> {
            condition = trialCondition[1]
            testPositionX = startX
            testPositionY = startY
            Log.d(condition, "$testPositionX  $testPositionY ")
        }
        3 -> {
            condition = trialCondition[2]
            restPositionX = startX
            restPositionY = startY
            Log.d(condition, "$restPositionX  $restPositionY  ")
        }
        4 -> {
            condition = trialCondition[3]
            responsePositionX = startX
            responsePositionY = startY
            Log.d(condition, "$responsePositionX  $responsePositionY  ")
        }
        5 -> {
            condition = ""
            startX = 0.0f
            startY = 0.0f
        }
    }
}



fun u_displayScoreInText(inputScoreList: List<Float>, flag: Int, Score:TextView) {
    //val Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score)

    var performanceDescriptionAP: String = ""  //Y軸
    var performanceDescriptionML: String = ""  //X軸
    when {
        inputScoreList[0] < 0 -> {
            //performanceDescriptionAP = "Underestimated"
            performanceDescriptionAP = "少於指定目標位置"
        }
        inputScoreList[0] > 0 -> {
            //performanceDescriptionAP = "Overestimated"
            performanceDescriptionAP = "多於指定目標位置"
        }
        inputScoreList[0] == 0f -> {
            //performanceDescriptionAP = "Perfect Matched"
            performanceDescriptionAP = "等於指定目標位置"
        }
    }  //performanceDescriptionAP
    when {
        inputScoreList[1] > 0 -> {
            //performanceDescriptionML = "Right Deviated"
            performanceDescriptionML = "右偏指定目標位置"
        }
        inputScoreList[1] < 0 -> {
            //performanceDescriptionML = "Left  Deviated"
            performanceDescriptionML = "左偏指定目標位置"
        }
        inputScoreList[1] == 0f -> {
            //performanceDescriptionML = "Perfect Matched"
            performanceDescriptionML = "等於指定目標位置"
        }
    }  //performanceDescriptionML
    when (flag) {
        1 -> {
            /*
            val modifyString: String = ("Score" + "\n" +
                    "Anterior-Posterior: " + performanceDescriptionAP + "\n" +
                    "Medial - Lateral: " + performanceDescriptionML + "\n" +
                    "Relative Error  AP: " + inputScoreList[0].toString() + "\n" +
                    "Relative Error  ML: " + inputScoreList[1].toString() + "\n" +
                    "Absolute Error  AP: " + inputScoreList[2].toString() + "\n" +
                    "Absolute Error  ML: " + inputScoreList[3].toString() + "\n" +
                    "Absolute Error T2R: " + inputScoreList[4].toString())
             */
            val modifyString: String = ("表現概述" + "\n" +
                    "整體誤差距離: " + String.format("%.2f", inputScoreList[4]) + "\n" +
                    "前後方向表現: " + performanceDescriptionAP + "\n" +
                    "左右方向表現: " + performanceDescriptionML + "\n" +
                    "\n" +
                    "詳細分數" + "\n" +
                    "Relative Error  AP: " + String.format("%.2f", inputScoreList[0]) + "\n" +
                    "Relative Error  ML: " + String.format("%.2f", inputScoreList[1]) + "\n" +
                    "Absolute Error  AP: " + String.format("%.2f", inputScoreList[2]) + "\n" +
                    "Absolute Error  ML: " + String.format("%.2f", inputScoreList[3]) + "\n"
                    )
            Score.text = modifyString
        }
        0 -> {
            /*  val modifyString: String = ("Score" + "\n" +
                      "Anterior-Posterior: " + "" + "\n" +
                      "Medial - Lateral: " + "" + "\n" +
                      "Relative Error  AP: " + "" + "\n" +
                      "Relative Error  ML: " + "" + "\n" +
                      "Absolute Error  AP: " + "" + "\n" +
                      "Absolute Error  ML: " + "" + "\n" +
                      "Absolute Error T2R: " + "")*/
            val modifyString: String = ("表現概述" + "\n" +
                    "整體誤差距離: " + "" + "\n" +
                    "前後方向表現: " + "" + "\n" +
                    "左右方向表現: " + "" + "\n" +
                    "\n" +
                    "詳細分數" + "\n" +
                    "Relative Error  AP: " + "" + "\n" +
                    "Relative Error  ML: " + "" + "\n" +
                    "Absolute Error  AP: " + "" + "\n" +
                    "Absolute Error  ML: " + "" + "\n"
                    )
            Score.text = modifyString
        }
    }
}  //用於描述測驗表現


fun u_clearCurrentTrialRecord(
) {
    //Todo: 這邊的數值 (var / val)有問題，要確認
    startPositionX = 0.0f
    startPositionY = 0.0f
    testPositionX = 0.0f
    testPositionY = 0.0f
    restPositionX = 0.0f
    restPositionY = 0.0f
    responsePositionX = 0.0f
    responsePositionY = 0.0f
    condition = ""
    startX = 0.0f
    startY = 0.0f
}


fun u_reset8Trial() {
    for (n in 0..7) {
        arrayListOf8Trial[n] = listOf<Float>(0.0f,0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
    }
}

fun u_clearRecord() {
    u_clearCurrentTrialRecord() // 四個位置、startX/Y的全域變數
    trialCount = 0
    practiceTrialsCount = 0   //進入測驗練習後的練習次數
    currentTrial = 1
    u_reset8Trial()             // 清除所有trial的紀錄
    positionData.setLength(0) //clean buffer
}




// 將單次反應存入LIST
fun u_saveCurrentTrialRecord() {
    //確認目前practiceTrialsCount
    when (practiceTrialsCount) {
        1 -> {
            trial1list = u_saveTrialToList()
        }
        2 -> {
            trial2list = u_saveTrialToList()
        }
        3 -> {
            trial3list = u_saveTrialToList()
        }
        4 -> {
            trial4list = u_saveTrialToList()
        }
        5 -> {
            trial5list = u_saveTrialToList()
        }
        6 -> {
            trial6list = u_saveTrialToList()
        }
        7 -> {
            trial7list = u_saveTrialToList()
        }
        8 -> {
            trial8list = u_saveTrialToList()
        }
    }
}

fun u_saveTrialToList(): List<Float> {
    val tempScore = u_calculateTrialScoreP()  // 11/21 global
    //val tempScore = calculateTrialScoreP()  //計算當前trial表現Error
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



//Todo: 存檔參數的長度要調整 trial1list (1-8)
//Todo: 存檔excel輸出的標題要調整 新增四欄

//Todo:此為11/11新增的fun，需要再新增True Start & Target
// 1-8為受試者實際平板上觸摸的點
// 9-12為程式給定的座標點
// 這9-10座標位置，要從前面的程式碼抓出來
fun u_comineRawDataToFloatList(startPositionX: Float, startPositionY: Float,
                               testPositionX: Float, testPositionY: Float,
                               restPositionX: Float, restPositionY: Float,
                               responsePositionX: Float, responsePositionY: Float,
                               trueGivenStartPositionX: Float,trueGivenStartPositionY: Float,
                               trueGivenTargetPositionX: Float,trueGivenTargetPositionY: Float
                               ): List<Float> {
    return listOf<Float>(
        startPositionX,
        startPositionY,
        testPositionX,
        testPositionY,
        restPositionX,
        restPositionY,
        responsePositionX,
        responsePositionY,
        trueGivenStartPositionX,
        trueGivenStartPositionY,
        trueGivenTargetPositionX,
        trueGivenTargetPositionY
    )
}

fun u_saveTrialToList(positionList:FloatArray): List<Float> {
    var startPositionX = positionList[1]
    var startPositionY = positionList[2]
    var testPositionX = positionList[3]
    var testPositionY = positionList[4]
    var restPositionX = positionList[5]
    var restPositionY = positionList[6]
    var responsePositionX = positionList[7]
    var responsePositionY = positionList[8]
    var trueGivenStartPositionX = positionList[9]
    var trueGivenStartPositionY = positionList[10]
    var trueGivenTargetPositionX = positionList[11]
    var trueGivenTargetPositionY = positionList[12]

    val tempScore = u_calculateTrialScoreP()  //計算當前trial表現Error
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
        tempScore[4],   //AE T2R
        trueGivenStartPositionX,
        trueGivenStartPositionY,
        trueGivenTargetPositionX,
        trueGivenTargetPositionY
    )
}

fun u_calculateTrialScoreP(): List<Float> {
    //Todo: 11/11 這邊的分數計算，可能要重新調整(given position)，因為起點/目標的給法不同
    //Todo: 11/21 也要新增 true given position 的輸入/
  //var trueGivenStartPositionX = positionList[9]
  //var trueGivenStartPositionY = positionList[10]
  //var trueGivenTargetPositionX = positionList[11]
  //var trueGivenTargetPositionY = positionList[12]
    //計算向量: 以startPosition為基準/原點
    //Vector( test2Response ) = Vector( start2Response ) - Vector( start2Test )
    //start2Test(x,y) = Test(x,y) - Start(x,y)
    var start2TestX = testPositionX - startPositionX
    var start2TestY = testPositionY - startPositionY

    //start2Response(x,y) = Response(x,y) - Start(x,y)
    var start2ResponseX = responsePositionX - startPositionX
    var start2ResponseY = responsePositionY - startPositionY

    //test2Response
    var test2ResponseAP = start2ResponseY - start2TestY
    var test2ResponseML = start2ResponseX - start2TestX


    //Absolute Error: AP、ML、T2R(AP^2+ML^2)^1/2
    var absoluteErrorAP = kotlin.math.abs(test2ResponseAP)
    var absoluteErrorML = kotlin.math.abs(test2ResponseML)

    //Relative Error: AP、ML
    var relativeErrorAP = test2ResponseAP
    var relativeErrorML = test2ResponseML

    //1105校正relativeError正負號
    when {
        test2ResponseAP  < 0 -> {
            //performanceDescriptionAP = "Underestimated"
            relativeErrorAP = -absoluteErrorAP
        }
        test2ResponseAP  > 0 -> {
            //performanceDescriptionAP = "Overestimated"
            relativeErrorAP = absoluteErrorAP
        }
        test2ResponseAP  == 0f -> {
            //performanceDescriptionAP = "Perfect Matched"
            relativeErrorAP = absoluteErrorAP
        }
    }

    when {
        test2ResponseML < 0 -> {
            //performanceDescriptionML = "Right Deviated"
            relativeErrorML = absoluteErrorML
        }
        test2ResponseML > 0 -> {
            //performanceDescriptionML = "Left  Deviated"
            relativeErrorML = -absoluteErrorML
        }
        test2ResponseML == 0f -> {
            //performanceDescriptionML = "Perfect Matched"
            relativeErrorML = absoluteErrorML
        }
    }

    val a = test2ResponseAP.toDouble()
    val b = test2ResponseML.toDouble()
    val c: Double = sqrt(a * a + b * b)
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

fun u_clearScoreList() {
    scoreListForDisplay = listOf<Float>(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
} //清除用來顯示的測驗表現分數List


// View管理相關
@SuppressLint("SetTextI18n")
fun u_changeText() {

    trialCountView.text = "測驗次數: $currentTrial / $maxTrailDesire"


    // Todo: 指導語部分之後可以改到 Rstring中，也記得要改成新的指導語 ( VAP2AP, AP2AP, PP2AP)
    val instructionList = arrayListOf(
        "施測者將受試者的手指或握著的筆尖，" + "\n" + "移動至 預備位置 上，" + "\n" + "確認動作停止後按下紀錄。",
        "施測者將受試者的手指或握著的筆尖，" + "\n" + "移動到 目標位置 上，" + "\n" + "確認動作停止後按下紀錄。",
        "施測者將受試者的手指或握著的筆尖，" + "\n" + "移動回 預備位置 上，" + "\n" + "確認動作停止後按下紀錄。",
        "受試者聽到嗶聲後將手指或握著的筆，" + "\n" + "移動到所記得的位置，" + "\n" + "確認動作停止後按下紀錄。",
        "施測者將受試者的手指或握著的筆尖，" + "\n" + "移動到平板外的桌面上，" + "\n" + "確認資料正確後按下Save Trial。"
    )

    //判斷測驗情境，並更新對應的Text
    when (condition) {
        "Start Position" -> {
            start.text = "起始位置：X= " + String.format("%.2f", startX) + ",Y= " + String.format(
                "%.2f",
                startY
            )
            instructionText.text = instructionList[1]
        }
        "Test Position" -> {
            test.text = "目標位置：X= " + String.format("%.2f", startX) + ",Y= " + String.format(
                "%.2f",
                startY
            )
            instructionText.text = instructionList[2]
        }
        "Rest Position" -> {
            rest.text = "預備位置：X= " + String.format("%.2f", startX) + ",Y= " + String.format(
                "%.2f",
                startY
            )
            instructionText.text = instructionList[3]
        }
        "Response Position" -> {
            response.text = "反應位置：X= " + String.format("%.2f", startX) + ",Y= " + String.format(
                "%.2f",
                startY
            )
            //recordingButton.text = getString(R.string.next_trial)
            recordingButton.text = "Save Trial"

            recordingButton.textSize = 24.toFloat()
            instructionText.text = instructionList[4]
        }
        "" -> {
            start.text = "起始位置："
            test.text = "目標位置："
            rest.text = "預備位置："
            response.text = "反應位置："
            //recordingButton.text = getString(R.string.record_position)
            recordingButton.text = "紀錄位置"

            recordingButton.textSize = 30.toFloat()

            instructionText.text = instructionList[0]
        }
        // getString reference >>導致閃退，棄用
        // https://stackoverflow.com/questions/4253328/getstring-outside-of-a-context-or-activity
    }
}

fun u_launchTrialInputSpinner() {
    //mContext_demo = requireActivity().applicationContext
    //trialInputSpinner = requireView()!!.findViewById<View>(R.id.trialInput_list) as Spinner
    val adapter = ArrayAdapter.createFromResource(
        mContextKIN,
        R.array.practice_trial_count_list,
        android.R.layout.simple_spinner_dropdown_item
    )
    trialInputSpinner.adapter = adapter
    trialInputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            u_setTrialLimit(practiceTrialCountList[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //TODO("Not yet implemented")
        }
    }
}

fun u_setTrialLimit(trialLimitInput: String) {
    maxTrailDesire = trialLimitInput.toInt()
    }  //可直接移植到補測

fun u_launchContextSpinner() {
    //mContext_demo = requireActivity().applicationContext
    //contextSpinner = requireView()!!.findViewById<View>(R.id.context_list) as Spinner
    val adapter = ArrayAdapter.createFromResource(
        mContextKIN,
        R.array.context_list,
        android.R.layout.simple_spinner_dropdown_item
    )
    contextSpinner.adapter = adapter
    contextSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            u_setContext(contextList[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //TODO("Not yet implemented")
        }
    }
}

fun u_setContext(contextInput: String) {
    currentTestContext = contextInput
    when (contextInput) {
        "請選情境" -> {
            Toast.makeText(mContextKIN, "請選擇測驗情境", Toast.LENGTH_SHORT).show()
        }
        "Finger" -> {
        }
        "Pen" -> {
        }
    }
    u_checkContextAndLaunchView(currentTestContext) //更換ImageView宣告內容
}


// Todo, 此function 到正式測驗後，要寫新的，可以多放斜向箭頭
fun u_checkContextAndLaunchView(context: String) {

    var tempContext = context
    when (context) {
        "請選情境" -> {
            tempContext = "Finger"
        }  //default 顯示Finger
        "" -> {
            tempContext = "Finger"
        } //default 顯示Finger
        "Finger" -> {
            tempContext = "Finger"
        }
        "Pen" -> {
            tempContext = "Pen"
        }
    }
    //清掉前一個情境的view
    when (tempContext) {
        "Pen" -> {
            penTarget.visibility =  View.VISIBLE
            penStartPoint.visibility =  View.VISIBLE
            penDownArrow.visibility =  View.VISIBLE

            fingerTarget.visibility = View.GONE
            fingerStartPoint.visibility = View.GONE
            fingerDownArrow.visibility = View.GONE
        }
        "Finger" -> {
            penTarget.visibility = View.GONE
            penStartPoint.visibility = View.GONE
            penDownArrow.visibility = View.GONE

            fingerTarget.visibility = View.VISIBLE
            fingerStartPoint.visibility  = View.VISIBLE
            fingerDownArrow.visibility  = View.VISIBLE
        }
    }

} //輸入currentContext

fun updatePracticeTimeToViewModel(){
    if (practiceTrialsCount >= maxTrailDesire) {
        MainViewModel().setPracticeTime(practiceTime)
    }
}



fun u_manageVisibility(flag: Int) {

    //Todo: 要新增 是正式測驗或練習題的判斷式，來決定要不要處理該view的顯示 如 randomtarget/trialInputSpinner/
    if (testCondition == "Practice") {
        randomTargetView.visibility = View.INVISIBLE
    } else {
        trialInputSpinner.visibility = View.INVISIBLE
    }


    when (flag) {
        0 -> {
            //顯示測驗進行VIEW
            countAndHint.visibility = View.VISIBLE
            trialCountView.visibility = View.VISIBLE
            recordingButton.visibility = View.VISIBLE
            touchBoard.visibility = View.VISIBLE
            //隱藏方向選擇VIEW
            trialInputSpinner.visibility = View.INVISIBLE
            selectButton.visibility = View.INVISIBLE
            contextSpinner.visibility = View.INVISIBLE
        }

        1 -> {
            //隱藏測驗進行VIEW
            countAndHint.visibility = View.GONE
            trialCountView.visibility = View.GONE
            recordingButton.visibility = View.GONE
            touchBoard.visibility = View.GONE
            //顯示方向選擇VIEW
            trialInputSpinner.visibility = View.VISIBLE
            selectButton.visibility = View.VISIBLE
            contextSpinner.visibility = View.VISIBLE
            //
            Score.text = "Score"
        }
    }
}  //管理測驗相關View顯示、可觸控與否



