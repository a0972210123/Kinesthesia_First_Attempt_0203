package com.example.kinesthesia_first_attempt

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PagerAdapter : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    // 建立一個array
    private val colorArray = ArrayList<Int>()

    // 在array中存放頁面，並通知系統資料更動
    private fun addPage() {
        colorArray.add(Color.rgb(255, 255, 255))
        notifyDataSetChanged()
    }

    // 起始先增加四頁
    init {
        addPage()
        addPage()
        addPage()
        addPage()
    }

    // 計算陣列大小
    //override fun getItemCount(): Int = colorArray.size

    //將需要滑動的頁面載入
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PagerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page,parent, false)
        )

    // 載入頁面中的View
    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTextView: TextView = itemView.findViewById(R.id.page_number)

        private var colors = arrayOf("#FFFFFFFF","#FFFFFFFF","#FFFFFFFF","#FFFFFFFF","#FFFFFFFF")

        fun bindData(i: Int) {
            mTextView.setBackgroundColor(Color.parseColor(colors[i-1]))
        }
    }


    //將滑動頁面中的參數、圖樣、背景更新
    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindData(mList[position])  //Call function更新背景

        // 用條件判斷更新文字
        holder.itemView.run{
            when (position) {
                0 -> {
                    holder.mTextView.text = "歡迎使用本系統"
                }
                1 -> {
                    holder.mTextView.text = "此評量需約XX分鐘，請確認你處於舒適的坐姿"
                }
                2 -> {
                    holder.mTextView.text = "評量時，需要用你的手或請你握著筆，在平板上移動到不同的位置"
                }
                3 -> {
                    holder.mTextView.text = "準備好了，請按開始"
                }
                4 -> {
                    holder.mTextView.text = "準備好了，請按開始"
                }
            }
        }


    }



    //補充
    private var mList: List<Int> = ArrayList()

    fun setList(list: List<Int>) {
        mList = list
    }

    override fun getItemCount(): Int {
        return mList.size
    }








}