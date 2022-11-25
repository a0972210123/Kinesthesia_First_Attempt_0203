package com.example.kinesthesia_first_attempt

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.kinesthesia_first_attempt.databinding.FragmentIntroBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [IntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//testing
@SuppressLint("StaticFieldLeak") var mContext: Context? = null
@SuppressLint("StaticFieldLeak") var mButton: Button? = null
@SuppressLint("StaticFieldLeak") lateinit var mInstruction: TextView
var TestingInstructionText : String = ""
lateinit var mViewPager: ViewPager2  //test1

//testing

class IntroFragment : Fragment() {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: MainViewModel by viewModels()
    //private var binding: FragmentIntroBinding? = null
    private lateinit var binding: FragmentIntroBinding

    //val instructionList = listOf("指導語1","指導語2","指導語3","指導語4","指導語5")
    //var indexForList by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            introFragment = this@IntroFragment //使用listenser binding，用UI button 在xml中設定onclick
        }

        // view一定要在這邊call，不然會crash
        mButton = requireView().findViewById<View>(R.id.instruction_next) as Button  //要移到onviewcreated
        mViewPager = requireView()!!.findViewById<View>(R.id.view_pager) as ViewPager2

        //以下測試pageradapter
        mContext = getActivity()?.getApplicationContext()

        //方法1
        val pagerAdapter = PagerAdapter()
        val data = listOf<Int>(1,2,3,4,5)
        pagerAdapter.setList(data)

        mViewPager.apply {
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position==0){
                        mButton!!.visibility = View.INVISIBLE
                    } else if (position==1){
                        mButton!!.visibility = View.INVISIBLE
                    } else if (position==2){
                        mButton!!.visibility = View.INVISIBLE
                    } else if (position==3){
                        mButton!!.visibility = View.VISIBLE
                    } else if (position==4){
                        mButton!!.visibility = View.VISIBLE
                    }
                }
            })
        }

/*
        //方法2
        //val viewPager2 = requireView()!!.findViewById<ViewPager2>(R.id.view_pager)
        val myAdapter = MyAdapter()
        val data = listOf<Int>(1,2,3,4)
        myAdapter.setList(data)
        mviewPager2.adapter = myAdapter
*/

    } //onViewCreated結束

    companion object {}

    fun goToMenu() {
        Toast.makeText(activity, "進入測驗選單", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id.action_introFragment_to_testMenuFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }





}



//Testing1   將context、pageradapter、判斷釋放這?

//val pagerAdapter = PagerAdapter()
//mContext = getContext()?.getApplicationContext() //有修改
/*
//ref: https://codertw.com/%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80/456183/
        1.getActivity()；//獲取包含該fragment的活動（activity）上下文
        2.getContext()；//獲取該fragment上下文
        3.getActivity().getApplicationContext()；//通過包含該fragment的活動（activity）獲取整個應用的上下文
        4.getContext().getApplicationContext()；//通過該fragment獲取整個應用的上下文

 */


/*
mViewPager.apply {
    adapter = pagerAdapter
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (position==0){
                mButton!!.visibility = View.INVISIBLE
                mInstruction!!.text = TestingInstructionText
            } else if (position==1){
                mButton!!.visibility = View.INVISIBLE
                mInstruction!!.text = TestingInstructionText
            } else if (position==2){
                mButton!!.visibility = View.INVISIBLE
                mInstruction!!.text = TestingInstructionText
            } else if (position==3){
                mButton!!.visibility = View.VISIBLE
                mInstruction!!.text = TestingInstructionText
            } else if (position==4){
                mButton!!.visibility = View.VISIBLE
                mInstruction!!.text = TestingInstructionText
            }
        }
    })
}
//Testing
*/



//Testing2

/*
val viewPager2 = requireView()!!.findViewById<ViewPager2>(R.id.view_pager)
val myAdapter = MyAdapter()
val data = listOf<Int>(1,2,3,4)
myAdapter.setList(data)
viewPager2.adapter = myAdapter


 */