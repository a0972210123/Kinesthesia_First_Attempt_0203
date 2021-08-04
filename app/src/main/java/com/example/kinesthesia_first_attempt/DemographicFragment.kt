package com.example.kinesthesia_first_attempt

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentDemographicBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DemographicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


// 變數宣告
var gcode: String = ""
var gHand: String = ""
var filePathStr:String = ""




class DemographicFragment : Fragment() {


    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel
    private var binding: FragmentDemographicBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentDemographicBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            //sendButton.setOnClickListener { sendOrder() }
            demographicFragment = this@DemographicFragment //使用listenser binding，用UI button 在xml中設定onclick
        }
    }














    companion object {

    }


    fun goToIntroduction() {
        Toast.makeText(activity, "開始測驗說明", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id.action_demographicFragment_to_introFragment)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }




// 0804測試
    private fun updateBirthday() {
     binding?.birthDate?.inputType = InputType.TYPE_NULL
        //binding.textInputEditText.text.toString()

    binding?.birthDate?.setOnClickListener {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // 問題，context? function沒import?
        DatePickerDialog(this, { _, year, month, day ->
            run {
                val month2 = month+1
                var month3:String = month2.toString()
                if (month2<10){
                    month3 = "0$month2"
                }
                val subjDate = "$month3/$day/$year"
                binding?.birthDate?.setText(subjDate)
            }
        }, year, month, day).show()
        Log.d("DemographicFragment","$year/$month/$day")
    }
// 0804測試




    }












    }