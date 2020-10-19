package com.yanxing.networklibrarykt

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import com.yanxing.networklibrarykt.dialog.LoadDialog
import com.yanxing.networklibrarykt.util.LogUtil
import com.yanxing.networklibrarykt.intercepter.Interceptor
import com.yanxing.networklibrarykt.intercepter.ParameterInterceptor
import com.yanxing.networklibrarykt.model.ResultModel
import com.yanxing.networklibrarykt.util.ToastUtil
import com.yanxing.networklibrarykt.util.createGson
import com.yanxing.networklibrarykt.util.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @author 李双祥 on 2020/7/22.
 */
object RetrofitManage {

    private lateinit var retrofitBuilder: Retrofit.Builder
    private lateinit var okHttpClientBuilder: OkHttpClient.Builder
    private lateinit var context: Context

    /**
     * 初始化retrofit
     * @param baseUrl
     * @param log true打印日志
     */
    @Synchronized
    fun init(context: Context, baseUrl: String, log: Boolean) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor())
        init(context, baseUrl, okHttpClientBuilder, log)
    }

    @Synchronized
    fun init(
        context: Context,
        baseUrl: String,
        okHttpClientBuilder: OkHttpClient.Builder,
        log: Boolean
    ) {
        this.context = context
        LogUtil.isDebug = log
        this.okHttpClientBuilder = okHttpClientBuilder
        retrofitBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(okHttpClientBuilder.build())
    }

    @Synchronized
    fun init(context: Context, baseUrl: String, headers: Map<String, String>, log: Boolean) {
        okHttpClientBuilder = getOkHttpClientBuilderTimeout()
            .addInterceptor(ParameterInterceptor(headers))
        init(context, baseUrl, okHttpClientBuilder, log)
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


    fun <E> requestData(serviceAPIMethod: suspend () -> ResultModel<E>, observer: SimpleAbstractObserver<E>) {
        MainScope().launch {

            flow {
                emit(serviceAPIMethod)
            }.flowOn(Dispatchers.Main)
                .onCompletion {
                    observer.onComplete()
                }.catch {
                    observer.onError()
                }.collect {
                    val result = it.invoke()
                    //业务成功
                    if (isSuccess(result.status) || isSuccess(result.code)) {
                        result.data?.apply { observer.onCall(this) }
                    } else {
                        result.data?.apply { observer.onFail() }
                        ToastUtil.showToast(
                            context,
                            if (TextUtils.isEmpty(result.msg)) result.message else result.msg
                        )
                    }
                }
        }
    }

    fun <E> requestDataWithDialog(serviceAPIMethod: suspend () -> ResultModel<E>,fragmentManager: FragmentManager,toast: String
                                  ,observer: SimpleAbstractObserver<E>) {
        MainScope().launch {
            flow {
                emit(serviceAPIMethod)
            }.onStart {
                try {
                    val fragment = fragmentManager.findFragmentByTag(LoadDialog.TAG)
                    if (fragment == null) {
                        val loadDialog = LoadDialog()
                        if (!TextUtils.isEmpty(toast)) {
                            val bundle = Bundle()
                            bundle.putString("tip", toast)
                            loadDialog.arguments = bundle
                        }
                        loadDialog.show(fragmentManager.beginTransaction(), LoadDialog.TAG)
                    }
                } catch (e: Exception) {
                }
            }.flowOn(Dispatchers.Main)
                .onCompletion {
                    observer.onComplete()
                    try {
                        val fragment = fragmentManager.findFragmentByTag(LoadDialog.TAG)
                        fragment?.let {
                            fragmentManager.beginTransaction().remove(fragment)
                                .commitNowAllowingStateLoss()
                        }
                    } catch (e: Exception) {
                    }
                }.catch {
                    observer.onError()
                    try {
                        val fragment = fragmentManager.findFragmentByTag(LoadDialog.TAG)
                        fragment?.let {
                            fragmentManager.beginTransaction().remove(fragment)
                                .commitNowAllowingStateLoss()
                        }
                    } catch (e: Exception) {
                    }
                }.collect {
                    val result = it.invoke()
                    //业务成功
                    if (isSuccess(result.status) || isSuccess(result.code)) {
                        result.data?.apply { observer.onCall(this) }
                    } else {
                        result.data?.apply { observer.onFail() }
                        ToastUtil.showToast(
                            context,
                            if (TextUtils.isEmpty(result.msg)) result.message else result.msg
                        )
                    }
                }
        }
    }

    fun <E> requestDataWithDialog(serviceAPIMethod: suspend () -> ResultModel<E>,fragmentManager: FragmentManager
                                  ,observer: SimpleAbstractObserver<E>) {
        requestDataWithDialog(serviceAPIMethod,fragmentManager,"努力加载中...",observer)
    }

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