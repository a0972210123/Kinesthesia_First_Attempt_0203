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
import com.example.kinesthesia_first_attempt.databinding.FragmentCoverBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel


class CoverFragment : Fragment() {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel
    //private var binding: FragmentCoverBinding? = null
    private lateinit var binding: FragmentCoverBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val fragmentBinding = FragmentCoverBinding.inflate(inflater, container, false)
        //binding = fragmentBinding

        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_cover, container, false)

        return binding.root

        //return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // Set up the button click listeners
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            //goToDemographicPage.setOnClickListener { goToDemographic() }
            coverFragment = this@CoverFragment  //使用listenser binding，用UI button 在xml中設定onclick
        }
    }




    fun goToDemographic() {
        Toast.makeText(activity, "填寫基本資料", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id.action_coverFragment_to_demographicFragment)
    }


    //companion object {
    //    fun newInstance() = CoverFragment()
   // }


    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }

}