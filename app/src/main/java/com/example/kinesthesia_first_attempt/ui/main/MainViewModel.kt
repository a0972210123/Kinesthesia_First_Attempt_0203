package com.example.kinesthesia_first_attempt.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


// 常數定義區段 : 此段落放不會改變的參數，例如總測驗次數/練習次數等等


//

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    // 放入所有要記錄的變數: 如人口學資料


    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name

    private val _sex = MutableLiveData<String>("")
    val sex: LiveData<String> = _sex

    private val _handedness = MutableLiveData<String>("")
    val handedness: LiveData<String> = _handedness

    private val _grade = MutableLiveData<Int>(0)
    val grade: LiveData<Int> = _grade

    private val _date = MutableLiveData<String>("")
    val date: LiveData<String> = _date


    //private val _instructionList = MutableLiveData<String> ("")
    //val instructionList: LiveData<String> = _instructionList

    //測驗表現資料

    val instructionList = listOf("指導語1","指導語2","指導語3","指導語4")
    // instructionList[0] = 第一個

    val instructionList2 = listOf ("歡迎使用本系統",
    "此評量需約30分鐘，請確認你處於舒適的坐姿且不被干擾",
     "評量時，需使用我們提供的筆將指定的文字抄寫至空格中",
     "準備好了，請按開始")






    // function段落，與按鈕、app logic、變數更動相關的method/funtion


    //基本資料頁用function

  open fun selectionPage(){



  }


















}