package com.yanxing.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import com.yanxing.networklibrarykt.RetrofitManage
import com.yanxing.networklibrarykt.util.LogUtil
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val header = ArrayMap<String, String>()
        header.put("app_id", "oikqyppvlokrnkpq")
        header.put("app_secret", "YlpnRkR2TjhNRS9EU0ZKenFVNmllZz09")
        RetrofitManage.init("https://www.mxnzp.com/api/",header, true)
        val serviceAPI=RetrofitManage.getRetrofit().create(ServiceAPI::class.java)

        val scope = MainScope()
        scope.launch {
            runCatching {
                serviceAPI.getWeather("上海")
            }.onSuccess {
               LogUtil.d("结果",it.code+"  "+it.message+"  "+it.data)
            }.onFailure {
                
            }

        }
    }
}