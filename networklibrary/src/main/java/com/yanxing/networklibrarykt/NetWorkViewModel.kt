package com.yanxing.networklibrarykt

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanxing.networklibrarykt.dialog.LoadDialog
import com.yanxing.networklibrarykt.model.ResultModel
import com.yanxing.networklibrarykt.util.ToastUtil
import com.yanxing.networklibrarykt.util.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * @author 李双祥 on 2021/2/3.
 */
class NetWorkViewModel:ViewModel(){

    fun <E> request(serviceAPIMethod: suspend () -> ResultModel<E>, observer: SimpleAbstractObserver<E>) {
        viewModelScope.launch {
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
                        observer.context?.let { ToastUtil.showToast(it,
                            if (TextUtils.isEmpty(result.msg)) result.message else result.msg
                        ) }
                    }
                }
        }
    }

    fun <E> requestHasProgress(serviceAPIMethod: suspend () -> ResultModel<E>, fragmentManager:
    FragmentManager, toast: String
                                  , observer: SimpleAbstractObserver<E>) {
        viewModelScope.launch {
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
                        observer.context?.let { ToastUtil.showToast(it,
                            if (TextUtils.isEmpty(result.msg)) result.message else result.msg
                        ) }

                    }
                }
        }
    }

    fun <E> requestHasProgress(serviceAPIMethod: suspend () -> ResultModel<E>,fragmentManager: FragmentManager
                                  ,observer: SimpleAbstractObserver<E>) {
        requestHasProgress(serviceAPIMethod,fragmentManager,"努力加载中...",observer)
    }

}