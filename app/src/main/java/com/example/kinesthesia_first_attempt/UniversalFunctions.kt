package com.example.kinesthesia_first_attempt

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt



class UniversalFunctions: AppCompatActivity() {



    // 記錄當下手指位置，並管理測驗流程
    // recordPosition功能描述
    // 1.將XY存入對應情境的變數中
    // 2.log.d中印出對應情境的資料
    var trialCondition =
        listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")
    var condition: String = ""

    //測驗次數上限變數
    // trialCount
    var maxTrailDesire: Int = 8
    val practiceTrialCountList = arrayListOf<String>("8", "7", "6", "5", "4", "3", "2", "1")

}





// 常數定義區段 : 此段落放不會改變的參數
// 例如總測驗次數/練習次數等等，方格大小、等等
private const val TARGET_BOX_SIZE = 2.00                   //待修改
private const val RESPONSE_DOT_SIZE = 3.00

// 最大練習次數 或每個方向的測驗次數
const val MAX_PRACTICE_TRIAL = 8
const val MAX_FORMAL_TRIAL = 5


//測驗流程

// 記錄當下手指位置，並管理測驗流程
// recordPosition功能描述
// 1.將XY存入對應情境的變數中
// 2.log.d中印出對應情境的資料
var trialCondition = listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")
var condition: String = ""

//測驗選項
var testCondition: String = "Practice"
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
var startPositionX: Float = 0f
var startPositionY: Float = 0f
var testPositionX: Float = 0f
var testPositionY: Float = 0f
var restPositionX: Float = 0f
var restPositionY: Float = 0f
var responsePositionX: Float = 0f
var responsePositionY: Float = 0f


// 存檔相關變數宣告
val positionData = StringBuffer()

//顯示表現用暫存LIST
var scoreListForDisplay = listOf<Float>(0f, 0f, 0f, 0f, 0f)

lateinit var arrayListOf8Trial: ArrayList<List<Float>>
var trial1list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f) //四次表現(X/Y) + 5個表現參數  = 13
var trial2list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial3list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial4list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial5list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial6list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial7list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial8list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)





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
fun u_clearInAir(inAirData:StringBuffer) {
    systemTimestamp = 0  //
    heightZ = 0f
    tipPressure = 0f
    inAirData.setLength(0)
}  //歸零inair相關參數

