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
     * @param success 业务层面状态码成功
     * @param error 业务层面状态码失败
     * @param catch 异常
     * @param complete 请求完成
     * @param collect ResultModel<T>数据
     */
    fun <T> request(serviceAPI: suspend () -> ResultModel<T>, success: suspend (data: T) -> Unit
                    , error: suspend (message: String) -> Unit, catch: suspend (message: String?) -> Unit
                    , complete: suspend () -> Unit,collect:suspend (ResultModel:ResultModel<T>)->Unit) {
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
                    collect.invoke(result)
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
     * @param success 业务层面状态码成功
     * @param error 业务层面状态码失败
     * @param catch 异常
     * @param complete 请求完成
     * @param collect ResultModel<T>数据
     */
    fun <T> requestHasProgress(serviceAPI: suspend () -> ResultModel<T>, fragmentManager: FragmentManager, toast: String,
                               success: suspend (data: T) -> Unit, error: suspend (message: String) -> Unit
                               , catch: suspend (message: String?) -> Unit, complete: suspend () -> Unit
                               ,collect:suspend (ResultModel:ResultModel<T>)->Unit) {
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
                    collect.invoke(result)
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