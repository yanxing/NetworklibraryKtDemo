package com.yanxing.networklibrary

import com.yanxing.networklibrary.util.LogUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * @author 李双祥 on 2020/7/22.
 */
object RetrofitManage {

    private lateinit var retrofitBuilder:Retrofit.Builder
    private lateinit var okHttpClientBuilder:OkHttpClient.Builder


    @Synchronized fun init(baseUrl:String,log:Boolean){
        LogUtil.isDebug=log
        okHttpClientBuilder= getOkHttpClientBuilderTimeout()
            //.addInterceptor()
    }

    private fun getOkHttpClientBuilderTimeout(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
    }

}