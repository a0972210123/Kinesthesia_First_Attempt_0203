package com.example.kinesthesia_first_attempt

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentDemographicBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import java.io.File
import java.io.FileOutputStream
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
            demographicFragment =
                this@DemographicFragment //使用listenser binding，用UI button 在xml中設定onclick
        }


        //0804測試
        //syntax    val imageView = requireView()!!.findViewById<View>(R.id.foo) as ImageView
        //val editTextView = requireView()!!.findViewById<View>(R.id.birthDate) as EditText

        val subjName = requireView()!!.findViewById<EditText>(R.id.subjName)
        val subjCode = requireView()!!.findViewById<EditText>(R.id.subjCode)
        val subjBirth = requireView()!!.findViewById<EditText>(R.id.birthDate)
        val rg = requireView()!!.findViewById<RadioGroup>(R.id.GenderGroup)
        val subjGrade = requireView()!!.findViewById<RadioGroup>(R.id.GradeGroup)
        val subjHand = requireView()!!.findViewById<RadioGroup>(R.id.HandGroup)
        val gender: String
        val grade: String
        val hand: String
        //0804測試


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
    private fun selectionPage() {

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
                    val month2 = month + 1
                    var month3: String = month2.toString()
                    if (month2 < 10) {
                        month3 = "0$month2"
                    }
                    val subjDate = "$month3/$day/$year"
                    binding?.birthDate?.setText(subjDate)
                }
            }, year, month, day).show()
            Log.d("DemographicFragment", "$year/$month/$day")
        }


        val cityList = arrayListOf(
            "高雄市", "台南市", "台北市", "新北市", "桃園市", "台中市", "基隆市", "新竹市",
            "嘉義市", "新竹縣", "苗栗縣", "彰化縣", "南投縣", "雲林縣", "嘉義縣", "屏東縣", "宜蘭縣", "花蓮縣", "臺東縣", "澎湖縣", "其他"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cityList)
        // 問題，context? function沒import?

        binding?.city?.adapter = adapter

        binding?.city?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val selectCity: String = "城市：" + cityList[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        // 這個目前還沒解
        Demo_next.setOnClickListener {
            // 開啟檔案串流
            if (Build.VERSION.SDK_INT >= 23) {
                val requestCodePermissionStorage = 100
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                for (str in permissions) {
                    if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(permissions, requestCodePermissionStorage)
                        return@setOnClickListener
                    }
                }
            }

            //問題: 目前這邊都紅字，改成這種syntax，並放到onViewCreated就可以work
            //val editTextView = requireView()!!.findViewById<View>(R.id.birthDate) as EditText

            val subjName = findViewById<EditText>(R.id.subjName)
            val subjCode = findViewById<EditText>(R.id.subjCode)
            val subjBirth = findViewById<EditText>(R.id.birthDate)
            val rg = findViewById<RadioGroup>(R.id.GenderGroup)
            val subjGrade = findViewById<RadioGroup>(R.id.GradeGroup)
            val subjHand = findViewById<RadioGroup>(R.id.HandGroup)
            val gender:String
            val grade:String
            val hand:String


            //依選取項目顯示不同訊息
            gender = when (rg.checkedRadioButtonId) {
                R.id.male -> "性別:男"
                R.id.female -> "性別:女"
                else -> "性別:"
            }

            grade = when (subjGrade.checkedRadioButtonId) {
                R.id.Grade1 -> "年級:1"
                R.id.Grade2 -> "年級:2"
                R.id.Grade3 -> "年級:3"
                R.id.Grade4 -> "年級:4"
                R.id.Grade5 -> "年級:5"
                R.id.Grade6 -> "年級:6"
                else -> "年級:"
            }

            hand = when (subjHand.checkedRadioButtonId) {
                R.id.leftHand -> "慣用手:左"
                R.id.rightHand -> "慣用手:右"
                else -> "慣用手:"
            }

            gHand = hand.substring(4)
            gcode = subjCode.text.toString()

            val selectCity: String = "城市:" + city.selectedItem.toString()
            val name = "姓名:"+subjName.text.toString()
            val code = "編號:"+subjCode.text.toString()
            val bDate = "生日:"+subjBirth.text.toString()
            val emp = "\n"

            val txtFile: File
            //建立檔案資料夾
            val filePath = File(getExternalFilesDir("").toString(), subjCode.text.toString())

            if (filePath.exists()) {
                Toast.makeText(this, "這個編號已經使用過了", Toast.LENGTH_SHORT).show()
                val s=Calendar.getInstance().time
                txtFile = File(filePath, subjCode.text.toString() + "_demographic_"+ s+".txt")
            } else {
                filePath.mkdir()
                txtFile = File(filePath, subjCode.text.toString() + ".txt")

                filePathStr = filePath.path
                //儲存基本資料檔案
                val out = FileOutputStream(txtFile, true)
                out.write(name.toByteArray())
                out.write(emp.toByteArray())
                out.write(gender.toByteArray())
                out.write(emp.toByteArray())
                out.write(bDate.toByteArray())
                out.write(emp.toByteArray())
                out.write(hand.toByteArray())
                out.write(emp.toByteArray())
                out.write(grade.toByteArray())
                out.write(emp.toByteArray())
                out.write(selectCity.toByteArray())
                out.write(emp.toByteArray())
                out.write(code.toByteArray())
                out.flush()
                out.close()
                Toast.makeText(this, "Demographic Data Save Success", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, TaskSelectionPage::class.java)
                startActivity(intent)
            }
        }





    } //selectionpage


    //以上 0804測試
}