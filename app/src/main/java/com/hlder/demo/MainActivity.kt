package com.hlder.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hlder.bottom.navigation.BottomNavigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigation>(R.id.bottomNavigation)

        bottomNavigation.setOnBottomNavigationSelectListener{
            println("=====================onBottomItemSelect:$it")
        }
        bottomNavigation.setAdapter(TestBottomNavigationAdapter(this))
    }
}