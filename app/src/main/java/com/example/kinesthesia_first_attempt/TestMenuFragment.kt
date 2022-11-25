package com.example.kinesthesia_first_attempt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentTestMenuBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [TestMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestMenuFragment : Fragment() {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel
    //private var binding: FragmentTestMenuBinding? = null
    private lateinit var binding: FragmentTestMenuBinding


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = requireActivity().window.decorView
        u_showSystemUI(decorView)
        Log.d("testMainViewModel", "TestMenuFragment onCreate!")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("testMainViewModel", "TestMenuFragment onCreateView!")
        //val fragmentBinding = FragmentTestMenuBinding.inflate(inflater, container, false)
        //binding = fragmentBinding
        //return fragmentBinding.root
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_test_menu, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            //sendButton.setOnClickListener { sendOrder() }
            testMenuFragment = this@TestMenuFragment //使用listenser binding，用UI button 在xml中設定onclick
        }

        Log.d("testMainViewModel", "TestMenuFragment created!")
    }



    companion object {

    }


    fun goToPractice() {
        Toast.makeText(activity, "進入測驗練習", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id.action_testMenuFragment_to_practiceFragment)
    }




    fun goToFormal() {
        Toast.makeText(activity, "進入正式測驗", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id. action_testMenuFragment_to_testFragment)
    }


    fun goToAddition() {
        Toast.makeText(activity, "進入補充測驗", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id.action_testMenuFragment_to_additionFragment)
    }

    fun goToNondominant() {
        Toast.makeText(activity, "進入非慣用手測驗", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id.action_testMenuFragment_to_nondominantFragment)
    }


    fun goToAuto() {
        Toast.makeText(activity, "進入自動記錄", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id. action_testMenuFragment_to_autorecordFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }

}