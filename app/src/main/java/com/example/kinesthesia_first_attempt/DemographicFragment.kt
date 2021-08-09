package com.example.kinesthesia_first_attempt

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kinesthesia_first_attempt.databinding.FragmentDemographicBinding
import com.example.kinesthesia_first_attempt.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

// 變數宣告
var filePathStr:String = ""
//var mContext_demo: Context? = null
lateinit var mContext_demo: Context
lateinit var city: Spinner
val cityList = arrayListOf("高雄市", "台南市", "台北市", "新北市", "桃園市","台中市","基隆市","新竹市",
    "嘉義市","新竹縣","苗栗縣","彰化縣","南投縣","雲林縣","嘉義縣","屏東縣","宜蘭縣","花蓮縣","臺東縣","澎湖縣", "其他")


class DemographicFragment : Fragment() {


    private val sharedViewModel: MainViewModel by activityViewModels()
    //private lateinit var viewModel: MainViewModel
    //private var binding: FragmentDemographicBinding? = null
    private lateinit var binding: FragmentDemographicBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val fragmentBinding = FragmentDemographicBinding.inflate(inflater, container, false)
        //binding = fragmentBinding
        //return fragmentBinding.root
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_demographic, container, false)
        return binding.root
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

        //city選單CODE
        mContext_demo = requireActivity().applicationContext

        city = requireView()!!.findViewById<View>(R.id.city) as Spinner

        //城市選單CODE: arrayList已經移置string ,name: city_list
        val adapter = ArrayAdapter.createFromResource( mContext_demo , R.array.city_list, android.R.layout.simple_spinner_dropdown_item)
        city.adapter = adapter

        city.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.viewModel?.setCity(cityList[position])
                Toast.makeText(activity, "城市:"+cityList[position] , Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
        //以上: 城市選單CODE: arrayList已經移置string ,name: city_list




    } //OnViewCreated End


    companion object {

    }


    fun goToIntroduction() {
        Toast.makeText(activity, "開始測驗說明", Toast.LENGTH_SHORT).show()
        //使用以下code來抓取navController，用findNavController().navigate()，並輸入"動作的ID"。也就是要執行的nav動作(要和nav_graph.xml相同
        findNavController().navigate(R.id.action_demographicFragment_to_introFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }



    /*

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



        binding?.DemoNext?.setOnClickListener {
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

            /*
            val subjName = findViewById<EditText>(R.id.subjName)
            val subjCode = findViewById<EditText>(R.id.subjCode)
            val subjBirth = findViewById<EditText>(R.id.birthDate)
            val rg = findViewById<RadioGroup>(R.id.GenderGroup)
            val subjGrade = findViewById<RadioGroup>(R.id.GradeGroup)
            val subjHand = findViewById<RadioGroup>(R.id.HandGroup)
            val gender:String
            val grade:String
            val hand:String
            */

            //依選取項目顯示不同訊息
            val gender = when (rg.checkedRadioButtonId) {
                R.id.male -> "性別:男"
                R.id.female -> "性別:女"
                else -> "性別:"
            }

            val grade = when (subjGrade.checkedRadioButtonId) {
                R.id.Grade1 -> "年級:1"
                R.id.Grade2 -> "年級:2"
                R.id.Grade3 -> "年級:3"
                R.id.Grade4 -> "年級:4"
                R.id.Grade5 -> "年級:5"
                R.id.Grade6 -> "年級:6"
                else -> "年級:"
            }

            val hand = when (subjHand.checkedRadioButtonId) {
                R.id.leftHand -> "慣用手:左"
                R.id.rightHand -> "慣用手:右"
                else -> "慣用手:"
            }

            gHand = hand.substring(4)
            gcode = subjCode.text.toString()

            val selectCity: String = "城市:" + city.selectedItem.toString()
            val name = "姓名:"+ subjName.text.toString()
            val code = "編號:"+ subjCode.text.toString()
            val bDate = "生日:"+ subjBirth.text.toString()
            val emp = "\n"

            val txtFile: File
            //建立檔案資料夾
            val filePath = File(getExternalFilesDir("").toString(), subjCode.text.toString())

            if (filePath.exists()) {
                Toast.makeText(activity, "這個編號已經使用過了", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(activity, "Demographic Data Save Success", Toast.LENGTH_SHORT).show()

                //val intent = Intent(this, TaskSelectionPage::class.java)
                //startActivity(intent)
            }
        }

    } //selectionpage


    //以上 0804測試   */













    // 按鈕後確認資料都有輸入
     fun checkInputAndUpdate() {

        //1.讀取目前使用者輸入內容。
        // 註:性別、慣用手、年級在輸入後會由dataBinding及Livedata自動更新
        val nameInput = binding.subjName.text.toString()  //讀取使用者輸入的值，並且轉換為指定的資料型態
        val codeInput = binding.subjCode.text.toString()
        val birthdateInput = binding.birthDate.text.toString()


        //2&3.確認所有輸入都非空白，並顯示警告訊息
        when {
            nameInput.isNullOrEmpty() -> {
                Toast.makeText(activity, "未輸入姓名", Toast.LENGTH_SHORT).show()
            }
            codeInput.isNullOrEmpty() -> {
                Toast.makeText(activity, "未輸入編碼", Toast.LENGTH_SHORT).show()
            }
            birthdateInput.isNullOrEmpty() -> {
                Toast.makeText(activity, "未輸入生日", Toast.LENGTH_SHORT).show()
            }
            binding?.viewModel!!.hasNoHandSet() -> {
                Toast.makeText(activity, "未輸入慣用手", Toast.LENGTH_SHORT).show()
            }
            binding?.viewModel!!.hasNoGradeSet() -> {
                Toast.makeText(activity, "未輸入年級", Toast.LENGTH_SHORT).show()
            }
            binding?.viewModel!!.hasNoSexSet() -> {
                Toast.makeText(activity, "未輸入性別", Toast.LENGTH_SHORT).show()
            }
            binding?.viewModel!!.hasNoCitySet() -> {
                Toast.makeText(activity, "未輸入城市", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //4.更新人口學資料。註:性別、慣用手、年級在輸入後會由Databinding及Livedata自動更新，在此不用更動
                // 目前 城市(spinner)、生日及當日日期(還沒有function)還未加入
                binding.viewModel?.setName(nameInput)
                binding.viewModel?.setBirthdate(birthdateInput)
                binding.viewModel?.setClientCode(codeInput)

                //5. 顯示目前資料對話框
                showCurrentInputDialog()
            }
        }
    }




        fun showCurrentInputDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.demographic_dialog)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                getString(R.string.your_name, binding.viewModel?.name?.value)
                        + "\n" + getString(R.string.your_sex, binding.viewModel?.sex?.value)
                        + "\n" + getString(R.string.your_birthdate, binding.viewModel?.birthdate?.value)
                        + "\n" + getString(R.string.your_handedness, binding.viewModel?.handedness?.value)
                        + "\n" + getString(R.string.your_grade, binding.viewModel?.grade?.value)
                        + "\n" + getString(R.string.your_city, binding.viewModel?.city?.value)
                        + "\n" + getString(R.string.your_code, binding.viewModel?.clientCode?.value)
            ) //Set the message to show the data

            .setCancelable(false)  // alert dialog not cancelable when the back key is pressed,

            .setNegativeButton(getString(R.string.modify_input)) { _, _ ->  //Add two text buttons EXIT and PLAY AGAIN using the methods
                binding.viewModel?.resetDemographicInput()
            }
            .setPositiveButton(getString(R.string.confirm_input)) { _, _ ->
                goToIntroduction()
            }
            .show() //creates and then displays the alert dialog.

    }
    //Context as the name suggests means the context or the current state of the application, activity, or fragment.
// It contains the information regarding the activity, fragment or application.





} //fragment end