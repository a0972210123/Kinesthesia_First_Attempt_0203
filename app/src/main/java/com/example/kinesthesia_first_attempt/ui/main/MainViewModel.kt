package com.example.kinesthesia_first_attempt.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*


// 常數定義區段 : 此段落放不會改變的參數，例如總測驗次數/練習次數等等，方格大小、等等

private const val TARGET_BOX_SIZE = 2.00 //待修改
private const val RESPONSE_DOT_SIZE = 3.00

const val MAX_PRACTICE_TRIAL = 8
const val MAX_FORMAL_TRIAL = 5




//

class MainViewModel : ViewModel() {

    // 放入所有要記錄的變數: 如人口學資料

    private var _outputFilePath = MutableLiveData<String>("")
    val outputFilePath: LiveData<String> = _outputFilePath

    fun setFilePath(PathInput: String) {
        _outputFilePath.value = PathInput
    }

    private var _totalPracticeTime = MutableLiveData<Int>(0)
    val totalPracticeTime: LiveData<Int> = _totalPracticeTime

    fun setPracticeTime(Input:Int) {
        _totalPracticeTime.value = Input
    }




    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name

    private val _sex = MutableLiveData<String>("")
    val sex: LiveData<String> = _sex

    private val _handedness = MutableLiveData<String>("")
    val handedness: LiveData<String> = _handedness

    private val _grade = MutableLiveData<String>("")
    val grade: LiveData<String> = _grade

    private val _birthdate = MutableLiveData<String>("")
    val birthdate: LiveData<String> = _birthdate

    private val _testDate = MutableLiveData<String>("")
    val testDate: LiveData<String> = _testDate

    private val _city = MutableLiveData<String>("")
    val city: LiveData<String> = _city

    private val _clientCode = MutableLiveData<String>("")
    val clientCode: LiveData<String> = _clientCode


    //測驗表現資料
    //absoluteError relativeError variableError  TrialsCounts  testList
    // x.y座標等等

    ///from unscramble
    private var wordsList: MutableList<String> = mutableListOf()   // 轉換成測驗的layout編號list
    private lateinit var currentWord: String  // 轉換成測驗的 總測驗次數var   單測驗layout執行次數 >> key: value
    ///

    private fun updateTrialsCounts() {
    }

    private val _currentTestID = MutableLiveData<String>("")
    val currentTestID: LiveData<String> = _currentTestID

    var testList = listOf<String>("A", "B", "C", "L2R", "R2L")


    // function段落，與按鈕、app logic、變數更動相關的method/function
    fun setName(UserNameInput: String) {
        _name.value = UserNameInput
        //updatePrice() // 確認數量後，也更新價格
    }

    fun setSex(Gender: String) {
        _sex.value = Gender
    }

    fun setHandedness(UserHandedness: String) {
        _handedness.value = UserHandedness
    }

    fun setGrade(UserGrade: String) {
        _grade.value = UserGrade
    }

    fun setBirthdate(UserBirthdate: String) {
        _birthdate.value = UserBirthdate
    }

    fun setTestDate(UserTestDate: String) {
        _testDate.value = UserTestDate
    }

    fun setCity(UserCity: String) {
        _city.value = UserCity
    }

    fun setClientCode(UserCode: String) {
        _clientCode.value = UserCode
    }


    // 起始時將參數歸零
    init {
        resetDemographicInput()
    }

    fun resetDemographicInput() {
        _birthdate.value = ""
        _testDate.value = ""
        _city.value = ""
        _grade.value = ""
        _handedness.value = ""
        _name.value = ""
        _sex.value = ""
        _clientCode.value = ""
    }


    // 以下用於檢查是否有輸入
    fun hasNoNameSet(): Boolean {
        return _name.value.isNullOrEmpty()
    }

    fun hasNoSexSet(): Boolean {
        return _sex.value.isNullOrEmpty()
    }

    fun hasNoBirthdateSet(): Boolean {
        return _birthdate.value.isNullOrEmpty()
    }

    fun hasNoTestDateSet(): Boolean {
        return _testDate.value.isNullOrEmpty()
    }


    fun hasNoHandSet(): Boolean {
        return _handedness.value.isNullOrEmpty()
    }

    fun hasNoGradeSet(): Boolean {
        return _grade.value.isNullOrEmpty()
    }

    fun hasNoCitySet(): Boolean {
        return _city.value.isNullOrEmpty()
    }

    fun hasNoCodeSet(): Boolean {
        return _clientCode.value.isNullOrEmpty()
    }
    // 以上用於檢查是否有輸入


    //基本資料頁用function
    // 測驗日期選擇function (參考cupcake)

    //以下Fun 用於處理使用者選擇日期 來自cupcake

    val dateOptions = getPickupOptions()
    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()

        repeat(4) {
            options.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return options
    }



}