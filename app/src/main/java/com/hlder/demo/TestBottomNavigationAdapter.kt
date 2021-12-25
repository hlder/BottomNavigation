package com.hlder.demo

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.hlder.bottom.navigation.BottomNavigationAdapter

class TestBottomNavigationAdapter(private val context:Context): BottomNavigationAdapter<TestBottomNavigationAdapter.TestBottomNavigationViewHolder>() {

    override fun onCreateViewHolder(viewType: Int): TestBottomNavigationViewHolder {
        val textView = TextView(context).apply {
            setPadding(0,10,0,10)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setBackgroundResource(R.mipmap.bgw)
            text = "张三"
        }
        return TestBottomNavigationViewHolder(textView)
    }

    override fun onItemSelect(item: TestBottomNavigationViewHolder, position: Int) {
        (item.view as TextView).setTextColor(Color.RED)
    }

    override fun onItemDisSelect(item: TestBottomNavigationViewHolder, position: Int) {
        (item.view as TextView).setTextColor(Color.BLACK)
    }

    override fun getItemCount(): Int {
        return 5
    }

    class TestBottomNavigationViewHolder(view:View):ViewHolder(view){
    }
}