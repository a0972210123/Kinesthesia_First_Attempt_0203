package com.example.kinesthesia_first_attempt

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*

// 變數宣告
var filePathStr: String = ""

//var mContext_demo: Context? = null
lateinit var mContext_demo: Context
lateinit var city: Spinner
lateinit var birthdate: EditText
val cityList = arrayListOf(
    "高雄市", "台南市", "台北市", "新北市", "桃園市", "台中市", "基隆市", "新竹市",
    "嘉義市", "新竹縣", "苗栗縣", "彰化縣", "南投縣", "雲林縣", "嘉義縣", "屏東縣", "宜蘭縣", "花蓮縣", "臺東縣", "澎湖縣", "其他"
)


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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_demographic, container, false)
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
        val adapter = ArrayAdapter.createFromResource(
            mContext_demo,
            R.array.city_list,
            android.R.layout.simple_spinner_dropdown_item
        )
        city.adapter = adapter
        city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.viewModel?.setCity(cityList[position])
                //Toast.makeText(activity, "城市:"+cityList[position] , Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
        //以上: 城市選單CODE: arrayList已經移置string ,name: city_list


        //生日選擇及測驗日期選擇
        //延遲至所有lifecycle call完成再執行避免crash 及 dialog需特定context輸入
        // reference:
        // 1. https://stackoverflow.com/questions/4187673/problems-creating-a-popup-window-in-android-activity
        // 2. https://stackoverflow.com/questions/61237173/kotlin-datepicker-pop-up-doesnt-work-inside-fragment-windowmanagerbadtokenex
        birthdate = requireView()!!.findViewById<View>(R.id.birthDate) as EditText
        birthdate.post(Runnable { dateSelection() })
        //以上生日選擇及測驗日期選擇

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

    fun citySelection() {
        // 之後可以將CODE從onViewCreated移到這裡
    }

    // 生日或是測驗日期選擇
    fun dateSelection() {
        binding?.birthDate?.inputType = InputType.TYPE_NULL
        binding?.birthDate?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            //
            DatePickerDialog(requireActivity(), { _, year, month, day ->
                run {
                    //將月前加上0
                    val month2 = month + 1
                    var month3: String = month2.toString()
                    if (month2 < 10) {
                        month3 = "0$month2"
                    }
                    //將日前加上0
                    val day2 = day + 1
                    var day3: String = day2.toString()
                    if (day2 < 10) {
                        day3 = "0$day2"
                    }
                    val subjDate = "$month3/$day3/$year"
                    binding?.birthDate?.setText(subjDate)
                    binding.viewModel?.setBirthdate("$month3/$day3/$year")
                    Log.d("DemographicFragment", "birthDate:$month3/$day3/$year")
                }
            }, year, month, day).show()
            //Log.d("DemographicFragment", "birthDate:$year/$month/$day")
            binding.viewModel?.setTestDate(getTestDate())
            Log.d("DemographicFragment", "testDate:" + getTestDate())
        }
    } // 以上生日選擇function


    // 測驗日期選擇function
    //val tempTestDate = getTestDate()
    fun getTestDate(): String {
        var currentDate: String = ""
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        currentDate = formatter.format(calendar.time).toString()
        return currentDate
    }

    // 其他格式範例
    var formateA = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒")
    var formateB = SimpleDateFormat("yyyy/MM/dd")
    var formateC = SimpleDateFormat("yyyy-MM-dd")
    // pattern reference: https://www.itread01.com/content/1541297598.html
    /*
    SimpleDateFormat函式語法：
    G 年代標誌符
    y 年
    M 月
    d 日
    h 時 在上午或下午 (1~12)
    H 時 在一天中 (0~23)
    m 分
    s 秒
    S 毫秒
    E 星期
    D 一年中的第幾天
    F 一月中第幾個星期幾
    w 一年中第幾個星期
    W 一月中第幾個星期
    a 上午 / 下午 標記符
    k 時 在一天中 (1~24)
    K 時 在上午或下午 (0~11)
    z 時區
     */





    /*
    // 存檔function 零件
    // 0804測試
    private fun selectionPage() {

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

   // 整理人口學資料段落

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
    // 待處理: 需加入存檔function
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


    fun showCurrentInputDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.demographic_dialog)) //Set the title on the alert dialog, use a string resource from strings.xml.et the message to show the final score,
            .setMessage(
                getString(R.string.your_name, binding.viewModel?.name?.value)
                        + "\n" + getString(R.string.your_sex, binding.viewModel?.sex?.value)
                        + "\n" + getString(R.string.your_birthdate, binding.viewModel?.birthdate?.value)
                        + "\n" + getString(R.string.your_testDate, binding.viewModel?.testDate?.value)
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