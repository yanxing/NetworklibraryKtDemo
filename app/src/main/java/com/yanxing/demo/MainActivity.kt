package com.yanxing.demo

import android.util.ArrayMap
import com.dianmei.baselibrary.BaseActivity
import com.yanxing.demo.databinding.ActivityMainBinding
import com.yanxing.networklibrarykt.RetrofitManage
import com.yanxing.networklibrarykt.SimpleAbstractObserver

class MainActivity : BaseActivity<ActivityMainBinding>() {

    init {
        val header = ArrayMap<String, String>()
        header["app_id"] = "oikqyppvlokrnkpq"
        header["app_secret"] = "YlpnRkR2TjhNRS9EU0ZKenFVNmllZz09"
        RetrofitManage.init("https://www.mxnzp.com/api/",header, true)
    }

    override fun afterInstanceView() {
        viewModelStore.clear()
        val serviceAPI=RetrofitManage.getRetrofit().create(ServiceAPI::class.java)
        RetrofitManage.requestHasProgress(this,{serviceAPI.getWeather("上海") },supportFragmentManager,
            object : SimpleAbstractObserver<Weather>(this) {
                override fun onCall(value: Weather) {
                    binding.content.text=value.toString()
                }
            })
    }

    override fun initBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}