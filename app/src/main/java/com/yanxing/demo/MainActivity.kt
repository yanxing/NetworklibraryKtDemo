package com.yanxing.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import com.yanxing.networklibrarykt.RetrofitManage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    init {
        val header = ArrayMap<String, String>()
        header["app_id"] = "oikqyppvlokrnkpq"
        header["app_secret"] = "YlpnRkR2TjhNRS9EU0ZKenFVNmllZz09"
        RetrofitManage.init("https://www.mxnzp.com/api/", header, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serviceAPI = RetrofitManage.getRetrofit().create(ServiceAPI::class.java)
 /*       RetrofitManage.request(this, { serviceAPI.getWeather("上海") }, {
            //success挂起函数，业务层面状态码成功data数据，必写
            content.text = it.toString()
        }, {
            //error挂起函数，业务层面状态码失败，可以不写此部分
        }, {
            //catch挂起函数，请求报错，可以不写此部分
        }, {
            //complete挂起函数，请求完成，可以不写此部分
        }, {
                //collect挂起函数，ResultModel<T>数据，可以不写此部分
        })*/

        RetrofitManage.request(this, { serviceAPI.getWeather("上海") }, {
            content.text = it.toString()
        })

        //含有加载对话框
/*        RetrofitManage.requestDialog(this, { serviceAPI.getWeather("上海") }, {
            content.text = it.toString()
        })*/

    }
}