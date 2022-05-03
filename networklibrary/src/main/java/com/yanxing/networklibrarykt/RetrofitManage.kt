package com.yanxing.networklibrarykt

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
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

    //业务错误信息全局回调
    private var onObserver :((errorCode:String,errorMessage:String)->Unit)?=null

    /**
     * 初始化retrofit
     * @param baseUrl
     * @param log true打印日志
     */
    fun init(baseUrl: String, log: Boolean,onObserver:((errorCode:String,errorMessage:String)->Unit)?) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor())
        init(baseUrl, okHttpClientBuilder, log,onObserver)
    }


    fun init(baseUrl: String, headers: Map<String, String>, log: Boolean
             ,onObserver: ((errorCode:String,errorMessage:String)->Unit)?) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor(headers))
        init(baseUrl, okHttpClientBuilder, log,onObserver)
    }

    fun init(baseUrl: String, okHttpClientBuilder: OkHttpClient.Builder, log: Boolean
             ,onObserver:((errorCode:String,errorMessage:String)->Unit)?) {
        LogUtil.isDebug = log
        this.onObserver=onObserver
        this.okHttpClientBuilder = okHttpClientBuilder
        retrofitBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(okHttpClientBuilder.build())
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

    fun getOkHttpClientBuilder()= okHttpClientBuilder

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

    fun getRetrofit()=retrofitBuilder.build()

    private fun getOkHttpClientBuilderTimeout()=OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .writeTimeout(60L, TimeUnit.SECONDS)

    /**
     * 不带对话框的请求
     * @param owner ViewModelStoreOwner
     * @param serviceAPI 请求接口
     * @param success 业务层面状态码成功
     * @param error 业务层面状态码失败
     * @param catch 异常
     * @param complete 请求完成
     * @param collect ResultModel<T>数据
     */
    fun <T> request(owner: ViewModelStoreOwner, serviceAPI: suspend () -> ResultModel<T>, success: suspend (data: T?) -> Unit
                    , error: suspend (message: String) -> Unit={}, catch: suspend (message: String?) -> Unit={}
                    , complete: suspend () -> Unit={}, collect:suspend (ResultModel:ResultModel<T>)->Unit={}) {
        val requestViewModel = ViewModelProvider(owner).get(RequestViewModel::class.java)
        requestViewModel.request(serviceAPI,success,error,catch,complete,collect, onObserver)
    }

    fun <T> request(fragmentActivity: FragmentActivity, serviceAPI: suspend () -> ResultModel<T>, success: suspend (data: T?) -> Unit
                    , error: suspend (message: String) -> Unit={}, catch: suspend (message: String?) -> Unit={}
                    , complete: suspend () -> Unit={}, collect:suspend (ResultModel:ResultModel<T>)->Unit={}) {
        val requestViewModel = ViewModelProvider(fragmentActivity).get(RequestViewModel::class.java)
        requestViewModel.request(serviceAPI,success,error,catch,complete,collect, onObserver)
    }

    fun <T> request(fragment: Fragment, serviceAPI: suspend () -> ResultModel<T>, success: suspend (data: T?) -> Unit
                    , error: suspend (message: String) -> Unit={}, catch: suspend (message: String?) -> Unit={}
                    , complete: suspend () -> Unit={}, collect:suspend (ResultModel:ResultModel<T>)->Unit={}) {
        val requestViewModel = ViewModelProvider(fragment).get(RequestViewModel::class.java)
        requestViewModel.request(serviceAPI,success,error,catch,complete,collect, onObserver)
    }

    /**
     * 带对话框的请求
     * @param fragmentActivity
     * @param serviceAPI 请求接口
     * @param success 业务层面状态码成功
     * @param error 业务层面状态码失败
     * @param catch 异常
     * @param complete 请求完成
     * @param collect ResultModel<T>数据
     */
    fun <T> requestDialog(fragmentActivity: FragmentActivity, serviceAPI: suspend () -> ResultModel<T>
                          , success: suspend (data: T?) -> Unit, error: suspend (message: String) -> Unit={}
                          , catch: suspend (message: String?) -> Unit={}, complete: suspend () -> Unit={}
                          , collect:suspend (ResultModel:ResultModel<T>)->Unit={}) {
        val netWorkViewModel = ViewModelProvider(fragmentActivity).get(RequestViewModel::class.java)
        netWorkViewModel.requestHasProgress(serviceAPI, fragmentActivity.supportFragmentManager, ""
            , success,error,catch,complete,collect, onObserver)
    }



    /**
     * 带对话框的请求
     * @param fragment
     * @param serviceAPI 请求接口
     * @param success 业务层面状态码成功
     * @param error 业务层面状态码失败
     * @param catch 异常
     * @param complete 请求完成
     * @param collect ResultModel<T>数据
     */
    fun <T> requestDialog(fragment: Fragment, serviceAPI: suspend () -> ResultModel<T>
                          , success: suspend (data: T?) -> Unit, error: suspend (message: String) -> Unit={}
                          , catch: suspend (message: String?) -> Unit={}, complete: suspend () -> Unit={}
                          , collect:suspend (ResultModel:ResultModel<T>)->Unit={}) {
        val netWorkViewModel = ViewModelProvider(fragment).get(RequestViewModel::class.java)
        fragment.context?.let {
            netWorkViewModel.requestHasProgress(serviceAPI, fragment.requireActivity().supportFragmentManager
                , "", success,error,catch,complete,collect, onObserver)
        }
    }
}