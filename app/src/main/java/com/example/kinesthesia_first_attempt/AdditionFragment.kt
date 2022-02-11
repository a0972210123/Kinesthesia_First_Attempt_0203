package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.kinesthesia_first_attempt.databinding.FragmentAdditionBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class AdditionFragment : Fragment() {
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentAdditionBinding
    private lateinit var mContext_demo: Context
    lateinit var directionSpinner: Spinner
    lateinit var trialInputSpinner: Spinner
    lateinit var contextSpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        //hideBottomUIMenu()
        hideSystemUI()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_addition, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            additionFragment = this@AdditionFragment //使用listenser binding，用UI button 在xml中設定onclick
        }


        val currentPosition = requireView().findViewById<TextView>(R.id.current_position_field)
        currentPosition.text =
            ("目前位置 : X= " + String.format("%.2f", startX) + ",Y= " + String.format("%.2f", startY))

        val touchBoard = requireView().findViewById(R.id.view) as TouchBoard
        touchBoard.setOnTouchListener { _, _ ->
            if (buttonPressedCountsInATrial == 3 && currentTestContext == "Pen") {
                arrangeInAirData()
            }

            currentPosition.text =
                ("目前位置 : X= " + String.format("%.2f", startX) + ",Y= " + String.format(
                    "%.2f",
                    startY
                ))
            //true
            false
        } //0824可以讀到即時觸碰位置

        //* new
        touchBoard.setOnHoverListener { _, _ ->
            if (buttonPressedCountsInATrial == 3 && currentTestContext == "Pen") {
                arrangeInAirData()
            }

            currentPosition.text =
                ("目前位置 : X= " + String.format("%.2f", startX) + ",Y= " + String.format(
                    "%.2f",
                    startY
                ))
            false
        }
        //* new


        //更新文字view
        changeText()

        val Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score)
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

        /// direction spinner
        launchDirectionSpinner()

        //設定測驗情境(手指或握筆) > 必須
        launchContextSpinner()

        //設應測驗次數 for 補測  > 正式測驗刪掉
        launchTrialInputSpinner()

        //launch第一次避免crush
        checkContextAndLaunchView(currentTestContext)
    }


    /////////存INAIR
    fun arrangeInAirData() {
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

    fun saveInAirDataToCSV() {
        val outputInAirData = inAirData.toString().replace("\r", "").split("\n")
        //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
        val outputFileName = "Dominant_Addition_${currentTestContext}_${currentTestDirection}_InAir_Trial_$currentTrial.csv"
        // 存檔: name,List,flag
        outputInAirCsv(outputFileName, outputInAirData, 0)
    }

    fun outputInAirCsv(fileName: String, input: List<String>, flag: Int) {
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
        Toast.makeText(activity, "${currentTestContext}_${currentTestDirection}_InAir_Trial$currentTrial 儲存成功", Toast.LENGTH_SHORT).show()
        //Log.d("data", "outCSV Success")
    }  // sample from HW


    fun clearInAir() {
        systemTimestamp = 0
        heightZ = 0f
        tipPressure = 0f
        inAirData.setLength(0)
    }  //歸零Inair相關參數
    /////////存INAIR

    //以下三種spinner
    fun launchDirectionSpinner() {
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

    fun launchTrialInputSpinner() {
        mContext_demo = requireActivity().applicationContext
        trialInputSpinner = requireView()!!.findViewById<View>(R.id.trialInput_list) as Spinner
        val adapter = ArrayAdapter.createFromResource(
            mContext_demo,
            R.array.trial_count_list,
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
                setTrialLimit(trialCountList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
    }

    fun launchContextSpinner() {
        mContext_demo = requireActivity().applicationContext
        contextSpinner = requireView()!!.findViewById<View>(R.id.context_list) as Spinner
        val adapter = ArrayAdapter.createFromResource(
            mContext_demo,
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
                setContext(contextList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
    }

    //變數宣告
    //測驗相關變數宣告
    var buttonPressedCountsInATrial: Int = 0  //按鈕次數
    var trialsCount: Int = 0  //已練習次數
    var currentTrial: Int = 1  //當前Trial

    // 測驗情境變數
    var trialCondition =
        listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")
    var condition: String = ""

    //測驗次數上限變數
    // trialCount
    var maxTrailDesire: Int = 5
    val trialCountList = arrayListOf<String>("請選次數", "5", "4", "3", "2", "1")


    //測驗方向變數
    var currentTestDirection: String = ""
    //val directionList = arrayListOf<String>("請選方向", "L2L", "L2R", "R2R", "R2L")

/*    val directionList = arrayListOf<String>(
        "請選方向",
        "L_Up",
        "L_Up_Right",
        "R_Up",
        "R_Up_Left",
        "L_Down",
        "L_Down_Right",
        "R_Down",
        "R_Down_Left",
    )*/

    val directionList = arrayListOf<String>(
        "請選方向",
        "R_Up",       //<item>右下至右上</item>
        "R_Up_Left",  // <item>右下至左上</item>
        "L_Up",       //<item>左下至左上</item>
        "L_Up_Right", //<item>左下至右上</item>
    )


    var TestingFinishedList = arrayListOf<String>()

    //測驗方式
    var currentTestContext: String = ""
    val contextList = arrayListOf<String>("請選情境", "Finger", "Pen")
    var finishedContextList = arrayListOf<String>()


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
    private var scoreListForDisplay = listOf<Float>(0f, 0f, 0f, 0f, 0f)

    // 存檔相關變數宣告
    private val positionData = StringBuffer()
    private lateinit var arrayListOf5Trial: ArrayList<List<Float>>
    private var trial1list =
        listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f) //四次表現(X/Y) + 5個表現參數  = 13
    private var trial2list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial3list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial4list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var trial5list = listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)


    //版面相關變數宣告
    //隨機位置相關變數宣告 (每一輪只有5Trial)
    var randWidth = IntArray(5)
    var randHeight = IntArray(5)

    //螢幕寬、高
    val w = Resources.getSystem().displayMetrics.widthPixels    //網路資料值 =2800，程式計算值=2800
    val h = Resources.getSystem().displayMetrics.heightPixels   //網路資料值 =1752，程式計算值=1650
    val pixelDensity = Resources.getSystem().displayMetrics.densityDpi //資料值為 266 >>抓系統 為340

    val centerX = w / 2  //1400
    val centerY = h / 2  //825

    //(螢幕實際長度(mm), 螢幕實際寬度(mm),螢幕長度dp,螢幕寬度dp, x(mm/pixel))
    //可嘗試用程式計算值帶入 w or h
    val screenParameters = calculateScreenParams(w, h, 314.96, pixelDensity)
    val screenLengthMM = screenParameters[0]
    val screenWidthMM = screenParameters[1]
    val screenLengthIn2dp = screenParameters[2]
    val screenWidthIn2dp = screenParameters[3]
    val mmPerPixel = screenParameters[4]       //(mm/pixel)

    //換算單位段落
    //由於 1. xml中僅接受輸入dp 2. fragment中call layoutParams時使用的單位為pixel
    //若要確認排版位置一致，或是設定指定長度，需要進行換算
    val desireStart2TargetLengthInMM: Int = 100   //先設定10公分
    val desiretargetBoxSizeInMM: Int = 20  //先設定2公分
    val desiretargetStepInMM: Int = 5  //以5mm間隔

    // mm >>  pixel
    val targetBoxSize =
        (desiretargetBoxSizeInMM / mmPerPixel).toInt()   //先設定 5公分  //暫定 >> 需要計算實際長度 vs pixel
    val targetStep = (desiretargetStepInMM / mmPerPixel).toInt()

    val Center2Target = ((desireStart2TargetLengthInMM / 2) / mmPerPixel).toInt()
    val Center2Start = ((desireStart2TargetLengthInMM / 2) / mmPerPixel).toInt()

    // dp = (width in pixels * 160) / screen density
    // pixel = (dp * screen density)/160
    val widthOfTitle = 400
    val widthOfTandS = 50 //dp
    val cantextPenWidthOfTandS = 20 //dp

    val titleCalibrate = viewAdjustDp2Pixel(widthOfTitle)

    val TandSCalibrate = viewAdjustDp2Pixel(widthOfTandS)
    val penTandSCalibrate = viewAdjustDp2Pixel(cantextPenWidthOfTandS)

    var calibrateWidth = TandSCalibrate

    //context 相關 ImageView宣告
    lateinit var startView: ImageView
    lateinit var targetView: ImageView
    lateinit var randomTargetView: ImageView
    lateinit var upArrow: ImageView
    lateinit var upLeftArrow: ImageView
    lateinit var upRightArrow: ImageView

    //0908改版面新增
    lateinit var downArrow: ImageView
    lateinit var downLeftArrow: ImageView
    lateinit var downRightArrow: ImageView


    fun checkContextAndLaunchView(context: String) {
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
                val hideTargetView = requireView().findViewById<ImageView>(R.id.target)
                val hideStartView = requireView().findViewById<ImageView>(R.id.start_point)
                val hideRandomTargetView = requireView().findViewById<ImageView>(R.id.random_target)
                val hideUpArrow = requireView().findViewById<ImageView>(R.id.up_arrow)
                val hideUpLeftArrow = requireView().findViewById<ImageView>(R.id.arrow_to_up_left)
                val hideUpRightArrow = requireView().findViewById<ImageView>(R.id.arrow_to_up_right)

                //0908new
                val hideDownArrow = requireView().findViewById<ImageView>(R.id.down_arrow)
                val hideDownLeftArrow =
                    requireView().findViewById<ImageView>(R.id.arrow_to_down_left)
                val hideDownRightArrow =
                    requireView().findViewById<ImageView>(R.id.arrow_to_down_right)

                hideTargetView.visibility = View.GONE
                hideStartView.visibility = View.GONE
                hideRandomTargetView.visibility = View.GONE
                hideUpArrow.visibility = View.GONE
                hideUpLeftArrow.visibility = View.GONE
                hideUpRightArrow.visibility = View.GONE

                //0908new
                hideDownArrow.visibility = View.GONE
                hideDownLeftArrow.visibility = View.GONE
                hideDownRightArrow.visibility = View.GONE

            }
            "Finger" -> {
                val hideTargetView = requireView().findViewById<ImageView>(R.id.pen_target)
                val hideStartView = requireView().findViewById<ImageView>(R.id.pen_start_point)
                val hideRandomTargetView =
                    requireView().findViewById<ImageView>(R.id.pen_random_target)
                val hideUpArrow = requireView().findViewById<ImageView>(R.id.pen_up_arrow)
                val hideUpLeftArrow =
                    requireView().findViewById<ImageView>(R.id.pen_arrow_to_up_left)
                val hideUpRightArrow =
                    requireView().findViewById<ImageView>(R.id.pen_arrow_to_up_right)

                //0908new
                val hideDownArrow = requireView().findViewById<ImageView>(R.id.pen_down_arrow)
                val hideDownLeftArrow =
                    requireView().findViewById<ImageView>(R.id.pen_arrow_to_down_left)
                val hideDownRightArrow =
                    requireView().findViewById<ImageView>(R.id.pen_arrow_to_down_right)

                hideTargetView.visibility = View.GONE
                hideStartView.visibility = View.GONE
                hideRandomTargetView.visibility = View.GONE
                hideUpArrow.visibility = View.GONE
                hideUpLeftArrow.visibility = View.GONE
                hideUpRightArrow.visibility = View.GONE
                //0908new
                hideDownArrow.visibility = View.GONE
                hideDownLeftArrow.visibility = View.GONE
                hideDownRightArrow.visibility = View.GONE
            }
        }

        when (tempContext) {
            "Finger" -> {
                targetView = requireView().findViewById<ImageView>(R.id.target)
                startView = requireView().findViewById<ImageView>(R.id.start_point)
                randomTargetView = requireView().findViewById<ImageView>(R.id.random_target)
                upArrow = requireView().findViewById<ImageView>(R.id.up_arrow)
                upLeftArrow = requireView().findViewById<ImageView>(R.id.arrow_to_up_left)
                upRightArrow = requireView().findViewById<ImageView>(R.id.arrow_to_up_right)

                //0908new
                downArrow = requireView().findViewById<ImageView>(R.id.down_arrow)
                downLeftArrow = requireView().findViewById<ImageView>(R.id.arrow_to_down_left)
                downRightArrow = requireView().findViewById<ImageView>(R.id.arrow_to_down_right)

                calibrateWidth = TandSCalibrate
            }
            "Pen" -> {
                targetView = requireView().findViewById<ImageView>(R.id.pen_target)
                startView = requireView().findViewById<ImageView>(R.id.pen_start_point)
                randomTargetView = requireView().findViewById<ImageView>(R.id.pen_random_target)
                upArrow = requireView().findViewById<ImageView>(R.id.pen_up_arrow)
                upLeftArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_up_left)
                upRightArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_up_right)

                //0908new
                downArrow = requireView().findViewById<ImageView>(R.id.pen_down_arrow)
                downLeftArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_down_left)
                downRightArrow = requireView().findViewById<ImageView>(R.id.pen_arrow_to_down_right)

                calibrateWidth = penTandSCalibrate
            }
        }

    } //輸入currentContext

    fun viewAdjustDp2Pixel(dpWidthOfView: Int): Int {
        return (((dpWidthOfView / 2) * pixelDensity) / 160).toInt()
    }

    fun calculateScreenParams(
        resolutionLength: Int,
        resolutionWidth: Int,
        screenDiagonalSizeMM: Double,
        screenPixelDensity: Int
    ): List<Double> {
        //參考資料Comment
        /* 公式推導
         // 依據已知螢幕解析度長寬比(a:b) 令 螢幕長為8x 寬為5x，已知螢幕對角線長度為314.96mm ，求螢幕實際長寬(mm)，並換匴此螢幕每單位pixel長度(x)。
         // (ax)^2 + (bx)^2 = ( 314.96 )^2
         // (a^2+b^2)(x^2) = (314.96)^2
         // x = sqrt((314.96)^2 / (a^2+b^2))
         // ax = 螢幕實際長度, bx=螢幕實際寬度, x 單位為 (mm/pixel)
         */
        /* 參考網站及資料
        android material:  //https://material.io/design/layout/pixel-density.html
        dp vs sp vs pixel :  //https://stackoverflow.com/questions/2025282/what-is-the-difference-between-px-dip-dp-and-sp
         */
        /* 目前平板參數
        網站: https://icecat.biz/zh-tw/p/samsung/sm-t970nzkaeue/galaxy+tab+s7%2B-tablets-8806090623523-wi-fi+sm-t970-80509596.html
            //型號及規格 :  Galaxy Tab S7+ SM-T970NZKAEUE
            //平板大小: PHYSICAL SPECIFICATIONS - Dimensions	Tablet: 285 x 185 x 5.7 mm
            //螢幕大小(對角)/面積 : 12.4 inches( 314.96 mm ~ 31.5 cm)  , area (446.1 cm2 )
            //解析度 Resolution:	1752 x 2800 pixels,
            //長寬比 16:10 ratio:
            //pixel density: (~266 ppi density)
         */
        /* pixel dp轉換
           // Pixel density on Android:  Dps and screen density
            // A dp is equal to one physical pixel on a screen with a density of 160.
            // To calculate dp: dp = (width in pixels * 160) / screen density
         */
        val a = resolutionLength.toDouble()
        val b = resolutionWidth.toDouble()
        val mmPerPixel = sqrt(screenDiagonalSizeMM.pow(2.0) / (a.pow(2.0) + b.pow(2.0))).toDouble()
        val screenLengthMM = a * mmPerPixel
        val screenWidthMM = b * mmPerPixel
        //本平板pixel density = 266, Resolution:	1752 x 2800 pixels,
        val screenLengthIn2dp = ((resolutionLength * 160) / screenPixelDensity).toDouble()
        val screenWidthIn2dp = ((resolutionWidth * 160) / screenPixelDensity).toDouble()

        val logString =
            "長mm:$screenLengthMM,寬mm:$screenWidthMM,長dp:$screenLengthIn2dp,寬dp:$screenWidthIn2dp,$mmPerPixel(mm/pixel)"
        Log.d("Screen Information", logString)

        return listOf(
            screenLengthMM,
            screenWidthMM,
            screenLengthIn2dp,
            screenWidthIn2dp,
            mmPerPixel
        )
    }
    // 輸入螢幕 (長pixel:Int ,寬pixel:Int ,螢幕對角長度(mm):Double, 螢幕pixel密度)
    // 傳回List: (螢幕實際長度(mm), 螢幕實際寬度(mm),螢幕長度dp,螢幕寬度dp, x(mm/pixel))


    fun setDirection(directionInput: String) {
        currentTestDirection = directionInput  //儲存目前選擇方向

        //View宣告
        /*
        val targetView = requireView().findViewById<ImageView>(R.id.target)
        val startView = requireView().findViewById<ImageView>(R.id.start_point)
        val upArrow = requireView().findViewById<ImageView>(R.id.arrow)
        val leftArrow = requireView().findViewById<ImageView>(R.id.arrow_to_left)
        val rightArrow = requireView().findViewById<ImageView>(R.id.arrow_to_right)

         */
        checkContextAndLaunchView(currentTestContext) //判斷狀況並launch特定view

        val performanceTitle = requireView().findViewById<TextView>(R.id.performance_title)
        //宣告調整各view需要的layoutParams
        val targetParams = targetView.layoutParams as ViewGroup.MarginLayoutParams
        val startParams = startView.layoutParams as ViewGroup.MarginLayoutParams
        val titleParams = performanceTitle.layoutParams as ViewGroup.MarginLayoutParams

        // "L_Up", "L_Up_Right", "R_Up", "R_Up_Left", "L_Down", "L_Down_Right", "R_Down", "R_Down_Left",)

        //調整要呈現的View
        when (currentTestDirection) {
            "請選方向" -> {
                targetView.visibility = View.GONE
                startView.visibility = View.GONE
                upArrow.visibility = View.GONE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.GONE

                //new
                downArrow.visibility = View.GONE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.GONE

            }
            /*"L_Up" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.VISIBLE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.GONE
                //new
                downArrow.visibility = View.GONE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.GONE
            }
            "L_Up_Right" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.GONE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.VISIBLE
                //new
                downArrow.visibility = View.GONE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.GONE
            }
            "R_Up" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.VISIBLE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.GONE
                //new
                downArrow.visibility = View.GONE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.GONE
            }
            "R_Up_Left" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.GONE
                upLeftArrow.visibility = View.VISIBLE
                upRightArrow.visibility = View.GONE
                //new
                downArrow.visibility = View.GONE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.GONE
            }*/
            //以下為為受測者方向出發的選項，程式描述為icon對於施測者的方向///
            "R_Up" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.GONE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.GONE
                //new
                downArrow.visibility = View.VISIBLE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.GONE
            }        //<item>右下至右上</item>
            "R_Up_Left" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.GONE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.GONE
                //new
                downArrow.visibility = View.GONE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.VISIBLE
            }   // <item>右下至左上</item>
            "L_Up" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.GONE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.GONE
                //new
                downArrow.visibility = View.VISIBLE
                downLeftArrow.visibility = View.GONE
                downRightArrow.visibility = View.GONE
            }        //<item>左下至左上</item>
            "L_Up_Right" -> {
                targetView.visibility = View.VISIBLE
                startView.visibility = View.VISIBLE
                upArrow.visibility = View.GONE
                upLeftArrow.visibility = View.GONE
                upRightArrow.visibility = View.GONE
                //new
                downArrow.visibility = View.GONE
                downLeftArrow.visibility = View.VISIBLE
                downRightArrow.visibility = View.GONE
            }  //<item>左下至右上</item>
        }
        //調整view位置
        when (currentTestDirection) {
            "請選方向" -> {
                titleParams.setMargins(centerX - titleCalibrate, centerY - 400, 0, 0)
            }
         /*   "L_Up" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth - Center2Target,
                    centerY - calibrateWidth - Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth - Center2Start,
                    centerY - calibrateWidth + Center2Start,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate + 800, centerY - 400, 0, 0)
            }
            "L_Up_Right" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth + Center2Target,
                    centerY - calibrateWidth - Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth - Center2Start,
                    centerY - calibrateWidth + Center2Start,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate + 800, centerY - 400, 0, 0)
            }
            "R_Up" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth + Center2Target,
                    centerY - calibrateWidth - Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth + Center2Start,
                    centerY - calibrateWidth + Center2Start,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate - 800, centerY - 400, 0, 0)
            }
            "R_Up_Left" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth - Center2Target,
                    centerY - calibrateWidth - Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth + Center2Target,
                    centerY - calibrateWidth + Center2Target,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate - 800, centerY - 400, 0, 0)
            }*/
            //以下為為受測者方向出發的選項，程式描述為icon對於施測者的方向///
            "R_Up" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth - Center2Target,
                    centerY - calibrateWidth + Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth - Center2Start,
                    centerY - calibrateWidth - Center2Start,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate + 800, centerY - 400, 0, 0)
            }
            "R_Up_Left" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth + Center2Target,
                    centerY - calibrateWidth + Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth - Center2Target,
                    centerY - calibrateWidth - Center2Target,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate - 800, centerY - 400, 0, 0)
            }
            "L_Up" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth + Center2Target,
                    centerY - calibrateWidth + Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth + Center2Start,
                    centerY - calibrateWidth - Center2Start,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate - 800, centerY - 400, 0, 0)
            }
            "L_Up_Right" -> {
                targetParams.setMargins(
                    centerX - calibrateWidth - Center2Target,
                    centerY - calibrateWidth + Center2Target,
                    0,
                    0
                )
                startParams.setMargins(
                    centerX - calibrateWidth + Center2Start,
                    centerY - calibrateWidth - Center2Start,
                    0,
                    0
                )

                titleParams.setMargins(centerX - titleCalibrate + 800, centerY - 400, 0, 0)
            }
        }
    }

    fun setTrialLimit(trialLimitInput: String) {
        when (trialLimitInput) {
            "請選次數" -> {
                Toast.makeText(activity, "請輸入測驗次數上限", Toast.LENGTH_SHORT).show()
            }
            "5" -> {
                maxTrailDesire = 5
            }
            "4" -> {
                maxTrailDesire = 4
            }
            "3" -> {
                maxTrailDesire = 3
            }
            "2" -> {
                maxTrailDesire = 2
            }
            "1" -> {
                maxTrailDesire = 1
            }
        }
    }  //可直接移植到補測

    fun setContext(contextInput: String) {
        currentTestContext = contextInput
        when (contextInput) {
            "請選情境" -> {
                Toast.makeText(activity, "請選擇測驗情境", Toast.LENGTH_SHORT).show()
            }
            // 將ImageView更換 (大/小)
            "Finger" -> {
            }
            "Pen" -> {
            }
        }
        //checkContextAndLaunchView(currentTestContext) //更換ImageView宣告內容
        setDirection(currentTestDirection) //更新View(內含上一行
    }

    fun confirmSelection() {
        val contextChecked = checkContextInput()
        val directionChecked = checkDirectionInput()
        if (contextChecked == 1) {
            if (directionChecked == 1) {
                //TestingFinishedList.add(currentTestDirection)
                //finishedcontextList.add(currentTestContext) >>改到測完所有方向
                randomThePosition()
                setTargetPosition()
                changeText()
                Toast.makeText(
                    activity,
                    "開始補測 $currentTestContext $currentTestDirection",
                    Toast.LENGTH_SHORT
                ).show()
                manageVisibility(0)  //顯示觸控板及記錄紐
            }
        }
    }    //確認選擇方向、情境、並更新測驗紀錄    //移植補測後 刪掉判斷是否有選過

    fun checkContextInput(): Int {
        //checkContextTested()//避免bug可以跳出
        var flag = 0
        if (currentTestContext == contextList[0]) {
            Toast.makeText(activity, "測驗情境尚未設定", Toast.LENGTH_SHORT).show()
        } else {
            if (finishedContextList.contains(currentTestContext)) {
                Toast.makeText(activity, "此情境已測過", Toast.LENGTH_SHORT).show()
            } else {
                //finishedcontextList.add(currentTestContext)
                flag = 1
            }
            flag = 1  //直接移出 就算測過/選過也關係
        }
        return flag
    }

    fun checkDirectionInput(): Int {
        //checkDirectionTested()  //避免bug可以跳出
        var flag = 0
        if (currentTestDirection == directionList[0]) {
            Toast.makeText(activity, "測驗方向尚未設定", Toast.LENGTH_SHORT).show()
        } else {
            //若有選方向 > /check condition
            if (TestingFinishedList.contains(currentTestDirection)) {
                //若選過>> toast >>return
                Toast.makeText(activity, "此方向已測過", Toast.LENGTH_SHORT).show()
            } else {
                //若沒選過  >> toast >> update view
                //TestingFinishedList.add(currentTestDirection)
                flag = 1
            }
            flag = 1 //直接移出 就算測過/選過也關係
        }
        return flag
    }

    fun checkContextTested() {
        val checkList = arrayListOf<String>("Finger", "Pen")
        if (finishedContextList.toSet() == checkList.toSet()) {
            //finishedcontextList = arrayListOf<String>() //清除List >> 準備測另種情境
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.test_dialog_message_finished_all_context)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
                .setPositiveButton(getString(R.string.test_dialog_back_to_menu)) { _, _ ->
                    Toast.makeText(activity, "請查驗資料或補充測驗", Toast.LENGTH_SHORT).show()
                    finishedContextList = arrayListOf<String>()
                    goBackToMenu()
                }
                .show() //creates and then displays the alert dialog.
        }
    }

    // 確認方向後，產生randomlist
    fun randomThePosition() {
        //這邊會決定出來的數值範圍寬度
        val tempWidth = ((-targetBoxSize / 2)..(targetBoxSize / 2) step targetStep).shuffled()
        val tempHeight = ((-targetBoxSize / 2)..(targetBoxSize / 2) step targetStep).shuffled()
        // 這邊暫存
        randWidth = tempWidth.subList(0, 5).toIntArray()
        randHeight = tempHeight.subList(0, 5).toIntArray()
    }   //需要調整數值範圍

    fun setTargetPosition() {
        var c2tX = 0
        var c2tY = 0

        when (currentTestDirection) {
          /*  "L_Up" -> {
                c2tX = centerX - calibrateWidth - Center2Target
                c2tY = centerY - calibrateWidth - Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }
            "L_Up_Right" -> {
                c2tX = centerX - calibrateWidth + Center2Target
                c2tY = centerY - calibrateWidth - Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }
            "R_Up" -> {
                c2tX = centerX - calibrateWidth + Center2Target
                c2tY = centerY - calibrateWidth - Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }
            "R_Up_Left" -> {
                c2tX = centerX - calibrateWidth - Center2Target
                c2tY = centerY - calibrateWidth - Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }*/

            "R_Up" -> {
                c2tX = centerX - calibrateWidth - Center2Target
                c2tY = centerY - calibrateWidth + Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }
            "R_Up_Left" -> {
                c2tX = centerX - calibrateWidth + Center2Target
                c2tY = centerY - calibrateWidth + Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }
            "L_Up" -> {
                c2tX = centerX - calibrateWidth + Center2Target
                c2tY = centerY - calibrateWidth + Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }
            "L_Up_Right" -> {
                c2tX = centerX - calibrateWidth - Center2Target
                c2tY = centerY - calibrateWidth + Center2Target
                setTargetRandomPosition(c2tX, c2tY)
            }
        }
    }   //根據選項決定方向參數pixel

    fun setTargetRandomPosition(c2tX: Int, c2tY: Int) {
        //val randomTargetView = requireView().findViewById<ImageView>(R.id.random_target)
        randomTargetView.visibility = View.VISIBLE
        val randomTargetParams = randomTargetView.layoutParams as ViewGroup.MarginLayoutParams
        randomTargetParams.setMargins(
            c2tX + randWidth[trialsCount],
            c2tY + randHeight[trialsCount],
            0,
            0
        )
    }  //在方向參數基礎上，加上隨機位置參數

    fun checkDirectionTested() {
        contextSpinner = requireView()!!.findViewById<View>(R.id.context_list) as Spinner
        val checkList = arrayListOf<String>(
            "L_Up",
            "L_Up_Right",
            "R_Up",
            "R_Up_Left",
            "L_Down",
            "L_Down_Right",
            "R_Down",
            "R_Down_Left"
        )
        //當四種都測完
        if (TestingFinishedList.toSet() == checkList.toSet()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.test_dialog_message_finished_all_direction)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
                .setPositiveButton(getString(R.string.test_dialog_next_context)) { _, _ ->
                    finishedContextList.add(currentTestContext)
                    TestingFinishedList = arrayListOf<String>() //清除List >> 準備測另種情境
                    contextSpinner.visibility = View.VISIBLE
                    Toast.makeText(activity, "更換情境", Toast.LENGTH_SHORT).show()
                    //checkContextTested() //確認兩種情境是否測驗完成

                }
                .show() //creates and then displays the alert dialog.
        }
    } //判斷是否所有方向都測過 //1105前舊版本 要刪掉

    fun manageVisibility(flag: Int) {
        //找到相關的View
        val touchBoard = requireView().findViewById(R.id.view) as TouchBoard
        directionSpinner = requireView()!!.findViewById<View>(R.id.direction_list) as Spinner

        trialInputSpinner = requireView()!!.findViewById<View>(R.id.trialInput_list) as Spinner
        contextSpinner = requireView()!!.findViewById<View>(R.id.context_list) as Spinner

        val selectButton = requireView().findViewById(R.id.select_direction) as Button
        val recordingButton = requireView().findViewById<Button>(R.id.record_position)
        val trialCount = requireView().findViewById<TextView>(R.id.trial_count)
        val countDown = requireView().findViewById<TextView>(R.id.text1)
        val Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score)

        //val randomTargetView = requireView().findViewById<ImageView>(R.id.random_target)

        val instructionText = requireView().findViewById<TextView>(R.id.instruction_demonstration)

        when (flag) {
            0 -> {
                //顯示測驗進行VIEW
                trialCount.visibility = View.VISIBLE
                recordingButton.visibility = View.VISIBLE
                touchBoard.visibility = View.VISIBLE
                instructionText.visibility = View.VISIBLE
                countDown.visibility = View.VISIBLE

                //隱藏方向選擇VIEW
                trialInputSpinner.visibility = View.INVISIBLE
                contextSpinner.visibility = View.INVISIBLE
                directionSpinner.visibility = View.INVISIBLE
                selectButton.visibility = View.INVISIBLE
            }

            1 -> {    //放在每5次結束
                //隱藏測驗進行VIEW
                trialCount.visibility = View.GONE
                recordingButton.visibility = View.GONE
                touchBoard.visibility = View.GONE
                instructionText.visibility = View.GONE
                countDown.visibility = View.GONE

                //顯示方向選擇VIEW
                trialInputSpinner.visibility = View.VISIBLE
                contextSpinner.visibility = View.VISIBLE
                directionSpinner.visibility = View.VISIBLE
                selectButton.visibility = View.VISIBLE
                //
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
                randomTargetView.visibility = View.GONE
            }
        }
    }  //管理測驗相關View顯示、可觸控與否

    fun pressButton() {
        buttonPressedCountsInATrial++      //每按一次按鈕+1
        Log.d("X/Y/面積/長軸/短軸：inFragment", "$startX  $startY  $bb  $b1  $b2")
        recordPosition()   //儲存位置，並管理測驗流程
        changeText()       //更動text
        checkTime()        //計時

        if (buttonPressedCountsInATrial == 4) {
            scoreListForDisplay = calculateTrialScore()
            displayScoreInText(scoreListForDisplay, 1)
            clearScoreList()
        } else {
            displayScoreInText(scoreListForDisplay, 0)
        }  //計算測驗表現 (RE*2，AE*3)  //更新text內容

        if (buttonPressedCountsInATrial == 5) {
            //0912測試存InAir
            if (currentTestContext == "Pen"){
                saveInAirDataToCSV()
            }
            clearInAir()


            addTrialsCount()           // 完成一次測驗練習
            if (trialsCount == maxTrailDesire) { //第五trail結束不用再設目標
            } else {
                setTargetPosition()// 重設目標位置
            }
            saveCurrentTrialRecord()   //將單次反應存入LIST(包含分數計算)
            clearCurrentTrialRecord()  //清除單次表現、歸零座標、重設測驗情境
            checkTrialLimit()       //檢查是否達到練習次數
            buttonPressedCountsInATrial = 0
        }   //每一輪結束後，reset 參數以及view
        return
    }   //按鈕後執行  //1.增加按鈕次數 2.call recordPosition 3.call changeText 4. 計時 5.歸零按鈕次數

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
        formalTrialCount.text = "測驗次數: $currentTrial / $maxTrailDesire"


        //找到指導語textView
        val instructionText = requireView().findViewById<TextView>(R.id.instruction_demonstration)
        var instructionList = arrayListOf("")

        val pen = arrayListOf(
            "施測者將受試者握著的筆尖，" + "\n" + "移動至 預備位置 上，" + "\n" + "確認動作停止後按下紀錄。",
            "施測者將受試者握著的筆尖，" + "\n" + "移動到 目標位置 上，" + "\n" + "確認動作停止後按下紀錄。",
            "施測者將受試者握著的筆尖，" + "\n" + "移動回 預備位置 上，" + "\n" + "確認動作停止後按下紀錄。",
            "受試者聽到嗶聲後將自己握著的筆，" + "\n" + "移動到所記得的位置，" + "\n" + "確認動作停止後按下紀錄。",
            "施測者將受試者握著的筆尖，" + "\n" + "移動到平板外的桌面上，" + "\n" + "確認資料正確後按下Save Trial。"
        )

        val finger = arrayListOf(
            "施測者將受試者的手指，" + "\n" + "移動至 預備位置 上，" + "\n" + "確認動作停止後按下紀錄。",
            "施測者將受試者的手指，" + "\n" + "移動到 目標位置 上，" + "\n" + "確認動作停止後按下紀錄。",
            "施測者將受試者的手指，" + "\n" + "移動回 預備位置 上，" + "\n" + "確認動作停止後按下紀錄。",
            "受試者聽到嗶聲後將自己的手指，" + "\n" + "移動到所記得的位置，" + "\n" + "確認動作停止後按下紀錄。",
            "施測者將受試者的手指，" + "\n" + "移動到平板外的桌面上，" + "\n" + "確認資料正確後按下Save Trial。"
        )

        when (currentTestContext) {
            "Pen" -> {
                instructionList = pen
            }

            "Finger" -> {
                instructionList = finger
            }
        }


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
                recordingButton.text = getString(R.string.next_trial)
                recordingButton.textSize = 24.toFloat()
                instructionText.text = instructionList[4]
            }
            "" -> {
                start.text = "起始位置："
                test.text = "目標位置："
                rest.text = "預備位置："
                response.text = "反應位置："
                recordingButton.text = getString(R.string.record_position)
                recordingButton.textSize = 30.toFloat()

                instructionText.text = instructionList[0]
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

    fun reset5Trial() {
        for (n in 0..4) {
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

    fun arrangeData(TargetStringBuffer: StringBuffer): StringBuffer {   // 將List排進Buffer
        for (index in 0..4) {  //選一個trial
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
    }  //計算測驗分數  1105校正正負號

    fun clearScoreList() {
        scoreListForDisplay = listOf<Float>(0f, 0f, 0f, 0f, 0f)
    } //清除測驗分數

    fun displayScoreInText(inputScoreList: List<Float>, flag: Int) {
        val Score = requireView().findViewById<TextView>(R.id.performance_current_trial_score)
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
// + String.format("%.2f", startX) + ",Y= "+String.format("%.2f", startY)
// /////// 將表現分數取小數點
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
    }  //用於描述測驗表現: 1105去除轉換+-判斷式

    fun savePerformanceToCSV() {
        //call 整理8trialData
        arrayListOf5Trial = combineList()
        //call function 將List排進buffer
        arrangeData(positionData)
        //切割buffer
        val outputPositionData = positionData.toString().replace("\r", "").split("\n")

        val timeStamp: String //避免資料夾個案編號資料重複的額外後接編號
        val timeStampFormatter =
            SimpleDateFormat("HH_mm_ss", Locale.getDefault()) //H 時 在一天中 (0~23) // m 分 // s 秒
        val timeStampCalendar = Calendar.getInstance()
        timeStamp =
            timeStampFormatter.format(timeStampCalendar.time).toString() //當日時間  //需傳到viewmodel

        val timeStampMillis = System.currentTimeMillis()

        //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
        val outputFileName =
            "Dominant_Addition_" + currentTestContext + "_" + currentTestDirection + "_" + timeStampMillis + ".csv"

        // 存檔: name,List,flag
        outputCsv(outputFileName, outputPositionData, 0)
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

        val file = File(filePathStr, fileName)
        val os = FileOutputStream(file, true)   // 這邊給的字串要有檔案類型
        os.write(output.toString().toByteArray())
        os.flush()
        os.close()
        output.setLength(0) //clean buffer
        Toast.makeText(
            activity,
            "補充測驗:$currentTestContext $currentTestDirection 儲存成功",
            Toast.LENGTH_SHORT
        )
            .show()
        //Toast.makeText(activity, "正式測驗"+$direction+"儲存成功", Toast.LENGTH_SHORT)
        //Log.d("data", "outCSV Success")
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
            millisInFuture = 4000 //暫時改
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

        if (trialsCount >= maxTrailDesire) {
            //checkDirectionTested() // 確認完成所有測驗方向

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.addition_test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.addition_test_dialog_message)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                .setNegativeButton(getString(R.string.addition_test_dialog_next_condition)) { _, _ ->
                    savePerformanceToCSV()//儲存測驗表現
                    clearRecord()  // 清除測驗表現
                    formalTrialCount.text = "測驗次數: $currentTrial / $maxTrailDesire "
                    manageVisibility(1)
                    Toast.makeText(activity, "更換情境或方向", Toast.LENGTH_SHORT).show()
                }

                .setPositiveButton(getString(R.string.test_dialog_back_to_menu)) { _, _ ->
                    savePerformanceToCSV()//儲存測驗表現
                    clearRecord()  // 清除測驗表現
                    //formalTrialCount.text = "測驗次數: $currentTrial / $maxTrailDesire "
                    //manageVisibility(1)
                    Toast.makeText(activity, "回到選單", Toast.LENGTH_SHORT).show()
                    goBackToMenu()
                }
                .show() //creates and then displays the alert dialog.
        }
    }  //需修改成每完成一個方向，提供補測機會?? //測驗選單選項可以判斷全部情境都測過再出現

    fun goBackToMenu() {
        Toast.makeText(activity, "回到測驗選單", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_additionFragment_to_testMenuFragment)
    }

    private fun hideBottomUIMenu() {
        //https://www.cnblogs.com/Alex80/p/12986581.html
        //隱藏虛擬按鍵，並且全螢幕
        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v: View = (activity as AppCompatActivity?)!!.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView: View = (activity as AppCompatActivity?)!!.window.decorView
            val uiOptions: Int = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }
    }  //觸控時會重新顯示

    private fun showBottomUIMenu() {
        //恢復普通狀態
        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v: View = (activity as AppCompatActivity?)!!.window.decorView
            v.systemUiVisibility = View.VISIBLE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView: View = (activity as AppCompatActivity?)!!.window.decorView
            val uiOptions: Int = View.SCREEN_STATE_OFF
            decorView.systemUiVisibility = uiOptions
        }
    } //觸控時會重新顯示

    //https://stackoverflow.com/questions/37380587/android-how-to-hide-the-system-ui-properly
    private fun hideSystemUI() {
        val decorView = requireActivity().window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
    }

    private fun showSystemUI() {
        val decorView = requireActivity().window.decorView
        val uiOptions = decorView.systemUiVisibility
        var newUiOptions = uiOptions
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE.inv()
        newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
        decorView.systemUiVisibility = newUiOptions
    }


} //Fragment End