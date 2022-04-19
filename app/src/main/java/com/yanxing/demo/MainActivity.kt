package com.yanxing.demo

import android.util.ArrayMap
import com.dianmei.baselibrary.BaseActivity
import com.yanxing.demo.databinding.ActivityMainBinding
import com.yanxing.networklibrarykt.RetrofitManage

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
        RetrofitManage.request(this, { serviceAPI.getWeather("上海") }, {
            //success挂起函数，业务层面状态码成功data数据，必写
            binding.content.text = it.toString()
        }, {
            //error挂起函数，业务层面状态码失败，可以不写此部分
        }, {
            //catch挂起函数，请求报错，可以不写此部分
        }, {
            //complete挂起函数，请求完成，可以不写此部分
        }, {
            //collect挂起函数，ResultModel<T>数据，可以不写此部分
        })
    }

    override fun initBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}