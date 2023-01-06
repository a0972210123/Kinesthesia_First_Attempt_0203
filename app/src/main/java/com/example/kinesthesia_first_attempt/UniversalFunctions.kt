package com.example.kinesthesia_first_attempt


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

// 此Class目前暫時空白，如有資料需要pass給xml，可以放入這邊
class UniversalFunctions : AppCompatActivity() {

}


//版面相關變數宣告
//隨機位置相關變數宣告 (每一輪只有5Trial)
var randWidth = IntArray(5)
var randHeight = IntArray(5)

//螢幕寬、高
val w =
    Resources.getSystem().displayMetrics.widthPixels    //網路資料值 =2800，程式計算值=2800 //The absolute width of the available display size in pixels.
val h =
    Resources.getSystem().displayMetrics.heightPixels  //網路資料值 =1752，程式計算值=1650 //The absolute height of the available display size in pixels.

val centerCoordinateX = w / 2   //1400
val centerCoordinateY = h / 2  //825
val pixelDensity = Resources.getSystem().displayMetrics.densityDpi //340，APP本身讀到的

val adjustedPixelDensity = 266 //官方數據
val adjustedScreenParameters = calculateScreenParams(w, h, 314.96, adjustedPixelDensity)
val adjustedMmPerPixel = adjustedScreenParameters[4]
val adjustedPixelPerMm = 1 / adjustedMmPerPixel

//(螢幕實際長度(mm), 螢幕實際寬度(mm),螢幕長度dp,螢幕寬度dp, x(mm/pixel))
//可嘗試用程式計算值帶入 w or h

val screenParameters = calculateScreenParams(w, h, 314.96, pixelDensity)
val screenLengthMM = screenParameters[0]
val screenWidthMM = screenParameters[1]
val screenLengthIn2dp = screenParameters[2]
val screenWidthIn2dp = screenParameters[3]
val mmPerPixel = screenParameters[4]       //(mm/pixel)

val pixelPerMm = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_MM,
    1f,
    Resources.getSystem().displayMetrics
)  //10.011417

fun printScreenParameters() {
    //重新用 android程式 抓需要的參數
// https://medium.com/analytics-vidhya/what-is-the-difference-between-px-dip-dp-and-sp-e4351fefa685
// https://itecnote.com/tecnote/android-convert-mm-to-pixels/
// https://developer.android.com/reference/android/util/TypedValue#applyDimension(int,%20float,%20android.util.DisplayMetrics)
// https://developer.android.com/reference/android/util/DisplayMetrics#fields_1
// https://magiclen.org/android-screen/

    var screenDensity_fromResources =
        Resources.getSystem().displayMetrics.density // = currentDpi/160  = 2.125
    var screenDensityDpi_fromResources =
        Resources.getSystem().displayMetrics.densityDpi //= currentDpi = 340

    val ScreenWidth_fromResourcesXdpi =
        Resources.getSystem().displayMetrics.xdpi  // xdpi : physical pixels per inch of the screen in the X dimension.  //254.29
    val ScreenHeight_fromResourcesYdpi =
        Resources.getSystem().displayMetrics.ydpi // ydpi : physical pixels per inch of the screen in the Y dimension.  //261.47

    val XpixelPerMm_fromxdpi =
        (Resources.getSystem().displayMetrics.xdpi) / 25.4    //10.011417058509167
    val YpixelPerMm_fromxdpi =
        (Resources.getSystem().displayMetrics.ydpi) / 25.4    //10.294094536248155


    // 1dp = 1/160 inch = 0.00625 inch (always true)
    // 1dp = 0.00625 * 25.4 mm  (always true)
    // 1dp = 0.00625 * 25.4 * 10.011417058509167 px = 1.58931245804 px

    // 1 inch =  254.29 or 261.47 px  (this tablet)
    // 1 inch =  25.4 mm (always true)
    // 25.4 mm = 254.29 or 261.49 px (this tablet)
    // 1mm = 10.011417058509167  or  10.294094536248155 px (this tablet)

    // 1 dp = (1 /160) * 25.4 * (xdpi or ydpi / 25.4) pixel

    // 340/160 = 2.125 px = 1 dp   > sqrt(2.125) = 1.457
    // 254.29/160 = 1.5893125 px  = 1 dp   > sqrt = 1.25
    // 261.47/160 = 1.63125 px = 1 dp    > sqrt = 1.27

    // 5/3.6 = 1.3888889 > screen calibration ~ 1.4


    Log.d("ScreenParameters", "absolute width (pixel)" + ":" + w.toString()) //2800
    Log.d("ScreenParameters", "absolute height (pixel)" + ":" + h.toString()) //1650
    Log.d(
        "ScreenParameters",
        "=============================================================================="
    )

    Log.d("ScreenParameters", "centerX" + ":" + centerCoordinateX.toString()) //2800
    Log.d("ScreenParameters", "centerY" + ":" + centerCoordinateY.toString()) //1650
    Log.d(
        "ScreenParameters",
        "=============================================================================="
    )


    Log.d(
        "ScreenParameters",
        "X dimesion (pixel/inch)" + ":" + ScreenWidth_fromResourcesXdpi.toString()
    ) //254.29
    Log.d(
        "ScreenParameters",
        "Y dimesion (pixel/inch)" + ":" + ScreenHeight_fromResourcesYdpi.toString()
    ) //261.47
    Log.d(
        "ScreenParameters",
        "=============================================================================="
    )

    Log.d(
        "ScreenParameters",
        "applyDimension _ pixelPerMm(pixel/mm)" + ":" + pixelPerMm.toString()
    ) //10.011417
    Log.d(
        "ScreenParameters",
        "XpixelPerMm_fromxdpi _ pixelPerMm(pixel/mm)" + ":" + XpixelPerMm_fromxdpi.toString()
    ) //10.011417058509167
    Log.d(
        "ScreenParameters",
        "YpixelPerMm_fromxdpi  _ pixelPerMm(pixel/mm)" + ":" + YpixelPerMm_fromxdpi.toString()
    )  //10.294094536248155
    Log.d(
        "ScreenParameters",
        "=============================================================================="
    )

    Log.d(
        "ScreenParameters",
        "screenDensity_fromResources" + ":" + screenDensity_fromResources.toString()
    ) //2.125
    Log.d(
        "ScreenParameters",
        "screenDensityDpi_fromResources" + ":" + screenDensityDpi_fromResources.toString()
    )   //340

    Log.d(
        "ScreenParameters",
        "=============================================================================="
    )
    Log.d(
        "ScreenParameters",
        "currentUsing_pixelPerMm(pixel/mm)" + ":" + (1 / mmPerPixel).toString()
    )    // 10.318770637541276
    Log.d(
        "ScreenParameters",
        "currentUsing_mmPerPixel(mm/pixel)" + ":" + mmPerPixel.toString()
    )  // 0.09691076923076923


    Log.d(
        "ScreenParameters",
        "=============================================================================="
    )
    Log.d(
        "ScreenParameters",
        "adjsuted_pixelPerMm(pixel/mm)" + ":" + adjustedPixelPerMm.toString()
    )    // 10.318770637541276
    Log.d(
        "ScreenParameters",
        "adjsuted_mmPerPixel(mm/pixel)" + ":" + adjustedMmPerPixel.toString()
    )  // .09691076923076923

}

//換算單位段落
//由於 1. xml中僅接受輸入dp 2. fragment中call layoutParams時使用的單位為pixel
//若要確認排版位置一致，或是設定指定長度，需要進行換算

// 常數定義區段 : 此段落放不會改變的參數
// 例如總測驗次數/練習次數等等，方格大小、等等
//const val TARGET_BOX_SIZE = 2.00                   //待修改
//const val RESPONSE_DOT_SIZE = 3.00

val desireStart2TargetLengthInMM: Int = 100   //先設定10公分
val desiretargetBoxSizeInMM: Int = 20  //先設定2公分
val desiretargetStepInMM: Int = 5  //以5mm間隔

// 新增兩個常數 用來校正 pixel/dp 顯示上的誤差
val screenCalibration = 1.4 //sqrt(Resources.getSystem().displayMetrics.density) //1.4
val pixelCalibration = (10 / 9.5)

// mm >>  pixel
val targetBoxSize =
    (desiretargetBoxSizeInMM * pixelPerMm * screenCalibration).toInt()   //先設定 2公分  //暫定 >> 需要計算實際長度 vs pixel
val targetStep = (desiretargetStepInMM * pixelPerMm * screenCalibration).toInt()

val Center2Target = ((desireStart2TargetLengthInMM / 2) * pixelPerMm * pixelCalibration).toInt()
val Center2Start = ((desireStart2TargetLengthInMM / 2) * pixelPerMm * pixelCalibration).toInt()

// dp = (width in pixels * 160) / screen density
// pixel = (dp * screen density)/160

// TODO: 若之後 目標大小要依照公分指定，從這邊修改
val desireWidthOfTitleInMM: Int = 30   //先設定3公分
val desireWidthOfTandSInMM: Int = 10 //先設定1公分
val desirePenWidthOfTandSInMM: Int = 3  //以3mm大
val widthOfTandSInPixel =
    (desireWidthOfTandSInMM * pixelPerMm * screenCalibration).toInt()   //先設定 2公分  //暫定 >> 需要計算實際長度 vs pixel
val PenWidthOfTandSInPixel = (desirePenWidthOfTandSInMM * pixelPerMm * screenCalibration).toInt()
// TODO: 若之後 目標大小要依照公分指定，從這邊修改

val widthOfTitle = 400 //dp
val widthOfTandS = 50  //dp   finger Context
val cantextPenWidthOfTandS = 50 //dp  20 + 15padding + 15 padding

val titleCalibrate = viewAdjustDp2Pixel(widthOfTitle) / 2

// T= target S= Start
val TandSCalibrate = viewAdjustDp2Pixel(widthOfTandS) / 2
val penTandSCalibrate = viewAdjustDp2Pixel(cantextPenWidthOfTandS) / 2
var calibrateWidth = TandSCalibrate //default


fun viewAdjustDp2Pixel(dpWidthOfView: Int): Int {
    return dpWidthOfView * (pixelDensity / 160).toInt()
    //px =  dp * (dpi / 160)
    //dp = (px * 160) / dpi)
}

