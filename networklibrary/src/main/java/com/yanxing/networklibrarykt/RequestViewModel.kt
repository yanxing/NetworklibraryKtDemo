package com.yanxing.networklibrarykt

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanxing.networklibrarykt.dialog.LoadDialog
import com.yanxing.networklibrarykt.model.ResultModel
import com.yanxing.networklibrarykt.util.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * @author 李双祥 on 2021/2/3.
 */
class RequestViewModel : ViewModel() {

    /**
     * 不带对话框的请求
     * @param serviceAPI 请求接口
     * @param success 业务层面请求成功
     * @param error 业务层面请求失败
     * @param catch 异常
     * @param complete 请求完成
     */
    fun <E> request(serviceAPI: suspend () -> ResultModel<E>, success: suspend (data: E) -> Unit
                    , error: suspend (message: String) -> Unit, catch: suspend (message: String?) -> Unit
                    , complete: suspend () -> Unit) {
        viewModelScope.launch {
            flow {
                emit(serviceAPI)
            }.flowOn(Dispatchers.Main)
                .onCompletion {
                    complete.invoke()
                }.catch {
                    catch.invoke(it.message)
                }.collect {
                    val result = it.invoke()
                    //业务成功
                    if (isSuccess(result.status) || isSuccess(result.code)) {
                        result.data?.apply { success.invoke(this) }
                    } else {
                        val msg = if (TextUtils.isEmpty(result.msg)) result.message else result.msg
                        error.invoke(msg)
                    }
                }
        }
    }

    /**
     * 带对话框的请求
     * @param serviceAPI 请求接口
     * @param success 业务层面请求成功
     * @param error 业务层面请求失败
     * @param catch 异常
     * @param complete 请求完成
     */
    fun <E> requestHasProgress(serviceAPI: suspend () -> ResultModel<E>, fragmentManager: FragmentManager, toast: String,
                               success: suspend (data: E) -> Unit, error: suspend (message: String) -> Unit
                               , catch: suspend (message: String?) -> Unit, complete: suspend () -> Unit) {
        viewModelScope.launch {
            flow {
                emit(serviceAPI)
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
                    complete.invoke()
                    try {
                        val fragment = fragmentManager.findFragmentByTag(LoadDialog.TAG)
                        fragment?.let {
                            fragmentManager.beginTransaction().remove(fragment)
                                .commitNowAllowingStateLoss()
                        }
                    } catch (e: Exception) {
                    }
                }.catch {
                    catch.invoke(it.message)
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
                        result.data?.apply { success.invoke(this) }
                    } else {
                        val msg = if (TextUtils.isEmpty(result.msg)) result.message else result.msg
                        error.invoke(msg)
                    }
                }
        }
    }

}