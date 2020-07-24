package com.yanxing.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.widget.Toast
import com.yanxing.networklibrarykt.RetrofitManage
import com.yanxing.networklibrarykt.model.ResultModel
import com.yanxing.networklibrarykt.util.LogUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity() {

    private val serviceAPI by lazy {
        val header = ArrayMap<String, String>()
        header["app_id"] = "oikqyppvlokrnkpq"
        header["app_secret"] = "YlpnRkR2TjhNRS9EU0ZKenFVNmllZz09"
        RetrofitManage.init("https://www.mxnzp.com/api/",header, true)
        RetrofitManage.getRetrofit().create(ServiceAPI::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainScope().launch {
            flow {
                emit(serviceAPI.getWeather("上海"))
            }.onStart {
                showDialog()
            }.flowOn(Dispatchers.Main)
                .onCompletion {
                    dismissDialog()
                }.catch {
                    dismissDialog()
                    showToast("异常" + it.message)
                }.collect {
                    content.text = it.data.toString()
                    LogUtil.d("结果", it.code + "  " + it.message + "  " + it.data)
                }
        }
    }

   fun <E> loadData(serviceAPIMethod:()->ResultModel<E>){
        MainScope().launch {
            flow {
                emit(serviceAPI.getWeather("上海"))
            }.onStart {
                showDialog()
            }.flowOn(Dispatchers.Main)
                .onCompletion {
                    dismissDialog()
                }.catch {
                    dismissDialog()
                    showToast("异常" + it.message)
                }.collect {
                    content.text = it.data.toString()
                    LogUtil.d("结果", it.code + "  " + it.message + "  " + it.data)
                }
        }
    }


    private fun showDialog(){}

    private fun dismissDialog(){}

    private fun showToast(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }
}