fun viewAdjustPixel2Dp(pixelWidthOfView: Int): Int {
    return (pixelWidthOfView * 160) / pixelDensity.toInt()
    //px =  dp * (dpi / 160)
    //dp = (px * 160) / dpi)
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
    Log.d("ScreenParameters", logString)

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


// TODO: 目前設定VIEW寬度會有誤差  給50mm 出來會 3.7cm
// https://developer.android.com/training/multiscreen/screendensities
// https://www.altova.com/manual/MobileTogether/mobiletogetherdesigner/mtdobjsfeatures_sizes.html
// px = dp * (dpi / 160)

val desireWidthOfTargeAreaInMM = 50 //先訂五公分
val desireWidthOfFrameInMM = 5

val widthOfTargetAreaInPixel = (desireWidthOfTargeAreaInMM * pixelPerMm * screenCalibration).toInt()

//val widthOfTargetAreaInDp =  viewAdjustPixel2Dp (widthOfTargetAreaInPixel).toInt()
val calibrateAreaWidthInPixel = widthOfTargetAreaInPixel / 2
//viewAdjustDp2Pixel()

val widthOfFrameInPixel =
    ((desireWidthOfTargeAreaInMM + desireWidthOfFrameInMM * 2) * pixelPerMm * screenCalibration).toInt()

//val widthOfTargetAreaInDp =  viewAdjustPixel2Dp (widthOfTargetAreaInPixel).toInt()
val calibrateFrameWidthInPixel = widthOfFrameInPixel / 2

@SuppressLint("StaticFieldLeak")
lateinit var TargetArea: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var TargetAreaFrame: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var stimuliTypeSpinner: Spinner

//TODO: 以下三行要貼到其他 fragment中
//<variable
//name="universalFunction"
//type="com.example.kinesthesia_first_attempt.UniversalFunctionsKt" />

//TODO: 以下Spinner要貼到其他 fragment中
//<Spinner
//android:id="@+id/stimuliType_list"
//android:layout_width="200dp"
//android:layout_height="50dp"
//android:addStatesFromChildren="false"
//android:contentDescription="@string/hint_plz_set_stimuliType"
//android:visibility="visible"
//app:layout_constraintEnd_toEndOf="@id/confirm_trial"
//app:layout_constraintStart_toStartOf="@id/confirm_trial"
//app:layout_constraintTop_toBottomOf="@id/confirm_trial"
//tools:ignore="SpeakableTextPresentCheck" />


lateinit var mainViewModel: MainViewModel

@SuppressLint("StaticFieldLeak")
lateinit var mContextKIN: Context

@SuppressLint("StaticFieldLeak")
lateinit var mActivityKIN: Activity


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

var filePathStr: String = ""
//全域變數宣告，不然無法讀取到class給的資料


// 最大練習次數 或每個方向的測驗次數
const val MAX_PRACTICE_TRIAL = 8
const val MAX_FORMAL_TRIAL = 1 //測試流程用
//const val MAX_FORMAL_TRIAL = 5

//練習測驗次數上限變數
var maxTrailDesire = 0

var trialCondition =
    listOf<String>("Start Position", "Test Position", "Rest Position", "Response Position")
var condition: String = ""

//測驗選項
//var testCondition: String = "Practice"
val testConditionList =
    listOf<String>("Practice", "Formal", "Addition", "Non_dominant", "AutoRecord")
lateinit var testCondition: String  //進到各分頁後 重新宣告

val practiceTrialCountList = arrayListOf<String>("8", "7", "6", "5", "4", "3", "2", "1")

//測驗方式

//測驗方向變數
/* val directionList = arrayListOf<String>(
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
var currentTestDirection: String = ""

var TestingFinishedList = arrayListOf<String>()
var finishedContextList = arrayListOf<String>()
var finishedStimuliList = arrayListOf<String>()

var currentTestContext: String = ""
val contextList = arrayListOf<String>("請選情境", "Finger", "Pen")

var stimuliList = arrayListOf<String>("請選刺激", "VAP2AP", "AP2AP", "PP2AP")
var stimuliType = stimuliList[0]


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
var positionData = StringBuffer()

//顯示表現用暫存LIST
var scoreListForDisplay = listOf<Float>(0f, 0f, 0f, 0f, 0f)

// 存檔相關變數宣告
//lateinit var arrayListOf8Trial: ArrayList<List<Float>>
//lateinit var arrayListOf5Trial: ArrayList<List<Float>>
lateinit var arrayListOfTrials: ArrayList<List<Float>>


var trial1list = listOf<Float>(
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f,
    0f
) // 4*2 四次表現(X/Y) + 5個表現參數 + 6個given position = 19
var trial2list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial3list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial4list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial5list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial6list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial7list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
var trial8list =
    listOf<Float>(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

// 宣告位置調整參考資料：https://medium.com/globant/why-oncreate-of-activity-and-oncreateview-of-fragment-is-not-needed-anymore-6cdfc331102
// 11/15 View 相關宣告測試，嘗試避免重複宣告 requireView().findViewById
@SuppressLint("StaticFieldLeak")
lateinit var start: TextView

@SuppressLint("StaticFieldLeak")
lateinit var test: TextView

@SuppressLint("StaticFieldLeak")
lateinit var rest: TextView

@SuppressLint("StaticFieldLeak")
lateinit var response: TextView

@SuppressLint("StaticFieldLeak")
lateinit var trialCountView: TextView     //測驗次數textView

@SuppressLint("StaticFieldLeak")
lateinit var recordingButton: Button        //找到測驗按鈕

@SuppressLint("StaticFieldLeak")
lateinit var instructionText: TextView      //找到指導語textView

@SuppressLint("StaticFieldLeak")
lateinit var performanceTitle: TextView

@SuppressLint("StaticFieldLeak")
lateinit var directionSpinner: Spinner

@SuppressLint("StaticFieldLeak")
lateinit var trialInputSpinner: Spinner

@SuppressLint("StaticFieldLeak")
lateinit var contextSpinner: Spinner

//此段重要，為測驗方向和目標的View宣告，正式測驗中，需要新增斜向箭頭
@SuppressLint("StaticFieldLeak")
lateinit var fingerTarget: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var fingerStartPoint: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var fingerDownArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var penTarget: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var penStartPoint: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var penDownArrow: ImageView

//此段重要，為測驗方向和目標的View宣告，正式測驗中，需要新增斜向箭頭
@SuppressLint("StaticFieldLeak")
lateinit var penUpArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var penUpLeftArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var penUpRightArrow: ImageView

//@SuppressLint("StaticFieldLeak") lateinit var penDownArrow: ImageView
@SuppressLint("StaticFieldLeak")
lateinit var penDownLeftArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var penDownRightArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var fingerUpArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var fingerUpLeftArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var fingerUpRightArrow: ImageView

//@SuppressLint("StaticFieldLeak") lateinit var fingerDownArrow: ImageView
@SuppressLint("StaticFieldLeak")
lateinit var fingerDownLeftArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var fingerDownRightArrow: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var fingerRandomTargetView: ImageView

@SuppressLint("StaticFieldLeak")
lateinit var penRandomTargetView: ImageView

//正式測驗中ImageView，參數宣告，用於調整VIEW 位置用
lateinit var penTargetParams: ViewGroup.MarginLayoutParams
lateinit var penStartParams: ViewGroup.MarginLayoutParams
lateinit var fingerTargetParams: ViewGroup.MarginLayoutParams
lateinit var fingerStartParams: ViewGroup.MarginLayoutParams

lateinit var titleParams: ViewGroup.MarginLayoutParams

lateinit var fingerRandomTargetParams: ViewGroup.MarginLayoutParams
lateinit var penRandomTargetParams: ViewGroup.MarginLayoutParams


@SuppressLint("StaticFieldLeak")
lateinit var currentPosition: TextView

@SuppressLint("StaticFieldLeak")
lateinit var inAirText: TextView

@SuppressLint("StaticFieldLeak")
lateinit var touchBoard: TouchBoard

@SuppressLint("StaticFieldLeak")
lateinit var Score: TextView

//// 原本 checktime 中沒有提前宣告的view
@SuppressLint("StaticFieldLeak")
lateinit var countAndHint: TextView

////manageVisibility 中沒有提前宣告的view
@SuppressLint("StaticFieldLeak")
lateinit var selectButton: Button

@SuppressLint("StaticFieldLeak")
lateinit var navControllerKIN: NavController    //必須，用於從public function呼叫navControllerKIN


//以下嘗試放通用的function，供各個fragment使用
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

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 以下為隱藏/顯示選單用的功能
//參考資料 : https://stackoverflow.com/questions/37380587/android-how-to-hide-the-system-ui-properly
fun u_hideSystemUI(decorView: View) {
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

fun u_showSystemUI(decorView: View) {
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
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

fun u_saveInAirDataToCSV(inAirData: StringBuffer) {
    val outputInAirData = inAirData.toString().replace("\r", "").split("\n")
    //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
    var outputFileName = ""


    // 存檔: name,List,flag
    when (testCondition) {
        testConditionList[0] -> {
            outputFileName =
                testCondition + "_" + stimuliType + "_" + currentTestContext + "_Performance_$practiceTime" + "_InAirTrial_" + currentTrial.toString() + ".csv"
        }
        testConditionList[1] -> {
            outputFileName =
                "Dominant_" + testCondition + "_" + stimuliType + "_" + currentTestContext + "_" + currentTestDirection + "_InAirTrial_" + currentTrial.toString() + ".csv"
        }
        testConditionList[2] -> {
            outputFileName =
                "Dominant_" + testCondition + "_" + stimuliType + "_" + currentTestContext + "_" + currentTestDirection + "_InAirTrial_" + currentTrial.toString() + ".csv"
        }

        testConditionList[3] -> {
            outputFileName =
                "nonDominant_" + testCondition + "_" + stimuliType + "_" + currentTestContext + "_" + currentTestDirection + "_InAirTrial_" + currentTrial.toString() + ".csv"
        }

    }



    u_outputInAirCsv(outputFileName, outputInAirData, 0)
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
    Toast.makeText(
        mContextKIN,
        "{$testCondition}_InAir_Trial_$currentTrial 儲存成功",
        Toast.LENGTH_SHORT
    ).show()
    //Log.d("data", "outCSV Success")
}  // sample from HW
//以上InAir相關
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//以下 CSV & 存檔相關
fun u_combineList(): ArrayList<List<Float>> {
    lateinit var tempList: ArrayList<List<Float>>
    when (testCondition) {
        testConditionList[0] -> {
            tempList = arrayListOf(
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
        testConditionList[1] -> {
            tempList = arrayListOf(
                trial1list,
                trial2list,
                trial3list,
                trial4list,
                trial5list,
            )
        }
        testConditionList[2] -> {
            tempList = arrayListOf(
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
        testConditionList[3] -> {
            tempList = arrayListOf(
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

    }
    return tempList
}

fun u_arrangeData(
    TargetStringBuffer: StringBuffer,
    targetList: ArrayList<List<Float>>
): StringBuffer {

    for (index in 0..((targetList.size) - 1)) {  //選一個trial
        TargetStringBuffer.append(index + 1) //輸入trial數
        TargetStringBuffer.append(",")

        for (indexCell in 0..(((targetList[index]).size) - 1)) {  //選定trial，讀取該trial 每一格數據
            TargetStringBuffer.append(targetList[index][indexCell])
            TargetStringBuffer.append(",")
        }

        TargetStringBuffer.append("\r\n")
    }
    return TargetStringBuffer
}


fun u_savePerformanceToCSV() {
    //call combineList 整理trialData
    arrayListOfTrials = u_combineList()
    //call function 將List排進buffer
    u_arrangeData(positionData, arrayListOfTrials)
    //切割buffer
    val outputPositionData = positionData.toString().replace("\r", "").split("\n")

    //檔案名稱 準備fileName: p.s.filePath在outputCsv中已經準備好
    //TODO: 修改存檔檔名判斷式 ( 非慣用手、補測、自動化、刺激給法，這些都要更新)
    var outputFileName = ""

    when (testCondition) {
        testConditionList[0] -> {
            outputFileName =
                testCondition + "_" + stimuliType + "_" + currentTestContext + "_Performance_$practiceTime" + ".csv"
        }
        testConditionList[1] -> {
            outputFileName =
                "Dominant_" + testCondition + "_" + stimuliType + "_" + currentTestContext + "_" + currentTestDirection + "_Performance.csv"
        }
        testConditionList[2] -> {
            outputFileName =
                "Dominant_" + testCondition + "_" + stimuliType + "_" + currentTestContext + "_" + currentTestDirection + "_Performance.csv"
        }
        testConditionList[3] -> {
            outputFileName =
                "nonDominant_" + testCondition + "_" + stimuliType + "_" + currentTestContext + "_" + currentTestDirection + "_Performance.csv"
        }

    }

    // 存檔: name,List,flag
    u_outputCsv(outputFileName, outputPositionData, 0)
}   //存檔要改成Direction名稱

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

    val givenPositionTitleList = listOf(
        "Given Start Position X", "Given Start Position Y",
        "Given Target Position X", "Given Target Position Y",
        "Given Random Target Position X", "Given Random Target Position Y",
    )

    //合併title
    val allTitle: MutableList<String> = mutableListOf()
    allTitle.addAll(titleList)
    allTitle.addAll(scoreTitleList)
    allTitle.addAll(givenPositionTitleList)


    // 放入標題，使用迴圈，避免前後出現[]
    for (i in 0..(allTitle.size - 1)) {
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
        mContextKIN,
        "$stimuliType x $testCondition x $currentTestContext x $currentTestDirection 測驗表現儲存成功",
        Toast.LENGTH_SHORT
    ).show()
    Log.d("data", "outCSV Success")
}  // sample from HW
//以上 CSV存檔相關
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//以下測驗流程管理相關

fun u_pressButton() {
    buttonPressedCountsInATrial++      //每按一次按鈕+1
    Log.d("X/Y/面積/長軸/短軸：inFragment", "$startX  $startY  $bb  $b1  $b2")
    u_recordPosition()   //儲存位置，並管理測驗流程，直接讀取全域變數
    u_changeText()       //更動text   //11/23改為global
    u_checkTime()   //計時// 11/21 改global

    if (buttonPressedCountsInATrial == 1) {
        getGivenPosition()  //第1次按鈕時抓given position
    }

    if (buttonPressedCountsInATrial == 4) {
        scoreListForDisplay = u_calculateTrialScoreP()   //計算測驗表現 (RE*2，AE*3)
        u_displayScoreInText(scoreListForDisplay, 1, Score)  //更新text內容 //11/21 更新為 global
        u_clearScoreList()
    } else {
        u_displayScoreInText(scoreListForDisplay, 0, Score)  //11/21 更新為 global
    }

    if (buttonPressedCountsInATrial == 5) {

        //0912測試存InAir
        if (currentTestContext == "Pen") {
            u_saveInAirDataToCSV(inAirData) //  11/11新版，未驗證
            //TODO: 要驗證 INAir存檔相關程式的輸出，也要嘗試改Finger測驗時，有沒辦法抓到時間&座標，用來算MT
        }

        u_clearInAir() // 11/21 //11/11新版，未驗證
        u_addTrialsCount() // 完成一次測驗練習   //11/21

        when (testCondition) {
            testConditionList[0] -> {
                // 練習題，不需要重設位置
            }
            testConditionList[1] -> {
                // 正式測驗，除了每一方向的第5次，都要重設位置
                if (trialCount == 5) { //第五trail結束不用再設目標
                } else {
                    u_setTargetPosition()// 重設目標位置
                }
            }
            testConditionList[2] -> {
                if (trialCount == 5) { //第五trail結束不用再設目標
                } else {
                    u_setTargetPosition()// 重設目標位置
                }
            }
            testConditionList[3] -> {
                if (trialCount == 5) { //第五trail結束不用再設目標
                } else {
                    u_setTargetPosition()// 重設目標位置
                }
            }
            testConditionList[4] -> {
                if (trialCount == 5) { //第五trail結束不用再設目標
                } else {
                    u_setTargetPosition()// 重設目標位置
                }
            }

        }

        u_saveCurrentTrialRecord()
        u_clearCurrentTrialRecord() //11/11新版，未驗證
        clearGivenPosition()

        if (testCondition == testConditionList[4]){
            //自動化測驗
            auto_checkTrialLimit()
        }else{
            u_checkTrialLimit()  //檢查是否達到練習次數
        }


        buttonPressedCountsInATrial = 0
    }
    return
}

fun u_addTrialsCount() {
    trialCount++
    practiceTrialsCount++
    currentTrial++
}

fun u_checkTime() {
    recordingButton.visibility = View.INVISIBLE

    //確認是否需要倒數  millisInFuture
    var millisInFuture: Long
    millisInFuture = 1000

    //TODO: 之後要根據測驗需求，調整每個階段的時間
    when (buttonPressedCountsInATrial) {
        0 -> {
            millisInFuture = 1000
        }  //之後要看按鈕次數改回4000
        1 -> {
            millisInFuture = 1000
        }
        2 -> {
            millisInFuture = 1000
        }
        3 -> {
            millisInFuture = 1000
        }
        4 -> {
            millisInFuture = 1000
        }
        5 -> {
            millisInFuture = 0
        }
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
            //TODO: 之後要改回聲音 預設為80
            val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 0)
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

//確認選擇方向、情境、並更新測驗紀錄    //移植補測後 刪掉判斷是否有選過

fun u_confirmSelection() {
    // val testConditionList = listOf<String>("Practice", "Formal", "Addition","Non_dominant","AutoRecord")
    when (testCondition) {
        testConditionList[0] -> {
            Toast.makeText(mContextKIN, "開始測驗練習", Toast.LENGTH_SHORT).show()
            u_changeText()
            u_manageVisibility(0)  //顯示觸控板及記錄紐
        }
        testConditionList[1] -> {
            val stimuliChecked = u_checkStimuliInput()
            val contextChecked = u_checkContextInput()
            val directionChecked = u_checkDirectionInput()

            if (stimuliChecked == 1) {
                if (contextChecked == 1) {
                    if (directionChecked == 1) {
                        //TestingFinishedList.add(currentTestDirection)
                        u_randomThePosition()
                        u_setTargetPosition()

                        u_changeText()
                        u_manageVisibility(0)  //顯示觸控板及記錄紐
                        Toast.makeText(
                            mContextKIN,
                            "開始測驗，項目： $stimuliType & $currentTestContext & $currentTestDirection ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        //TODO: 確認新頁面要檢核的項目
        testConditionList[2] -> {}
        testConditionList[3] -> {}
        testConditionList[4] -> {
            //TODO: 新增 隨機測驗方向的 function，可以連續自動執行測驗
            //TODO: u_confirmSelection　這邊需要增加一個新函式（製作測驗順序隨機列表，每次測驗結束，只要還沒測完ＬＩＳＴ，就再觸發這邊，才能自動化測驗
            //TODO: 新增Auto Test Function
            // 0. 隱藏Spinner
            // 1. 產生隨機測驗順序，顯示到對話框 (參考 demographic對話框)
            // 2. 檢查測驗進度，輸入測驗選項 (仿照 confirmselection 正式測驗
            // 3. 隨機測驗目標、設定測驗目標
            // 4. 顯示文字/測驗成績/測驗按鈕
            // 5. 顯示測驗開始提示
            u_randomTheAutoTestList()
            executeAutoTestList()

            u_changeText()
            //  u_manageVisibility(0) 這個應該要包到 executeAutoTestList中 (這邊只會經過一次)
            u_manageVisibility(0)  //顯示觸控板及記錄紐

            Toast.makeText(
                mContextKIN,
                "開始測驗，項目： $stimuliType & $currentTestContext & $currentTestDirection ",
                Toast.LENGTH_SHORT)
        }
    }
}

fun u_checkContextInput(): Int {
    u_checkContextTested()//避免bug可以跳出
    var flag = 0
    if (currentTestContext == contextList[0]) {
        Toast.makeText(mContextKIN, "測驗情境尚未設定", Toast.LENGTH_SHORT).show()
    } else {
        if (finishedContextList.contains(currentTestContext)) {
            Toast.makeText(mContextKIN, "此情境已測過", Toast.LENGTH_SHORT).show()
        } else {
            flag = 1
        }
    }
    return flag
}

fun u_checkDirectionInput(): Int {
    u_checkDirectionTested()  //避免bug可以跳出
    var flag = 0
    if (currentTestDirection == directionList[0]) {
        Toast.makeText(mContextKIN, "測驗方向尚未設定", Toast.LENGTH_SHORT).show()
    } else {
        //若有選方向 > /check condition
        if (TestingFinishedList.contains(currentTestDirection)) {
            //若選過>> toast >>return
            Toast.makeText(mContextKIN, "此方向已測過", Toast.LENGTH_SHORT).show()
        } else {
            //若沒選過  >> toast >> update view
            //TestingFinishedList.add(currentTestDirection)
            flag = 1
        }
    }
    return flag
}

fun u_checkStimuliInput(): Int {
    u_checkStimuliTested() //避免bug可以跳出
    var flag = 0
    if (stimuliType == stimuliList[0]) {
        Toast.makeText(mContextKIN, "測驗刺激尚未設定", Toast.LENGTH_SHORT).show()
    } else {
        if (finishedStimuliList.contains(stimuliType)) {
            Toast.makeText(mContextKIN, "此刺激已測過", Toast.LENGTH_SHORT).show()
        } else {
            flag = 1
        }
    }
    return flag
}

fun u_checkContextTested() {
    val checkList = arrayListOf<String>("Finger", "Pen")
    if (finishedContextList.toSet() == checkList.toSet()) {
        finishedStimuliList.add(stimuliType)

        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_context)
            ) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_stimuli)) { _, _ ->
                finishedContextList = arrayListOf<String>()
                u_manageVisibility(1)
                Toast.makeText(mContextKIN, "已完成該刺激方式中，兩種測驗情境，請更換測驗刺激", Toast.LENGTH_SHORT).show()
                u_checkStimuliTested()
                //u_goBackToMenu()
            }
            .show() //creates and then displays the alert dialog.
    }
}

fun u_checkDirectionTested() {
    //目前暫定需要八種全測
    val checkList = arrayListOf<String>(
        "L_Up",
        "L_Up_Right",
        "R_Up",
        "R_Up_Left",
    )

    //當四種都測完
    if (TestingFinishedList.toSet() == checkList.toSet()) {
        finishedContextList.add(currentTestContext)
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_direction)) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_context))
            { _, _ ->
                // finishedContextList.add(currentTestContext)
                TestingFinishedList = arrayListOf<String>() //清除List >> 準備測另種情境
                u_manageVisibility(1)
                Toast.makeText(mContextKIN, "已完成該測驗情境中之所有測驗方向，請更換情境", Toast.LENGTH_SHORT).show()
                u_checkContextTested()
            }
            .show() //creates and then displays the alert dialog.
    }
}//判斷是否所有方向都測過

fun u_checkDirectionTested2() {
    //contextSpinner = requireView()!!.findViewById<View>(R.id.context_list) as Spinner
    //目前暫定需要八種全測
    val checkList = arrayListOf<String>("L_Down", "L_Down_Right", "R_Down", "R_Down_Left")
    //當四種都測完
    if (TestingFinishedList.toSet() == checkList.toSet()) {
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_direction)
            ) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_context)) { _, _ ->
                finishedContextList.add(currentTestContext)
                TestingFinishedList = arrayListOf<String>() //清除List >> 準備測另種情境
                contextSpinner.visibility = View.VISIBLE
                Toast.makeText(mContextKIN, "更換情境", Toast.LENGTH_SHORT).show()
                //checkContextTested() //確認兩種情境是否測驗完成
            }
            .show() //creates and then displays the alert dialog.
    }
}//判斷是否所有方向都測過 備用只測後四種

fun u_checkStimuliTested() {
    //TODO: 後續自動化前 要寫判斷是否測過的函式，用手動測試是否能正常運作
    val checkList = arrayListOf<String>("VAP2AP", "AP2AP", "PP2AP")
    if (finishedStimuliList.toSet() == checkList.toSet()) {
        //finishedcontextList = arrayListOf<String>() //清除List >> 準備測另種情境
        MaterialAlertDialogBuilder(mActivityKIN)
            .setTitle(mContextKIN.resources.getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                mContextKIN.resources.getString(R.string.test_dialog_message_finished_all_stimuliType)
            ) //Set the message to show the data
            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
            .setPositiveButton(mContextKIN.resources.getString(R.string.dialog_back_to_menu)) { _, _ ->
                Toast.makeText(mContextKIN, "請查驗資料或補充測驗", Toast.LENGTH_SHORT).show()
                TestingFinishedList = arrayListOf<String>() //方向
                finishedContextList = arrayListOf<String>() //情境
                finishedStimuliList = arrayListOf<String>() //刺激
                u_goBackToMenu()
            }
            .show() //creates and then displays the alert dialog.
    }
}

@SuppressLint("SetTextI18n")
fun u_checkTrialLimit() {
    var tempCount = 0
    // 確認 目前condition，設定參數
    when (testCondition) {
        testConditionList[0] -> {
            tempCount = practiceTrialsCount
            //maxTrailDesire = MAX_PRACTICE_TRIAL
        }
        testConditionList[1] -> {
            tempCount = trialCount
            maxTrailDesire = MAX_FORMAL_TRIAL
        }
        testConditionList[2] -> { //Addition
            tempCount = trialCount
            //maxTrailDesire = MAX_PRACTICE_TRIAL
        }
        testConditionList[3] -> { //Addition
            tempCount = trialCount
            //maxTrailDesire = MAX_PRACTICE_TRIAL
        }
    }

    if (tempCount >= maxTrailDesire) {  // practiceTrialsCount > MAX_PRACTICE_TRIAL
        when (testCondition) {
            testConditionList[0] -> {
                practiceTime++  //增加練習完成次數
                u_updatePracticeTimeToViewModel()

                MaterialAlertDialogBuilder(mActivityKIN)
                    .setTitle(mContextKIN.resources.getString(R.string.practice_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                    .setMessage(mContextKIN.resources.getString(R.string.practice_dialog_message)) //Set the message to show the data
                    .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                    .setNegativeButton(mContextKIN.resources.getString(R.string.practice_dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                        //u_savePracticePerformanceToCSV()//儲存測驗表現
                        u_savePerformanceToCSV()
                        u_clearRecord()  // 清除測驗表現>> 還沒寫完
                        trialCountView.text = "練習次數: $currentTrial / $maxTrailDesire"
                        u_manageVisibility(1)
                        Toast.makeText(mContextKIN, "再試一次", Toast.LENGTH_SHORT).show()
                    }
                    .setPositiveButton(mContextKIN.resources.getString(R.string.dialog_back_to_menu)) { _, _ ->
                        //u_savePracticePerformanceToCSV()// 儲存測驗表現
                        u_savePerformanceToCSV()
                        u_clearRecord()  // 清除測驗表現>> 還沒寫完
                        u_goBackToMenu()// 前往測驗選單 ( 維持local) >>可以寫判斷式　改成when根據測驗頁面，換指令　
                    }
                    .show() //creates and then displays the alert dialog.
            }

            testConditionList[1] -> {
                TestingFinishedList.add(currentTestDirection)
                MaterialAlertDialogBuilder(mActivityKIN)
                    .setTitle(mContextKIN.resources.getString(R.string.test_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                    .setMessage(
                        mContextKIN.resources.getString(R.string.test_dialog_message)
                    ) //Set the message to show the data
                    .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                    .setPositiveButton(mContextKIN.resources.getString(R.string.test_dialog_next_condition)) { _, _ ->
                        u_savePerformanceToCSV()//儲存測驗表現
                        u_clearRecord()  // 清除測驗表現
                        trialCountView.text = "測驗次數: $currentTrial / $maxTrailDesire "
                        u_manageVisibility(1)
                        u_checkDirectionTested() // 確認完成所有測驗方向
                        Toast.makeText(mContextKIN, "更換方向", Toast.LENGTH_SHORT).show()
                    }
                    .show() //creates and then displays the alert dialog.
            }

            testConditionList[2] -> {
                TestingFinishedList.add(currentTestDirection)
                MaterialAlertDialogBuilder(mActivityKIN)
                    .setTitle(mContextKIN.resources.getString(R.string.addition_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                    .setMessage(mContextKIN.resources.getString(R.string.addition_dialog_message)) //Set the message to show the data
                    .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
                    .setNegativeButton(mContextKIN.resources.getString(R.string.dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                        //u_savePracticePerformanceToCSV()//儲存測驗表現
                        u_savePerformanceToCSV()
                        u_clearRecord()  // 清除測驗表現>> 還沒寫完
                        trialCountView.text = "練習次數: $currentTrial / $maxTrailDesire"
                        u_manageVisibility(1)
                        Toast.makeText(mContextKIN, "再試一次", Toast.LENGTH_SHORT).show()
                    }
                    .setPositiveButton(mContextKIN.resources.getString(R.string.dialog_back_to_menu)) { _, _ ->
                        //u_savePracticePerformanceToCSV()// 儲存測驗表現
                        u_savePerformanceToCSV()
                        u_clearRecord()  // 清除測驗表現>> 還沒寫完
                        u_goBackToMenu()// 前往測驗選單 ( 維持local) >>可以寫判斷式　改成when根據測驗頁面，換指令　
                    }
                    .show() //creates and then displays the alert dialog.
            }

            testConditionList[3] -> {
                TestingFinishedList.add(currentTestDirection)
                //TODO:如果要測的話要改成跟正式測驗一樣
                MaterialAlertDialogBuilder(mActivityKIN)
                    .setTitle(mContextKIN.resources.getString(R.string.nondominant_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                    .setMessage(mContextKIN.resources.getString(R.string.nondominant_dialog_message)) //Set the message to show the data
                    .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
                    .setNegativeButton(mContextKIN.resources.getString(R.string.dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                        //u_savePracticePerformanceToCSV()//儲存測驗表現
                        u_savePerformanceToCSV()
                        u_clearRecord()  // 清除測驗表現>> 還沒寫完
                        trialCountView.text = "練習次數: $currentTrial / $maxTrailDesire"
                        u_manageVisibility(1)
                        Toast.makeText(mContextKIN, "再試一次", Toast.LENGTH_SHORT).show()
                    }
                    .setPositiveButton(mContextKIN.resources.getString(R.string.dialog_back_to_menu)) { _, _ ->
                        //u_savePracticePerformanceToCSV()// 儲存測驗表現
                        u_savePerformanceToCSV()
                        u_clearRecord()  // 清除測驗表現>> 還沒寫完
                        u_goBackToMenu()//
                    }
                    .show() //creates and then displays the alert dialog.
            }

            testConditionList[4] -> {
                //TODO:回CALL自動化測驗執行，要確認自動化測驗流程沒問題

            }


        }

    }
}

fun u_updatePracticeTimeToViewModel() {
    if (practiceTrialsCount >= maxTrailDesire) {
        mainViewModel.setPracticeTime(practiceTime)
    }
} //儲存練習次數
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//清除測驗表現資料
fun u_clearCurrentTrialRecord(
) {
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

fun u_resetTrials() {
    when (testCondition) {
        testConditionList[0] -> {
            u_reset8Trial()
        }
        testConditionList[1] -> {
            u_reset5Trial()
        }
        testConditionList[2] -> {
            u_reset8Trial()
        }
        testConditionList[3] -> {
            u_reset8Trial()
        }
    }
}

fun u_reset8Trial() {
    for (n in 0..((arrayListOfTrials.size) - 1)) {
        arrayListOfTrials[n] = listOf<Float>(
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f
        )
    }
}

fun u_reset5Trial() {
    for (n in 0..((arrayListOfTrials.size) - 1)) {
        arrayListOfTrials[n] = listOf<Float>(
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f,
            0f
        )
    }
} //清除5trial

fun u_clearRecord() {
    u_clearCurrentTrialRecord() // 四個位置、startX/Y的全域變數
    trialCount = 0
    practiceTrialsCount = 0   //進入測驗練習後的練習次數
    currentTrial = 1
    u_resetTrials()
    positionData.setLength(0) //clean buffer
}

fun u_clearScoreList() {
    scoreListForDisplay = listOf<Float>(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
} //清除用來顯示的測驗表現分數List
/////////////////////////////////////////////////////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 將單次反應存入LIST
fun u_saveCurrentTrialRecord() {
    //確認目前practiceTrialsCount
    var tempCount = 0
    when (testCondition) {
        testConditionList[0] -> {
            tempCount = practiceTrialsCount
        }
        testConditionList[1] -> {
            tempCount = trialCount
        }
        testConditionList[2] -> {
            tempCount = trialCount
        }
        testConditionList[3] -> {
            tempCount = trialCount
        }
        testConditionList[4] -> {
            tempCount = trialCount
        }
    }

    when (tempCount) {
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
        tempScore[4],   //AE T2R
        givenStartPositionX.toFloat(),
        givenStartPositionY.toFloat(),
        givenTargetPositionX.toFloat(),
        givenTargetPositionY.toFloat(),
        givenRandomTargetPositionX.toFloat(),
        givenRandomTargetPositionY.toFloat()
    )

}

fun u_calculateTrialScoreP(): List<Float> {
    //Todo: 11/11 這邊的分數計算，可能要重新調整(given position)，因為起點/目標的給法不同
    //Todo: 11/21 也要新增 true given position 的輸入/

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
        test2ResponseAP < 0 -> {
            //performanceDescriptionAP = "Underestimated"
            relativeErrorAP = -absoluteErrorAP
        }
        test2ResponseAP > 0 -> {
            //performanceDescriptionAP = "Overestimated"
            relativeErrorAP = absoluteErrorAP
        }
        test2ResponseAP == 0f -> {
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

//以下位置記錄相關
// 此函數只處理紀錄位置，不處理位置的隨機分配 & 目標位置給定，不給輸入，直接取用全域變數
fun u_recordPosition() {
    when (buttonPressedCountsInATrial) {
        1 -> {
            condition = trialCondition[0]
            startPositionX = startX
            startPositionY = startY
            Log.d("positionData $condition", "$startPositionX  $startPositionY")
        }
        2 -> {
            condition = trialCondition[1]
            testPositionX = startX
            testPositionY = startY
            Log.d("positionData $condition", "$testPositionX  $testPositionY")
        }
        3 -> {
            condition = trialCondition[2]
            restPositionX = startX
            restPositionY = startY
            Log.d("positionData $condition", "$restPositionX  $restPositionY")
        }
        4 -> {
            condition = trialCondition[3]
            responsePositionX = startX
            responsePositionY = startY
            Log.d("positionData $condition", "$responsePositionX  $responsePositionY")
        }
        5 -> {
            condition = ""
            startX = 0.0f
            startY = 0.0f
        }
    }

    //getGivenPosition()
}

fun u_displayScoreInText(inputScoreList: List<Float>, flag: Int, Score: TextView) {
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


var givenStartPositionX = 0
var givenStartPositionY = 0
var givenTargetPositionX = 0
var givenTargetPositionY = 0
var givenRandomTargetPositionX = 0
var givenRandomTargetPositionY = 0

fun getGivenPosition() {
    lateinit var targetParams: ViewGroup.MarginLayoutParams
    lateinit var startParams: ViewGroup.MarginLayoutParams
    lateinit var randomTargetParams: ViewGroup.MarginLayoutParams
    var objectWidthCalibration: Int

    // 根據當前情境，指派測驗參數
    when (currentTestContext) {
        "Pen" -> {
            targetParams = penTargetParams
            startParams = penStartParams
            randomTargetParams = penRandomTargetParams

            objectWidthCalibration = penTandSCalibrate
        }
        "Finger" -> {
            targetParams = fingerTargetParams
            startParams = fingerStartParams
            randomTargetParams = fingerRandomTargetParams

            objectWidthCalibration = TandSCalibrate
        }
        else -> {
            targetParams = fingerTargetParams
            startParams = fingerStartParams
            randomTargetParams = fingerRandomTargetParams

            objectWidthCalibration = TandSCalibrate
        }  //沒有選擇時，預設為手指
    }

    // givenStartPositionX = startView.layoutParams
    givenStartPositionX = startParams.leftMargin + objectWidthCalibration
    givenStartPositionY = startParams.topMargin + objectWidthCalibration
    givenTargetPositionX = targetParams.leftMargin + objectWidthCalibration
    givenTargetPositionY = targetParams.topMargin + objectWidthCalibration
    givenRandomTargetPositionX = randomTargetParams.leftMargin + objectWidthCalibration
    givenRandomTargetPositionY = randomTargetParams.topMargin + objectWidthCalibration

    Log.d(
        "positionData Given",
        "givenStartX:$givenStartPositionX  givenStartY:$givenStartPositionY"
    )
    Log.d(
        "positionData Given",
        "givenTargetX:$givenTargetPositionX givenTargetY: $givenTargetPositionY"
    )
    Log.d(
        "positionData Given",
        "givenRandomTargetX:$givenRandomTargetPositionX givenRandomTargetY: $givenRandomTargetPositionY"
    )
}

fun clearGivenPosition() {
    givenStartPositionX = 0
    givenStartPositionY = 0
    givenTargetPositionX = 0
    givenTargetPositionY = 0
    givenRandomTargetPositionX = 0
    givenRandomTargetPositionY = 0
}

fun saveGivenPosition() {
    //TODO: 要新增 trueGivenStartPositionX & trueGivenStartPositionY
    //TODO: 要新增 trueGivenTargetPositionX & trueGivenTargetPositionY
    //TODO: 注意後面所有 位置紀錄 & 檔案輸出，都要更新這四個參數
    //TODO: 下面所有的 position 應該要新整成一個 float list
    //TODO: 或是先另外存一個襠名相同(情境代號註記)的EXCEL
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//view 顯示管理
fun u_checkContextAndLaunchView(context: String) {
    u_clearViews()
    u_setSquareOfTargetArea()
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
            when (testCondition) {
                testConditionList[0] -> {
                    when (currentTestContext) {
                        contextList[0] -> {
                            fingerTarget.visibility = View.VISIBLE
                            fingerStartPoint.visibility = View.VISIBLE
                            //fingerRandomTargetView.visibility = View.VISIBLE
                            //fingerUpArrow.visibility = View.VISIBLE
                            //fingerUpLeftArrow.visibility = View.VISIBLE
                            //fingerUpRightArrow.visibility = View.VISIBLE
                            fingerDownArrow.visibility = View.VISIBLE
                            //fingerDownLeftArrow.visibility = View.VISIBLE
                            //fingerDownRightArrow.visibility = View.VISIBLE
                        }
                        contextList[1] -> {
                            fingerTarget.visibility = View.VISIBLE
                            fingerStartPoint.visibility = View.VISIBLE
                            //fingerRandomTargetView.visibility = View.VISIBLE
                            //fingerUpArrow.visibility = View.VISIBLE
                            //fingerUpLeftArrow.visibility = View.VISIBLE
                            //fingerUpRightArrow.visibility = View.VISIBLE
                            fingerDownArrow.visibility = View.VISIBLE
                            //fingerDownLeftArrow.visibility = View.VISIBLE
                            //fingerDownRightArrow.visibility = View.VISIBLE
                        }
                        contextList[2] -> {
                            penTarget.visibility = View.VISIBLE
                            penStartPoint.visibility = View.VISIBLE
                            //penRandomTargetView.visibility = View.VISIBLE
                            //penUpArrow.visibility = View.VISIBLE
                            //penUpLeftArrow.visibility = View.VISIBLE
                            //penUpRightArrow.visibility = View.VISIBLE
                            penDownArrow.visibility = View.VISIBLE
                            //penDownLeftArrow.visibility = View.VISIBLE
                            //penDownRightArrow.visibility = View.VISIBLE
                        }
                    }
                    u_setDirection(currentTestDirection)
                }
                //testConditionList = listOf<String>("Practice", "Formal", "Addition","Non_dominant","Dominant","AutoRecord")
                testConditionList[1] -> {
                    u_setDirection(currentTestDirection)
                }
                testConditionList[2] -> {
                    u_setDirection(currentTestDirection)
                }
                testConditionList[3] -> {
                    u_setDirection(currentTestDirection)
                }
            }
            ///////////////////////////////////////////
            calibrateWidth = penTandSCalibrate
        }
        "Finger" -> {
            when (testCondition) {
                testConditionList[0] -> {
                    when (currentTestContext) {
                        contextList[0] -> {
                            fingerTarget.visibility = View.VISIBLE
                            fingerStartPoint.visibility = View.VISIBLE
                            //fingerRandomTargetView.visibility = View.VISIBLE
                            //fingerUpArrow.visibility = View.VISIBLE
                            //fingerUpLeftArrow.visibility = View.VISIBLE
                            //fingerUpRightArrow.visibility = View.VISIBLE
                            fingerDownArrow.visibility = View.VISIBLE
                            //fingerDownLeftArrow.visibility = View.VISIBLE
                            //fingerDownRightArrow.visibility = View.VISIBLE
                        }
                        contextList[1] -> {
                            fingerTarget.visibility = View.VISIBLE
                            fingerStartPoint.visibility = View.VISIBLE
                            //fingerRandomTargetView.visibility = View.VISIBLE
                            //fingerUpArrow.visibility = View.VISIBLE
                            //fingerUpLeftArrow.visibility = View.VISIBLE
                            //fingerUpRightArrow.visibility = View.VISIBLE
                            fingerDownArrow.visibility = View.VISIBLE
                            //fingerDownLeftArrow.visibility = View.VISIBLE
                            //fingerDownRightArrow.visibility = View.VISIBLE
                        }
                        contextList[2] -> {
                            penTarget.visibility = View.VISIBLE
                            penStartPoint.visibility = View.VISIBLE
                            //penRandomTargetView.visibility = View.VISIBLE
                            //penUpArrow.visibility = View.VISIBLE
                            //penUpLeftArrow.visibility = View.VISIBLE
                            //penUpRightArrow.visibility = View.VISIBLE
                            penDownArrow.visibility = View.VISIBLE
                            //penDownLeftArrow.visibility = View.VISIBLE
                            //penDownRightArrow.visibility = View.VISIBLE
                        }
                    }
                    u_setDirection(currentTestDirection)
                }
                testConditionList[1] -> {
                    u_setDirection(currentTestDirection)
                }
                testConditionList[2] -> {
                    u_setDirection(currentTestDirection)
                }
                testConditionList[3] -> {
                    u_setDirection(currentTestDirection)
                }
            }
            ///////////////////////////////////////////
            calibrateWidth = TandSCalibrate
        }
    }

    /// 正式測驗/練習測驗/其他頁面最大差別
    when (testCondition) {
        testConditionList[0] -> {
            trialInputSpinner.visibility = View.VISIBLE
            fingerRandomTargetView.visibility = View.GONE
            penRandomTargetView.visibility = View.GONE
        }
        testConditionList[1] -> {
            trialInputSpinner.visibility = View.GONE
        }
        testConditionList[2] -> {
            trialInputSpinner.visibility = View.VISIBLE
        }
        testConditionList[3] -> {
            trialInputSpinner.visibility = View.VISIBLE
        }

    }


} //輸入currentContext

fun u_manageVisibility(flag: Int) {

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
            directionSpinner.visibility = View.INVISIBLE
            stimuliTypeSpinner.visibility = View.INVISIBLE
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

            directionSpinner.visibility = View.VISIBLE
            stimuliTypeSpinner.visibility = View.VISIBLE
            //
            Score.text = ""
        }
    }
    when (testCondition) {
        testConditionList[0] -> {
            fingerRandomTargetView.visibility = View.GONE
            penRandomTargetView.visibility = View.GONE
            directionSpinner.visibility = View.GONE
        }
        testConditionList[1] -> {
            trialInputSpinner.visibility = View.GONE
        }
    }

}//管理測驗相關View顯示、可觸控與否

fun u_clearViews() {
    fingerTarget.visibility = View.GONE
    fingerStartPoint.visibility = View.GONE
    fingerRandomTargetView.visibility = View.GONE
    fingerUpArrow.visibility = View.GONE
    fingerUpLeftArrow.visibility = View.GONE
    fingerUpRightArrow.visibility = View.GONE
    fingerDownArrow.visibility = View.GONE
    fingerDownLeftArrow.visibility = View.GONE
    fingerDownRightArrow.visibility = View.GONE
    ///////////////////////////////////////////
    penTarget.visibility = View.GONE
    penStartPoint.visibility = View.GONE
    penRandomTargetView.visibility = View.GONE
    penUpArrow.visibility = View.GONE
    penUpLeftArrow.visibility = View.GONE
    penUpRightArrow.visibility = View.GONE
    penDownArrow.visibility = View.GONE
    penDownLeftArrow.visibility = View.GONE
    penDownRightArrow.visibility = View.GONE
} // 清除目標顯示view

@SuppressLint("SetTextI18n", "RestrictedApi")
fun u_changeText() {

    trialCountView.text = "測驗次數: $currentTrial / $maxTrailDesire"

    // Todo: 指導語部分之後可以改到 R-string中，也記得要改成新的指導語 ( VAP2AP, AP2AP, PP2AP)
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

            recordingButton.text = mContextKIN.resources.getString(R.string.next_trial)
            //recordingButton.text = "Save Trial"

            recordingButton.textSize = 24.toFloat()
            instructionText.text = instructionList[4]
        }
        "" -> {
            start.text = "起始位置："
            test.text = "目標位置："
            rest.text = "預備位置："
            response.text = "反應位置："
            recordingButton.text = mContextKIN.resources.getString(R.string.record_position)
            //recordingButton.text = "紀錄位置"

            recordingButton.textSize = 30.toFloat()

            instructionText.text = instructionList[0]
        }
        // getString reference >>導致閃退，棄用
        // https://stackoverflow.com/questions/4253328/getstring-outside-of-a-context-or-activity
    }
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 目標顯示管理
fun u_setTargetPosition() {
    var c2tX = 0
    var c2tY = 0

    when (currentTestDirection) {
        /*  "L_Up" -> {
              c2tX = centerCoordinateX - calibrateWidth - Center2Target
              c2tY = centerCoordinateY - calibrateWidth - Center2Target
          }
          "L_Up_Right" -> {
              c2tX = centerCoordinateX - calibrateWidth + Center2Target
              c2tY = centerCoordinateY - calibrateWidth - Center2Target
          }
          "R_Up" -> {
              c2tX = centerCoordinateX - calibrateWidth + Center2Target
              c2tY = centerCoordinateY - calibrateWidth - Center2Target
          }
          "R_Up_Left" -> {
              c2tX = centerCoordinateX - calibrateWidth - Center2Target
              c2tY = centerCoordinateY - calibrateWidth - Center2Target
          }*/

        "R_Up" -> {
            c2tX = centerCoordinateX - calibrateWidth - Center2Target
            c2tY = centerCoordinateY - calibrateWidth + Center2Target
        }
        "R_Up_Left" -> {
            c2tX = centerCoordinateX - calibrateWidth + Center2Target
            c2tY = centerCoordinateY - calibrateWidth + Center2Target
        }
        "L_Up" -> {
            c2tX = centerCoordinateX - calibrateWidth + Center2Target
            c2tY = centerCoordinateY - calibrateWidth + Center2Target
        }
        "L_Up_Right" -> {
            c2tX = centerCoordinateX - calibrateWidth - Center2Target
            c2tY = centerCoordinateY - calibrateWidth + Center2Target
        }
    }


    lateinit var targetView: ImageView
    // 根據當前情境，指派測驗參數
    when (currentTestContext) {
        "Pen" -> {
            targetView = penTarget
        }
        "Finger" -> {
            targetView = fingerTarget
        }
        else -> {
            targetView = fingerTarget
        }  //沒有選擇時，預設為手指
    }

    if (testCondition == testConditionList[0]) {
        //練習測中 不隱藏預設目標
    } else {
        //其他情境中，隱藏預設目標只顯示隨機目標
        when (stimuliType) {
            stimuliList[0] -> {
            }
            stimuliList[1] -> { //VAP2AP
                // 顯示隨機的目標，不顯示方格，不顯示灰目標 >> 在setTarget 寫判斷式
                targetView.visibility = View.INVISIBLE //隱藏預設目標
            }
            stimuliList[2] -> { //AP2AP
                targetView.visibility = View.INVISIBLE //隱藏預設目標
                // 顯示方格，不顯示任何目標 >> 在setTarget 寫判斷式
            }
            stimuliList[3] -> { //PP2AP
                // 顯示隨機的目標，不顯示方格，不顯示灰目標 >> 在setTarget 寫判斷式
                targetView.visibility = View.INVISIBLE //隱藏預設目標
            }
        }
    }

    u_setTargetRandomPosition(c2tX, c2tY)
}   //根據選項決定方向參數pixel

