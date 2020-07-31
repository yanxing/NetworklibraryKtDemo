package com.yanxing.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.widget.Toast
import com.yanxing.networklibrarykt.OnCallListener
import com.yanxing.networklibrarykt.RetrofitManage
import com.yanxing.networklibrarykt.model.ResultModel
import com.yanxing.networklibrarykt.util.LogUtil

class MainActivity : AppCompatActivity() {

    private val serviceAPI by lazy {
        val header = ArrayMap<String, String>()
        header["app_id"] = "oikqyppvlokrnkpq"
        header["app_secret"] = "YlpnRkR2TjhNRS9EU0ZKenFVNmllZz09"
        RetrofitManage.init(this,"https://www.mxnzp.com/api/",header, true)
        RetrofitManage.getRetrofit().create(ServiceAPI::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitManage.requestData({ serviceAPI.getWeather("上海") },
            object : OnCallListener<Weather>() {
                override fun onCall(value: Weather) {
                     LogUtil.d("结果",value.toString())
                }
            })


        runCatching {

        }.onSuccess {

        }.onFailure {

        }
    }
}