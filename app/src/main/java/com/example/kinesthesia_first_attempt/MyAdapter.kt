package com.example.kinesthesia_first_attempt

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//ref: https://codertw.com/%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80/712473/#outline__2_1

class MyAdapter : RecyclerView.Adapter<MyAdapter.PagerViewHolder>() {
    private var mList: List<Int> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        return PagerViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindData(mList[position])
    }

    fun setList(list: List<Int>) {
        mList = list
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    //	ViewHolder需要繼承RecycleView.ViewHolder
    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextView: TextView = itemView.findViewById(R.id.page_number)
        private var colors = arrayOf("#CCFF99","#41F1E5","#8D41F1","#FF99CC")
        fun bindData(i: Int) {
            mTextView.text = i.toString()
            mTextView.setBackgroundColor(Color.parseColor(colors[i-1]))
        }
    }
}