fun u_setTargetRandomPosition(c2tX: Int, c2tY: Int) {

    lateinit var randomTarget: ImageView
    lateinit var randomTargetParams: ViewGroup.MarginLayoutParams
    when (currentTestContext) {
        "Pen" -> {
            randomTarget = penRandomTargetView
        }
        "Finger" -> {
            randomTarget = fingerRandomTargetView
        }
    }

    if (stimuliType == stimuliList[2]) {
        randomTarget.visibility = View.INVISIBLE
    } else {
        randomTarget.visibility = View.VISIBLE
    }


    //val randomTargetView = requireView().findViewById<ImageView>(R.id.random_target)
    randomTargetParams = randomTarget.layoutParams as ViewGroup.MarginLayoutParams

    randomTargetParams.setMargins(
        c2tX + randWidth[trialCount],
        c2tY + randHeight[trialCount],
        0,
        0
    )
}  //在方向參數基礎上，加上隨機位置參數

fun u_setSquareOfTargetArea() {
    var c2AreaX = 0
    var c2AreaY = 0
    var c2FrameX = 0
    var c2FrameY = 0
    var arrowSize: Int //default

    lateinit var upArrow: ImageView
    lateinit var upLeftArrow: ImageView
    lateinit var upRightArrow: ImageView

    lateinit var downArrow: ImageView
    lateinit var downLeftArrow: ImageView
    lateinit var downRightArrow: ImageView

    if (stimuliType == stimuliList[2]) {
        arrowSize = 720

        if (testCondition == testConditionList[0]) {
            c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel
            c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel + Center2Target
            c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel
            c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel + Center2Target
        } else { //其他測驗中，依據方項選擇，選擇正確的目標框位置
            when (currentTestDirection) {
//                  "L_Up" -> {
//                      c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel - Center2Target
//                      c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel - Center2Target
//                      c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel - Center2Target
//                      c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel - Center2Target
//                  }
//                  "L_Up_Right" -> {
//                      c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel + Center2Target
//                      c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel - Center2Target
//                      c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel + Center2Target
//                      c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel - Center2Target
//                  }
//                  "R_Up" -> {
//                      c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel + Center2Target
//                      c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel - Center2Target
//                      c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel + Center2Target
//                      c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel - Center2Target
//                  }
//                  "R_Up_Left" -> {
//                      c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel - Center2Target
//                      c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel - Center2Target
//                      c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel - Center2Target
//                      c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel - Center2Target
//                  }
                //以下為為受測者方向出發的選項，程式描述為icon對於施測者的方向///
                "R_Up" -> {
                    c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel - Center2Target
                    c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel + Center2Target
                    c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel - Center2Target
                    c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel + Center2Target
                }
                "R_Up_Left" -> {
                    c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel + Center2Target
                    c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel + Center2Target
                    c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel + Center2Target
                    c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel + Center2Target
                }
                "L_Up" -> {
                    c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel + Center2Target
                    c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel + Center2Target
                    c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel + Center2Target
                    c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel + Center2Target
                }
                "L_Up_Right" -> {
                    c2AreaX = centerCoordinateX - calibrateAreaWidthInPixel - Center2Target
                    c2AreaY = centerCoordinateY - calibrateAreaWidthInPixel + Center2Target
                    c2FrameX = centerCoordinateX - calibrateFrameWidthInPixel - Center2Target
                    c2FrameY = centerCoordinateY - calibrateFrameWidthInPixel + Center2Target
                }
            }
        }

        //指定框框位置
        //TargetArea.visibility = View.VISIBLE
        TargetArea.layoutParams.width = widthOfTargetAreaInPixel
        TargetArea.layoutParams.height = widthOfTargetAreaInPixel
        (TargetArea.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
            c2AreaX,
            c2AreaY,
            0,
            0
        )
        //TargetAreaFrame.visibility = View.VISIBLE
        TargetAreaFrame.layoutParams.width = widthOfFrameInPixel
        TargetAreaFrame.layoutParams.height = widthOfFrameInPixel
        (TargetAreaFrame.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
            c2FrameX,
            c2FrameY,
            0,
            0
        )

    } else { //stimuliType ~= stimuliList[2]
        arrowSize = 900
        //Do nothing
    }

    // select arrow
    when (currentTestContext) {
        contextList[1] -> {//finger
            upArrow = fingerUpArrow
            upLeftArrow = fingerUpLeftArrow
            upRightArrow = fingerUpRightArrow

            downArrow = fingerDownArrow
            downLeftArrow = fingerDownLeftArrow
            downRightArrow = fingerDownRightArrow
        }
        contextList[2] -> {//pen
            upArrow = penUpArrow
            upLeftArrow = penUpLeftArrow
            upRightArrow = penUpRightArrow

            downArrow = penDownArrow
            downLeftArrow = penDownLeftArrow
            downRightArrow = penDownRightArrow
        }
        else -> {//default finger
            upArrow = fingerUpArrow
            upLeftArrow = fingerUpLeftArrow
            upRightArrow = fingerUpRightArrow

            downArrow = fingerDownArrow
            downLeftArrow = fingerDownLeftArrow
            downRightArrow = fingerDownRightArrow
        }
    }
    // resize arrow
    upArrow.layoutParams.width = arrowSize
    upLeftArrow.layoutParams.width = arrowSize
    upRightArrow.layoutParams.width = arrowSize
    downArrow.layoutParams.width = arrowSize
    downLeftArrow.layoutParams.width = arrowSize
    downRightArrow.layoutParams.width = arrowSize
    upArrow.layoutParams.height = arrowSize
    upLeftArrow.layoutParams.height = arrowSize
    upRightArrow.layoutParams.height = arrowSize
    downArrow.layoutParams.height = arrowSize
    downLeftArrow.layoutParams.height = arrowSize
    downRightArrow.layoutParams.height = arrowSize
    // resize arrow

    // check the input and decide if the Area should be displayed
    TargetArea.visibility = View.GONE
    TargetAreaFrame.visibility = View.GONE

    val contextChecked = u_checkContextInput()
    val directionChecked = u_checkDirectionInput()
    val stimuliChecked = u_checkStimuliInput()
    if (stimuliChecked == 1) {
        if (contextChecked + directionChecked >= 1) {
            if (stimuliType == stimuliList[2]) {
                TargetArea.visibility = View.VISIBLE
                TargetAreaFrame.visibility = View.VISIBLE
            } else {
                TargetArea.visibility = View.GONE
                TargetAreaFrame.visibility = View.GONE
            }
        } else {
            TargetArea.visibility = View.GONE
            TargetAreaFrame.visibility = View.GONE
        }
    } else {
        TargetArea.visibility = View.GONE
        TargetAreaFrame.visibility = View.GONE
    }
}

