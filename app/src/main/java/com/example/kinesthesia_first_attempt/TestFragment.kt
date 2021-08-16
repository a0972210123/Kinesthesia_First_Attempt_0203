package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kinesthesia_first_attempt.databinding.FragmentTestBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import java.io.File
import java.io.FileOutputStream








class TestFragment : Fragment() {


    private val sharedViewModel: MainViewModel by activityViewModels()
   // private val viewModel: MainViewModel by viewModels()
    //private var binding: FragmentIntroBinding? = null
    private lateinit var binding: FragmentTestBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_test, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            testFragment = this@TestFragment //使用listenser binding，用UI button 在xml中設定onclick
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    val kData = StringBuffer()


    //按鈕後執行
    fun printData(): List<Float>{
        Log.d("X/Y/面積/長軸/短軸：inFragment", "$startX  $startY  $bb  $b1  $b2")
        changeText()
        return listOf(startX,startY,bb,b1,b2)  //outPutCoordinate()
    }

    fun changeText(){
        val text1 = requireView()!!.findViewById<TextView>(R.id.text1)
        val text3 = requireView()!!.findViewById<TextView>(R.id.text3)

        if (text1.text.toString()=="第一次：-999") {
            text1.text = "第一次：X= $startX ; Y= $startY"
        }
        text3.text = "最後一次：X= $startX ; Y= $startY"

    }



    private fun sendkData() {
        val currentTimestamp = System.currentTimeMillis()
        val fileW = File(filePathStr, "$currentTimestamp.csv")
        val os = FileOutputStream(fileW, true)
        val tmp = StringBuffer()

        os.write(kData.toString().toByteArray())
        os.flush()
        os.close()
        tmp.setLength(0) //clean buffer
    }




}