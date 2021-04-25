package com.yanxing.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import com.yanxing.networklibrarykt.RetrofitManage
import com.yanxing.networklibrarykt.SimpleAbstractObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    init {
        val header = ArrayMap<String, String>()
        header["app_id"] = "oikqyppvlokrnkpq"
        header["app_secret"] = "YlpnRkR2TjhNRS9EU0ZKenFVNmllZz09"
        RetrofitManage.init("https://www.mxnzp.com/api/",header, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModelStore.clear()
        val serviceAPI=RetrofitManage.getRetrofit().create(ServiceAPI::class.java)
        RetrofitManage.requestDialog(this,{serviceAPI.getWeather("上海") },
            object : SimpleAbstractObserver<Weather>() {
                override fun onCall(value: Weather) {
                    content.text=value.toString()
                }
            })
    }
}