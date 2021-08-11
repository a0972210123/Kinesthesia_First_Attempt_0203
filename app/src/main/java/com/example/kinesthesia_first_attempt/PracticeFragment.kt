package com.example.kinesthesia_first_attempt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentPracticeBinding
import com.example.kinesthesia_first_attempt.ui.main.MAX_PRACTICE_TRIAL
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PracticeFragment : Fragment() {

    private val sharedViewModel: MainViewModel by activityViewModels()
    //private lateinit var viewModel: MainViewModel
    //private var binding: FragmentPracticeBinding? = null
    private lateinit var binding: FragmentPracticeBinding


    //測驗相關變數宣告
    var A = MAX_PRACTICE_TRIAL

    var ButtonPressedCountsInATrial: Int = 0

    var practiceTrialsCount :Int = 0

    var startPositionX: Int = 0
    var startPositionY: Int = 0
    var TestPositionX: Int = 0
    var TestPositionY: Int = 0
    var RestPositionX: Int = 0
    var RestPositionY: Int = 0
    var ResponsePositionX: Int = 0
    var ResponsePositionY: Int = 0


    fun clearCurrentTrialRecord(){
        startPositionX = 0
        startPositionY = 0
        TestPositionX = 0
        TestPositionY = 0
        RestPositionX = 0
        RestPositionY = 0
        ResponsePositionX = 0
        ResponsePositionY = 0
        ButtonPressedCountsInATrial = 0

    }

    fun addTrialsCount(){
        practiceTrialsCount ++
    }

    // 記錄當下手指位置，並管理測驗流程
    fun recordPosition(){
        //單次測驗紀錄區段
        ButtonPressedCountsInATrial++ //每按一次按鈕+1  (要debounce?)
        when (ButtonPressedCountsInATrial) {
            1 -> {
                startPositionX = 10
                startPositionY = 10
            }
            2 -> {
                TestPositionX = 10
                TestPositionY = 10
            }
            3 -> {
                RestPositionX = 10
                RestPositionY = 10
            }
            4 -> {
                ResponsePositionX = 10
                ResponsePositionY = 10
            }

            5 -> {
                //saveCurrentTrialRecord() // 將單次反應存入?LIST? SET?
                clearCurrentTrialRecord()  //清除單次表現
                addTrialsCount()     // 完成一次測驗練習
                checkPracticeLimit() //檢查是否達到練習次數
            }


        }
    }


    fun saveCurrentTrialRecord(){ // 將單次反應存入?LIST? SET?

    }

    fun savePracticePerformance(){
        // 存檔前置參數宣告
        // 判斷是否練習過
        // 存檔
    }


    // 清除所有參數!，還沒寫完!
    fun clearRecord(){
        practiceTrialsCount = 0
    }


    fun checkPracticeLimit(){
        if (practiceTrialsCount > MAX_PRACTICE_TRIAL){
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.practice_dialog_title)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
                .setMessage(
                    getString(R.string.practice_dialog_message)
                ) //Set the message to show the data
                .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

                .setNegativeButton(getString(R.string.practice_dialog_try_again)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                    //儲存測驗表現  savePracticePerformance()
                    clearRecord()  //清除測驗表現
                    Toast.makeText(activity, "再試一次", Toast.LENGTH_SHORT)
                }
                .setPositiveButton(getString(R.string.practice_dialog_back_to_menu)) { _, _ ->
                    // 儲存測驗表現 savePracticePerformance()
                    clearRecord()  //清除測驗表現
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
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_practice, container, false)
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
    }



    companion object {
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }
}