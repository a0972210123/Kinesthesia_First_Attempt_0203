package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kinesthesia_first_attempt.databinding.FragmentTestBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import java.io.File
import java.io.FileOutputStream


class TestFragment : Fragment() {


    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: MainViewModel by viewModels()
    //private var binding: FragmentIntroBinding? = null
    private lateinit var binding: FragmentTestBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        tBoard = requireView()!!.findViewById<TouchBoard>(R.id.view)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }






    //移植手指點點

    //變數宣告
    private var startX: Float = 0f
    private var startY: Float = 0f
    private val kData = StringBuffer()

    private lateinit var tBoard:TouchBoard

    // 缺少: 1.在lifecycle啟動 detect touch



    @SuppressLint("SetTextI18n")
    fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("Touch", "touch occur")

        val currentTimestamp = System.currentTimeMillis()
        val bb = event.getAxisValue(MotionEvent.AXIS_SIZE)
        val b1 = event.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR)
        val b2 = event.getAxisValue(MotionEvent.AXIS_TOUCH_MINOR)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y

                //val text1 = requireView()!!.findViewById<TextView>(R.id.text1)
                //val text3 = requireView()!!.findViewById<TextView>(R.id.text3)
                if (binding.text1.text.toString()=="第一次：-999") {
                    binding.text1.text = "第一次：X = $startX ; Y = $startY; 面積 = $bb; 長軸 = $b1; 短軸 = $b2"
                }
                binding.text3.text = "最後一次：X = $startX ; Y = $startY; 面積 = $bb; 長軸 = $b1; 短軸 = $b2"

                kData.append(currentTimestamp)
                kData.append(",")
                kData.append(startX)
                kData.append(",")
                kData.append(startY)
                kData.append(",")
                kData.append(bb)
                kData.append(",")
                kData.append(b1)
                kData.append(",")
                kData.append(b2)
                kData.append("\r\n")

                Log.d("X/Y/面積/長軸/短軸：", "$startX  $startY  $bb  $b1  $b2")
            }
        }
        return true
    }


    private fun detectTouch(){
        tBoard = requireView()!!.findViewById(R.id.view)
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

    //移植手指點點







}