fun u_arrangeInAirData(inAirData:StringBuffer) {
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

fun u_saveInAirDataToCSV(testCondition:String,inAirData:StringBuffer,currentTrial:Int,mContext: Context) {
    val outputInAirData = inAirData.toString().replace("\r", "").split("\n")
    //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
    val outputFileName = "{$testCondition}_InAir_Trial_$currentTrial.csv"
    // 存檔: name,List,flag
    // Todo: 新增判斷式，若沒有檔名輸入，則存成TESTING
    u_outputInAirCsv(outputFileName, outputInAirData,0,testCondition,currentTrial,mContext)
}

fun u_outputInAirCsv(fileName: String, input: List<String>, flag: Int,testCondition:String,currentTrial:Int,mContext: Context) {
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
    Toast.makeText( mContext, "{$testCondition}_InAir_Trial_$currentTrial 儲存成功", Toast.LENGTH_SHORT).show()
    //Log.d("data", "outCSV Success")
}  // sample from HW
//以上InAir相關


//以下測驗流程管理相關
fun u_addTrialsCount(u_trialsCount: Int,u_currentTrial: Int ): List<Int>{
    var u_trialsCount = u_trialsCount +1
    var u_currentTrial = u_currentTrial +1
    return listOf<Int>(
        u_trialsCount,u_currentTrial
    )

}



//以下位置記錄相關
// TODO: 注意後面所有 位置紀錄 & 檔案輸出，都要更新這四個參數
// TODO: 要新增 trueGivenStartPositionX & trueGivenStartPositionY
// TODO: 要新增 trueGivenTargetPositionX & trueGivenTargetPositionY
// TODO: 下面所有的 position 應該要新整成一個 float list

// u_clearCurrentTrialRecord(startPositionX,startPositionY,testPositionX,testPositionY,restPositionX,restPositionY,responsePositionX,responsePositionY,condition)


fun u_clearCurrentTrialRecord(
startPositionX: Float,
startPositionY: Float,
testPositionX: Float,
testPositionY: Float,
restPositionX: Float,
restPositionY: Float,
responsePositionX: Float,
responsePositionY: Float,
condition: String
) {
    //Todo: 這邊的數值 (var / val)有問題，要確認
    var startPositionX = 0f
    var startPositionY = 0f
    var testPositionX = 0f
    var testPositionY = 0f
    var restPositionX = 0f
    var restPositionY = 0f
    var responsePositionX = 0f
    var responsePositionY = 0f
    var condition = ""
    startX = 0f
    startY = 0f
}


/*// 將單次反應存入LIST
fun saveCurrentTrialRecord() {
    //確認目前practiceTrialsCount
    when (practiceTrialsCount) {
        1 -> {
            trial1list = saveTrialToList()
        }
        2 -> {
            trial2list = saveTrialToList()
        }
        3 -> {
            trial3list = saveTrialToList()
        }
        4 -> {
            trial4list = saveTrialToList()
        }
        5 -> {
            trial5list = saveTrialToList()
        }
        6 -> {
            trial6list = saveTrialToList()
        }
        7 -> {
            trial7list = saveTrialToList()
        }
        8 -> {
            trial8list = saveTrialToList()
        }
    }
}*/

//TODO: 要確認View是否可以寫成global function

//Todo: 嘗試把部分一直重複的參數弄成 constant 或全域變數

//Todo: 重新畫 practice fragement 流程圖，確認哪些步驟需要調整

//TOdo: 下一次開始工作，先從pressbutton開始調整，也先抓到給定的起始和目標位置


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

    val tempScore = u_calculateTrialScoreP( positionList )  //計算當前trial表現Error
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

fun u_calculateTrialScoreP(positionList:FloatArray): List<Float> {
    //Todo: 11/11 這邊的分數計算，可能要重新調整，因為起點/目標的給法不同
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



// View管理相關

fun u_changeText(start: TextView,test: TextView,
                 rest: TextView,response: TextView,
                 formalTrialCount: TextView,
                 recordingButton: Button,instructionText: TextView,
                 currentTrial: Int,maxTrailDesire: Int, condition: String
                 ) {

    formalTrialCount.text = "測驗次數: $currentTrial / $maxTrailDesire"


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

fun u_launchTrialInputSpinner(mContext:Context,trialInputSpinner:Spinner,practiceTrialCountList: ArrayList<String>) {
    //mContext_demo = requireActivity().applicationContext
    //trialInputSpinner = requireView()!!.findViewById<View>(R.id.trialInput_list) as Spinner
    val adapter = ArrayAdapter.createFromResource(
        mContext,
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

fun u_setTrialLimit(trialLimitInput: String): () -> Int {
    var maxTrailDesire = trialLimitInput.toInt()
    return {maxTrailDesire}
    }  //可直接移植到補測

fun u_launchContextSpinner(mContext:Context,contextSpinner:Spinner,contextList: ArrayList<String>,
                           fingerTarget:ImageView, fingerStarPoint:ImageView, fingerDownArrow:ImageView,
                           penTarget:ImageView, penStartPoint:ImageView, penDownArrow:ImageView) {
    //mContext_demo = requireActivity().applicationContext
    //contextSpinner = requireView()!!.findViewById<View>(R.id.context_list) as Spinner
    val adapter = ArrayAdapter.createFromResource(
        mContext,
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
            u_setContext(contextList[position],mContext,fingerTarget,fingerStarPoint,fingerDownArrow,penTarget,penStartPoint,penDownArrow)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //TODO("Not yet implemented")
        }
    }
}

fun u_setContext(contextInput: String, mContext:Context, fingerTarget:ImageView, fingerStarPoint:ImageView, fingerDownArrow:ImageView,
                 penTarget:ImageView, penStartPoint:ImageView, penDownArrow:ImageView) {
    currentTestContext = contextInput
    when (contextInput) {
        "請選情境" -> {
            Toast.makeText(mContext, "請選擇測驗情境", Toast.LENGTH_SHORT).show()
        }
        "Finger" -> {
        }
        "Pen" -> {
        }
    }
    u_checkContextAndLaunchView(currentTestContext,fingerTarget,fingerStarPoint,fingerDownArrow,penTarget,penStartPoint,penDownArrow) //更換ImageView宣告內容
}


//u_checkContextAndLaunchView(currentTestContext,fingerTarget,fingerStarPoint,fingerDownArrow,penTarget,penStartPoint,penDownArrow)

// Todo, 此function 到正式測驗後，要寫新的，可以多放斜向箭頭
fun u_checkContextAndLaunchView(context: String,
                                fingerTarget:ImageView, fingerStarPoint:ImageView, fingerDownArrow:ImageView,
                                penTarget:ImageView, penStartPoint:ImageView, penDownArrow:ImageView) {

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
            fingerStarPoint.visibility = View.GONE
            fingerDownArrow.visibility = View.GONE
        }
        "Finger" -> {
            penTarget.visibility = View.GONE
            penStartPoint.visibility = View.GONE
            penDownArrow.visibility = View.GONE

            fingerTarget.visibility = View.VISIBLE
            fingerStarPoint.visibility  = View.VISIBLE
            fingerDownArrow.visibility  = View.VISIBLE
        }
    }

} //輸入currentContext