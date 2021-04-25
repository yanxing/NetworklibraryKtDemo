package com.yanxing.networklibrarykt

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.yanxing.networklibrarykt.util.LogUtil
import com.yanxing.networklibrarykt.intercepter.Interceptor
import com.yanxing.networklibrarykt.intercepter.ParameterInterceptor
import com.yanxing.networklibrarykt.model.ResultModel
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
    fun init(baseUrl: String, log: Boolean) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor())
        init(baseUrl, okHttpClientBuilder, log)
    }

    fun init(baseUrl: String, okHttpClientBuilder: OkHttpClient.Builder, log: Boolean) {
        LogUtil.isDebug = log
        this.okHttpClientBuilder = okHttpClientBuilder
        retrofitBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(okHttpClientBuilder.build())
    }

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


    fun <E> request(fragmentActivity: FragmentActivity, serviceAPIMethod: suspend () -> ResultModel<E>, observer: SimpleAbstractObserver<E>) {
        val netWorkViewModel = ViewModelProvider(fragmentActivity).get(NetWorkViewModel::class.java)
        netWorkViewModel.request(fragmentActivity, serviceAPIMethod, observer)
    }

    fun <E> request(fragment: Fragment, serviceAPIMethod: suspend () -> ResultModel<E>, observer: SimpleAbstractObserver<E>) {
        val netWorkViewModel = ViewModelProvider(fragment).get(NetWorkViewModel::class.java)
        fragment.context?.let { netWorkViewModel.request(it, serviceAPIMethod, observer) }
    }

    /**
     * 含有等待对话框
     * @param serviceAPIMethod 请求挂起函数
     * @param toast 提示文字
     */
    fun <E> requestDialog(fragmentActivity: FragmentActivity, serviceAPIMethod: suspend () -> ResultModel<E>, toast: String, observer: SimpleAbstractObserver<E>) {
        val netWorkViewModel = ViewModelProvider(fragmentActivity).get(NetWorkViewModel::class.java)
        netWorkViewModel.requestHasProgress(fragmentActivity, serviceAPIMethod, fragmentActivity.supportFragmentManager, toast, observer)
    }

    fun <E> requestDialog(fragment: Fragment, serviceAPIMethod: suspend () -> ResultModel<E>, toast: String, observer: SimpleAbstractObserver<E>) {
        val netWorkViewModel = ViewModelProvider(fragment).get(NetWorkViewModel::class.java)
        fragment.context?.let {
            netWorkViewModel.requestHasProgress(it, serviceAPIMethod, fragment.fragmentManager!!, toast, observer)
        }
    }

    fun <E> requestDialog(fragmentActivity: FragmentActivity, serviceAPIMethod: suspend () -> ResultModel<E>, observer: SimpleAbstractObserver<E>) {
        val netWorkViewModel = ViewModelProvider(fragmentActivity).get(NetWorkViewModel::class.java)
        netWorkViewModel.requestHasProgress(fragmentActivity, serviceAPIMethod, fragmentActivity.supportFragmentManager, "", observer)
    }

    fun <E> requestDialog(fragment: Fragment, serviceAPIMethod: suspend () -> ResultModel<E>, observer: SimpleAbstractObserver<E>) {
        val netWorkViewModel = ViewModelProvider(fragment).get(NetWorkViewModel::class.java)
        netWorkViewModel.requestHasProgress(fragment.context!!, serviceAPIMethod, fragment.fragmentManager!!, "", observer)
    }

//    fun <E> requestDialog(fragment: FragmentActivity, serviceAPIMethod: ResultModel<Any>) {
//        val netWorkViewModel = ViewModelProvider(fragment).get(NetWorkViewModel::class.java)
//        netWorkViewModel.requestHasProgress(fragment.context!!, serviceAPIMethod, fragment.fragmentManager!!, "", null)
//    }


}

abstract class SimpleAbstractObserver<T> {

    /**
     * 接口状态码成功的情况下调用
     */
    abstract fun onCall(value: T)

    /**
     * 接口状态码不成功情况下调用
     */
    fun onFail() {}

    fun onError() {}

    fun onComplete() {}
}