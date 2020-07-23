package com.yanxing.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import com.yanxing.networklibrarykt.RetrofitManage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val header = ArrayMap<String, String>()
        header.put("app_id", "oikqyppvlokrnkpq")
        header.put("app_secret", "YlpnRkR2TjhNRS9EU0ZKenFVNmllZz09")
        RetrofitManage.init("https://www.mxnzp.com/api/",header, true)
    }

    fun getWeather(){

        viewModelScope
    }
}