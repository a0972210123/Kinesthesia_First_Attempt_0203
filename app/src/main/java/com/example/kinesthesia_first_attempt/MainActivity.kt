package com.example.kinesthesia_first_attempt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    //private lateinit var viewModel: MainViewModel
    //private var binding: MainActivityBinding? = null

    // This line declares a top-level variable in the class for the binding object.
    // It's defined at this level because it will be used across multiple methods in MainActivity class.
    //lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController  // 將navController 設定為變數，使其可以被其他method使用


    override fun onCreate(savedInstanceState: Bundle?) { //修改一開始onCreate時要準備的layout內容
        super.onCreate(savedInstanceState)

        // from cupcake app : 與navigation 和 上放退出menu按鈕
        val navHostFragment = supportFragmentManager                   // 問題出在這邊
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
        //
        //setContentView(R.layout.main_activity)


        //if (savedInstanceState == null) {
       //     supportFragmentManager.beginTransaction()
        //        .replace(R.id.container, CoverFragment.newInstance())
        //        .commitNow()
      //  }

    }

    //test

    //Within the same class, add code to override the onSupportNavigateUp() function.
    // This code will ask the navController to handle navigating up in the app.
    // Otherwise, fall back to back to the superclass implementation
    // (in AppCompatActivity) of handling the Up button.
    override fun onSupportNavigateUp(): Boolean {
        return  navController.navigateUp() || super.onSupportNavigateUp()
    }  //up button指 螢幕下方的返回鍵  而不是APP 本身的actionbar


}