// 此function 應在 confirm selection中呼叫
fun u_randomThePosition() {
    //這邊會決定出來的數值範圍寬度
    val tempWidth = ((-targetBoxSize / 2)..(targetBoxSize / 2) step targetStep).shuffled()
    val tempHeight = ((-targetBoxSize / 2)..(targetBoxSize / 2) step targetStep).shuffled()
    // 這邊暫存
    randWidth = tempWidth.subList(0, 5).toIntArray()
    randHeight = tempHeight.subList(0, 5).toIntArray()
}   //需要調整數值範圍


/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//spinner 顯示與儲存輸入
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
            //TO-DO("Not yet implemented")
        }
    }
}

fun u_setTrialLimit(trialLimitInput: String) {

    when (testCondition) {
        testConditionList[0] -> { //practice
            when (trialLimitInput) {
                "請選次數" -> {
                    maxTrailDesire = 1
                    Toast.makeText(mContextKIN, "請輸入次數，預設為 1 次", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    maxTrailDesire = trialLimitInput.toInt()
                }
            }
        }
        testConditionList[1] -> {  //formal
            maxTrailDesire = MAX_FORMAL_TRIAL
        }
        testConditionList[2] -> {  //Addition
            when (trialLimitInput) {
                "請選次數" -> {
                    maxTrailDesire = 1
                    Toast.makeText(mContextKIN, "請輸入次數，預設為 1 次", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    maxTrailDesire = trialLimitInput.toInt()
                }
            }
        }
        testConditionList[3] -> {  //Non_dominant
            when (trialLimitInput) {
                "請選次數" -> {
                    maxTrailDesire = 1
                    Toast.makeText(mContextKIN, "請輸入次數，預設為 1 次", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    maxTrailDesire = trialLimitInput.toInt()
                }
            }
        }


        testConditionList[4] -> {  //AutoRecord
            maxTrailDesire = MAX_FORMAL_TRIAL
        }


    }

}  //可直接移植到補測

fun u_launchDirectionSpinner() {
    val adapter = ArrayAdapter.createFromResource(
        mContextKIN,
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
            u_setDirection(directionList[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            currentTestDirection = ""
        }
    }
    //以上:z; 方向選單CODE: arrayList已經移置string ,name: direction_list
}

fun u_setDirection(directionInput: String) {
    currentTestDirection = directionInput  //儲存目前選擇方向
    u_clearViews()
    u_setSquareOfTargetArea()
    // u_checkContextAndLaunchView(currentTestContext) //判斷狀況並launch特定view

    lateinit var targetView: ImageView
    lateinit var startView: ImageView
    lateinit var randomTarget: ImageView

    lateinit var upArrow: ImageView
    lateinit var upLeftArrow: ImageView
    lateinit var upRightArrow: ImageView

    lateinit var downArrow: ImageView
    lateinit var downLeftArrow: ImageView
    lateinit var downRightArrow: ImageView

    lateinit var targetParams: ViewGroup.MarginLayoutParams
    lateinit var startParams: ViewGroup.MarginLayoutParams


    // 根據當前情境，指派測驗參數
    when (currentTestContext) {
        "Pen" -> {
            targetView = penTarget
            startView = penStartPoint
            randomTarget = penRandomTargetView

            upArrow = penUpArrow
            upLeftArrow = penUpLeftArrow
            upRightArrow = penUpRightArrow

            downArrow = penDownArrow
            downLeftArrow = penDownLeftArrow
            downRightArrow = penDownRightArrow

            targetParams = penTargetParams
            startParams = penStartParams

        }
        "Finger" -> {
            targetView = fingerTarget
            startView = fingerStartPoint
            randomTarget = fingerRandomTargetView

            upArrow = fingerUpArrow
            upLeftArrow = fingerUpLeftArrow
            upRightArrow = fingerUpRightArrow

            downArrow = fingerDownArrow
            downLeftArrow = fingerDownLeftArrow
            downRightArrow = fingerDownRightArrow

            targetParams = fingerTargetParams
            startParams = fingerStartParams
        }
        else -> {
            targetView = fingerTarget
            startView = fingerStartPoint
            randomTarget = fingerRandomTargetView

            upArrow = fingerUpArrow
            upLeftArrow = fingerUpLeftArrow
            upRightArrow = fingerUpRightArrow

            downArrow = fingerDownArrow
            downLeftArrow = fingerDownLeftArrow
            downRightArrow = fingerDownRightArrow

            targetParams = fingerTargetParams
            startParams = fingerStartParams
        }  //沒有選擇時，預設為手指
    }

    // "L_Up", "L_Up_Right", "R_Up", "R_Up_Left",
    // "L_Down", "L_Down_Right", "R_Down", "R_Down_Left",)

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


        /* "L_Up" -> {
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
        }   //<item>右下至左上</item>
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
            titleParams.setMargins(
                centerCoordinateX - titleCalibrate,
                centerCoordinateY - 400,
                0,
                0
            )
        }

        /*  "L_Up" -> {
              targetParams.setMargins(
                  centerCoordinateX - calibrateWidth - Center2Target,
                  centerCoordinateY - calibrateWidth - Center2Target,
                  0,
                  0
              )
              startParams.setMargins(
                  centerCoordinateX - calibrateWidth - Center2Start,
                  centerCoordinateY - calibrateWidth + Center2Start,
                  0,
                  0
              )

              titleParams.setMargins(ccenterCoordinateX - titleCalibrate + 800, centerCoordinateY - 400, 0, 0)
          }
          "L_Up_Right" -> {
              targetParams.setMargins(
                  centerCoordinateX - calibrateWidth + Center2Target,
                  centerCoordinateY - calibrateWidth - Center2Target,
                  0,
                  0
              )
              startParams.setMargins(
                  centerCoordinateX - calibrateWidth - Center2Start,
                  centerCoordinateY - calibrateWidth + Center2Start,
                  0,
                  0
              )

              titleParams.setMargins(centerCoordinateX - titleCalibrate + 800, centerCoordinateY - 400, 0, 0)
          }
          "R_Up" -> {
              targetParams.setMargins(
                  centerCoordinateX - calibrateWidth + Center2Target,
                  centerCoordinateY - calibrateWidth - Center2Target,
                  0,
                  0
              )
              startParams.setMargins(
                  centerCoordinateX - calibrateWidth + Center2Start,
                  centerCoordinateY - calibrateWidth + Center2Start,
                  0,
                  0
              )

              titleParams.setMargins(centerCoordinateX - titleCalibrate - 800, centerCoordinateY - 400, 0, 0)
          }
          "R_Up_Left" -> {
              targetParams.setMargins(
                  centerCoordinateX - calibrateWidth - Center2Target,
                  centerCoordinateY - calibrateWidth - Center2Target,
                  0,
                  0
              )
              startParams.setMargins(
                  centerCoordinateX - calibrateWidth + Center2Target,
                  centerCoordinateY - calibrateWidth + Center2Target,
                  0,
                  0
              )

              titleParams.setMargins(centerCoordinateX - titleCalibrate - 800, centerCoordinateY - 400, 0, 0)
          }*/

        //以下為為受測者方向出發的選項，程式描述為icon對於施測者的方向///
        "R_Up" -> {
            targetParams.setMargins(
                centerCoordinateX - calibrateWidth - Center2Target,
                centerCoordinateY - calibrateWidth + Center2Target,
                0,
                0
            )
            startParams.setMargins(
                centerCoordinateX - calibrateWidth - Center2Start,
                centerCoordinateY - calibrateWidth - Center2Start,
                0,
                0
            )

            titleParams.setMargins(
                centerCoordinateX - titleCalibrate + 800,
                centerCoordinateY - 400,
                0,
                0
            )
        }
        "R_Up_Left" -> {
            targetParams.setMargins(
                centerCoordinateX - calibrateWidth + Center2Target,
                centerCoordinateY - calibrateWidth + Center2Target,
                0,
                0
            )
            startParams.setMargins(
                centerCoordinateX - calibrateWidth - Center2Target,
                centerCoordinateY - calibrateWidth - Center2Target,
                0,
                0
            )

            titleParams.setMargins(
                centerCoordinateX - titleCalibrate - 800,
                centerCoordinateY - 400,
                0,
                0
            )
        }
        "L_Up" -> {
            targetParams.setMargins(
                centerCoordinateX - calibrateWidth + Center2Target,
                centerCoordinateY - calibrateWidth + Center2Target,
                0,
                0
            )
            startParams.setMargins(
                centerCoordinateX - calibrateWidth + Center2Start,
                centerCoordinateY - calibrateWidth - Center2Start,
                0,
                0
            )

            titleParams.setMargins(
                centerCoordinateX - titleCalibrate - 800,
                centerCoordinateY - 400,
                0,
                0
            )
        }
        "L_Up_Right" -> {
            targetParams.setMargins(
                centerCoordinateX - calibrateWidth - Center2Target,
                centerCoordinateY - calibrateWidth + Center2Target,
                0,
                0
            )
            startParams.setMargins(
                centerCoordinateX - calibrateWidth + Center2Start,
                centerCoordinateY - calibrateWidth - Center2Start,
                0,
                0
            )

            titleParams.setMargins(
                centerCoordinateX - titleCalibrate + 800,
                centerCoordinateY - 400,
                0,
                0
            )
        }
    }

    //避免重新練習時BUG
    when (testCondition) {
        testConditionList[0] -> {
            targetView.visibility = View.VISIBLE
            startView.visibility = View.VISIBLE
            upArrow.visibility = View.GONE
            upLeftArrow.visibility = View.GONE
            upRightArrow.visibility = View.GONE
            //new
            downArrow.visibility = View.VISIBLE
            downLeftArrow.visibility = View.GONE
            downRightArrow.visibility = View.GONE

            targetParams.setMargins(
                centerCoordinateX - calibrateWidth,
                centerCoordinateY - calibrateWidth + Center2Target,
                0,
                0
            )
            startParams.setMargins(
                centerCoordinateX - calibrateWidth,
                centerCoordinateY - calibrateWidth - Center2Start,
                0,
                0
            )
            titleParams.setMargins(
                centerCoordinateX - titleCalibrate + 800,
                centerCoordinateY - 400,
                0,
                0
            )
        }
    }

    if (testCondition == testConditionList[0]) {
        //練習測中 不隱藏預設目標
    } else {
        //其他情境中，預覽畫面，只有AP2AP 隱藏預設目標
        when (stimuliType) {
            stimuliList[0] -> {
            }
            stimuliList[1] -> { //VAP2AP
                // 顯示隨機的目標，不顯示方格，不顯示灰目標 >> 在setTarget 寫判斷式
                //targetView.visibility = View.INVISIBLE //隱藏預設目標
            }
            stimuliList[2] -> { //AP2AP
                targetView.visibility = View.INVISIBLE //隱藏預設目標
                // 顯示方格，不顯示任何目標 >> 在setTarget 寫判斷式
            }
            stimuliList[3] -> { //PP2AP
                // 顯示隨機的目標，不顯示方格，不顯示灰目標 >> 在setTarget 寫判斷式
                //targetView.visibility = View.INVISIBLE //隱藏預設目標
            }
        }
    }

}

fun u_launchContextSpinner() {
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
            //TO-DO("Not yet implemented")
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
    u_clearViews()
    u_setSquareOfTargetArea()
    u_checkContextAndLaunchView(currentTestContext) //更換ImageView宣告內容
    u_setDirection(currentTestDirection)
}

fun u_launchStimuliTypeSpinner() {
    val adapter = ArrayAdapter.createFromResource(
        mContextKIN,
        R.array.stimuliType_list,
        android.R.layout.simple_spinner_dropdown_item
    )
    stimuliTypeSpinner.adapter = adapter
    stimuliTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            u_setStimuliType(stimuliList[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //TO-DO("Not yet implemented")
        }
    }
}

fun u_setStimuliType(stimuliInput: String) {
    stimuliType = stimuliInput
    u_clearViews()
    when (stimuliType) {
        stimuliList[0] -> {
            Toast.makeText(mContextKIN, "請選擇測驗刺激", Toast.LENGTH_SHORT).show()
        }
        stimuliList[1] -> { //VAP2AP
            // 顯示隨機的目標，不顯示方格，不顯示灰目標 >> 在setTarget 寫判斷式
        }
        stimuliList[2] -> { //AP2AP
            // 顯示方格，不顯示任何目標 >> 在setTarget 寫判斷式
        }
        stimuliList[3] -> { //PP2AP
            // 顯示隨機的目標，不顯示方格，不顯示灰目標 >> 在setTarget 寫判斷式
        }
    }
    u_setSquareOfTargetArea()
    u_checkContextAndLaunchView(currentTestContext) //更換ImageView宣告內容
    u_setDirection(currentTestDirection)
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////


// 進入各頁面前確認有存檔路徑 或 default
fun u_checkDemographicInputAndUpdateDefault(mActivityKIN: Activity, mContextKIN: Context) {
    //1.檢查viewModel中demographic資料
    //2.確認所有輸入都非空白
    //3.提供default數值 (TEST)
    //val mainViewModel =  MainViewModel()

    if (mainViewModel.outputFilePath.value.isNullOrEmpty()) {
        Toast.makeText(mContextKIN, "存檔路徑不存在，開始設定預設路徑", Toast.LENGTH_SHORT).show()

        mainViewModel.setName("virtualSubject")
        mainViewModel.setClientCode("00000000")
        mainViewModel.setBirthdate("MM/dd/yyyy")
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        mainViewModel.setTestDate(formatter.format(Calendar.getInstance().time).toString()) //當日日期
        mainViewModel.setHandedness("TEST") // 右 or 左
        mainViewModel.setGrade("TEST")// 1-6
        mainViewModel.setSex("TEST") // 男 or 女
        mainViewModel.setCity("TEST")
        u_showCurrentDemographicInputDialog(mActivityKIN, mContextKIN, mainViewModel)

    } else {
        filePathStr = mainViewModel.outputFilePath.value.toString()
        Toast.makeText(mContextKIN, "存檔路徑存在", Toast.LENGTH_SHORT).show()
    }

}

fun u_showCurrentDemographicInputDialog(
    mActivityKIN: Activity,
    mContextKIN: Context,
    mainViewModel: MainViewModel
) {
    MaterialAlertDialogBuilder(mActivityKIN)
        .setTitle(mContextKIN.resources.getString(R.string.demographic_dialog)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
        .setMessage(
            mContextKIN.resources.getString(R.string.your_name, mainViewModel.name.value)
                    + "\n" + mContextKIN.resources.getString(
                R.string.your_sex,
                mainViewModel.sex.value
            )
                    + "\n" + mContextKIN.resources.getString(
                R.string.your_birthdate,
                mainViewModel.birthdate.value
            )
                    + "\n" + mContextKIN.resources.getString(
                R.string.your_testDate,
                mainViewModel.testDate.value
            )
                    + "\n" + mContextKIN.resources.getString(
                R.string.your_handedness,
                mainViewModel.handedness.value
            )
                    + "\n" + mContextKIN.resources.getString(
                R.string.your_grade,
                mainViewModel.grade.value
            )
                    + "\n" + mContextKIN.resources.getString(
                R.string.your_city,
                mainViewModel.city.value
            )
                    + "\n" + mContextKIN.resources.getString(
                R.string.your_code,
                mainViewModel.clientCode.value
            )
        ) //Set the message to show the data

        .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,
        .setNegativeButton(mContextKIN.resources.getString(R.string.modify_input)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
            mainViewModel.resetDemographicInput()
        }
        .setPositiveButton(mContextKIN.resources.getString(R.string.confirm_input)) { _, _ ->
            u_saveDemographic()
        }
        .show() //creates and then displays the alert dialog.
}

//設定預設的test存檔路徑，避免CRASH
fun u_saveDemographic() {
    // 整理人口學資料段落
    val outputName =
        mContextKIN.resources.getString(R.string.your_name, mainViewModel.name.value)
    val outputSex =
        mContextKIN.resources.getString(R.string.your_sex, mainViewModel.sex.value)
    val outputBirthdate = mContextKIN.resources.getString(
        R.string.your_birthdate,
        mainViewModel.birthdate.value
    )
    val outputTestDate =
        mContextKIN.resources.getString(R.string.your_testDate, mainViewModel.testDate.value)
    val outputHand = mContextKIN.resources.getString(
        R.string.your_handedness,
        mainViewModel.handedness.value
    )
    val outputGrade =
        mContextKIN.resources.getString(R.string.your_grade, mainViewModel.grade.value)
    val outputCity =
        mContextKIN.resources.getString(R.string.your_city, mainViewModel.city.value)
    val outputCode =
        mContextKIN.resources.getString(R.string.your_code, mainViewModel.clientCode.value)

    val emp = "\n"  //換行字串 "\n"
    val txtFile: File  //創建檔案
    val filePathConstructCode = mainViewModel.clientCode.value.toString()

    val timeStamp: String //避免資料夾個案編號資料重複的額外後接編號
    val timeStampFormatter =
        SimpleDateFormat("HH_mm_ss", Locale.getDefault()) //H 時 在一天中 (0~23) // m 分 // s 秒
    val timeStampCalendar = Calendar.getInstance()
    timeStamp =
        timeStampFormatter.format(timeStampCalendar.time).toString() //當日時間  //需傳到viewModel

    /////建立檔案資料夾段落

    //資料夾名稱/路徑
    val filePath = File(mActivityKIN.getExternalFilesDir("").toString(), filePathConstructCode)
    val backupFilePath = File(
        mActivityKIN.getExternalFilesDir("").toString(),
        filePathConstructCode + "_" + timeStamp
    )

    //檢查目前個案標號是否使用過，建立資料夾並建立txt檔
    if (filePath.exists()) {
        Toast.makeText(mContextKIN, "這個編號已經使用過了，將自動產生新資料夾", Toast.LENGTH_SHORT).show()
        backupFilePath.mkdir()
        txtFile =
            File(backupFilePath, filePathConstructCode + "_demographic_" + timeStamp + ".txt")
        filePathStr = backupFilePath.path  //更新全域變數，用於後續存檔(此段產生的資料夾名稱不同)
    } else {
        filePath.mkdir()
        txtFile = File(filePath, filePathConstructCode + "_demographic_" + ".txt")
        filePathStr = filePath.path      //更新全域變數，用於後續存檔
    }
    //更新檔案路徑
    mainViewModel.setFilePath(filePathStr)

    //儲存基本資料檔案
    val out = FileOutputStream(txtFile, true)
    out.write(outputName.toByteArray())
    out.write(emp.toByteArray())
    out.write(outputSex.toByteArray())
    out.write(emp.toByteArray())
    out.write(outputBirthdate.toByteArray())
    out.write(emp.toByteArray())
    out.write(outputTestDate.toByteArray())
    out.write(emp.toByteArray())
    out.write(outputHand.toByteArray())
    out.write(emp.toByteArray())
    out.write(outputGrade.toByteArray())
    out.write(emp.toByteArray())
    out.write(outputCity.toByteArray())
    out.write(emp.toByteArray())
    out.write(outputCode.toByteArray())
    out.flush()
    out.close()
    Toast.makeText(mContextKIN, "Demographic Data Save Success", Toast.LENGTH_SHORT).show()

}  // 存檔function END

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
// navigation
fun u_goBackToMenu() {
    Toast.makeText(mContextKIN, "回到測驗選單", Toast.LENGTH_SHORT).show()
    //TODO: 根據各情境，調整判斷式，才能正確回到頁面

    when (testCondition) {
        testConditionList[0] -> {
            navControllerKIN.navigate(com.example.kinesthesia_first_attempt.R.id.action_practiceFragment_to_testMenuFragment)
        }

        testConditionList[1] -> {
            navControllerKIN.navigate(com.example.kinesthesia_first_attempt.R.id.action_testFragment_to_testMenuFragment)
        }

        testConditionList[2] -> {
            navControllerKIN.navigate(com.example.kinesthesia_first_attempt.R.id.action_additionFragment_to_testMenuFragment)
        }

        testConditionList[3] -> {
            navControllerKIN.navigate(com.example.kinesthesia_first_attempt.R.id.action_nondominantFragment_to_testMenuFragment)
        }

        testConditionList[4] -> {
            navControllerKIN.navigate(com.example.kinesthesia_first_attempt.R.id.action_autorecordFragment_to_testMenuFragment)
        }

    }

}


class MyGestureDetectorListener : GestureDetector.OnGestureListener {
    //https://www.itread01.com/content/1549194854.html
    //https://developer.android.com/training/gestures/detector
    //https://developer.android.com/jetpack/compose/gestures
    private val SWIPE_THRESHOLD: Int = 300
    private val SWIPE_VELOCITY_THRESHOLD = 300

    var scrollThresholdValue_x = 0f
    var scrollThresholdValue_y = 0f
    var onDownTime: Long = 0
    var onLongPressTime: Long = 0
    var onScrollTime: Long = 0
    var interval: Long = 0

    var isLongPressed: Boolean = false

    fun resetTime() {
        onDownTime = 0
        onLongPressTime = 0
        interval = 0
        scrollThresholdValue_x = 0f
        scrollThresholdValue_y = 0f
        onScrollTime = 0
    }

    override fun onDown(e: MotionEvent?): Boolean {
        onDownTime = SystemClock.currentThreadTimeMillis()
        startX = e!!.x
        startY = e!!.y
        Log.d("Gesture", "onDown")
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
        startX = e!!.x
        startY = e!!.y
        Log.d("Gesture", "onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        startX = 0f
        startY = 0f
        Log.d("Gesture", "onSingleTapUp")
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        // Log.d("Gesture", "onScroll")

        onScrollTime = SystemClock.currentThreadTimeMillis()
        interval = onScrollTime - onDownTime
        //Log.d("Gesture", "onScroll: interval = $interval ms")
        var scrollThresholdValue_x = e2!!.x - e1!!.x
        var scrollThresholdValue_y = e2!!.y - e1!!.y
        //Log.d("Gesture", "onScroll: Scroll Threshold Value X=$scrollThresholdValue_x Y=$scrollThresholdValue_y")
        Log.d("Gesture", "onScroll: Scroll parameters X=$distanceX Y=$distanceY")

        resetTime()
        //return false
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        onLongPressTime = SystemClock.currentThreadTimeMillis()
        interval = onLongPressTime - onDownTime
        Log.d("Gesture", "onLongPress: interval = $interval ms")
        resetTime()

        //測試結果: GestureDetector 本身 onLongPress 的時間設定為 10 - 40ms之間，並不穩定，且時長短
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        var result: Boolean = false

        try {
            var diffY = e2!!.y - e1!!.y
            var diffX = e2!!.x - e1!!.x

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }

            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }

        } catch (exception: Exception) {
            exception.printStackTrace();
        }

        Log.d("Gesture", " onFling Confirm")
        return result
        //return true
    }

    fun onSwipeRight() {
        Log.d("Gesture", " onFling: Right")
    }

    fun onSwipeLeft() {
        Log.d("Gesture", " onFling: Left")
    }

    fun onSwipeTop() {
        Log.d("Gesture", " onFling: Top")
    }

    fun onSwipeBottom() {
        Log.d("Gesture", " onFling: Bottom")
    }

}


class MyOnDoubleTapListener : GestureDetector.OnDoubleTapListener {

    override fun onDoubleTap(event: MotionEvent?): Boolean {
        Log.d("Gesture", "onDoubleTap: $event")
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent?): Boolean {
        Log.d("Gesture", "onDoubleTapEvent: $event")
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent?): Boolean {
        Log.d("Gesture", "onSingleTapConfirmed: $event")
        return true
    }

}
