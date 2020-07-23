package com.yanxing.networklibrarykt

import com.yanxing.networklibrarykt.util.LogUtil
import com.yanxing.networklibrarykt.intercepter.Interceptor
import com.yanxing.networklibrarykt.intercepter.ParameterInterceptor
import com.yanxing.networklibrarykt.util.createGson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author 李双祥 on 2020/7/22.
 */
object RetrofitManage {

    private lateinit var retrofitBuilder: Retrofit.Builder
    private lateinit var okHttpClientBuilder: OkHttpClient.Builder


    /**
     * 初始化retrofit
     * @param baseUrl
     * @param log true打印日志
     */
    @Synchronized
    fun init(baseUrl: String, log: Boolean) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor())
        init(baseUrl, okHttpClientBuilder, log)
    }

    @Synchronized
    fun init(baseUrl: String, okHttpClientBuilder: OkHttpClient.Builder, log: Boolean) {
        LogUtil.isDebug = log
        this.okHttpClientBuilder = okHttpClientBuilder
        retrofitBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(okHttpClientBuilder.build())
    }

    @Synchronized
    fun init(baseUrl: String, headers: Map<String, String>, log: Boolean) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor(headers))
        init(baseUrl, okHttpClientBuilder, log)
    }

    /**
     * 设置拦截器
     */
    fun setInterceptor(interceptor: Interceptor) {
        retrofitBuilder.client(okHttpClientBuilder.addInterceptor(interceptor).build())
    }

    /**
     * 设置请求头
     */
    fun setHeader(headers: Map<String, String>) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor(headers))
        retrofitBuilder.client(okHttpClientBuilder.build())
    }

    fun setOkHttpClientBuilder(okHttpClientBuilder: OkHttpClient.Builder) {
        this.okHttpClientBuilder = okHttpClientBuilder
    }

    fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        return this.okHttpClientBuilder
    }

    /**
     * 设置超时时间
     */
    fun setTimeOut(readTimeOut: Long, connectTimeOut: Long, writeTimeOut: Long) {
        okHttpClientBuilder
            .readTimeout(readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
            .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
        retrofitBuilder.client(okHttpClientBuilder.build())
    }

    fun getRetrofit(): Retrofit {
        return retrofitBuilder.build()
    }

    private fun getOkHttpClientBuilderTimeout(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
    }

}