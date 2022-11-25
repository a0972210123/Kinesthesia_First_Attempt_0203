package com.example.kinesthesia_first_attempt.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*



class MainViewModel : ViewModel() {
    init {
        Log.d("MainViewModel", "MainViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MainViewModel", "MainViewModel destroyed!")
    }

    // 放入所有要記錄的變數: 如人口學資料

    var _outputFilePath = MutableLiveData<String>("")
    val outputFilePath: LiveData<String> = _outputFilePath

    fun setFilePath(PathInput: String) {
        _outputFilePath.value = PathInput
    }


    var _totalPracticeTime = MutableLiveData<Int>(0)
    val totalPracticeTime: LiveData<Int> = _totalPracticeTime

    fun setPracticeTime(Input:Int) {
        _totalPracticeTime.value = Input
    }

    private var _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name

    private var _sex = MutableLiveData<String>("")
    val sex: LiveData<String> = _sex

    private var _handedness = MutableLiveData<String>("")
    val handedness: LiveData<String> = _handedness

    private var _grade = MutableLiveData<String>("")
    val grade: LiveData<String> = _grade

    private var _birthdate = MutableLiveData<String>("")
    val birthdate: LiveData<String> = _birthdate

    private var _testDate = MutableLiveData<String>("")
    val testDate: LiveData<String> = _testDate

    private var _city = MutableLiveData<String>("")
    val city: LiveData<String> = _city

    private var _clientCode = MutableLiveData<String>("")
    val clientCode: LiveData<String> = _clientCode


    //測驗表現資料
    //absoluteError relativeError variableError  TrialsCounts  testList
    // x.y座標等等


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