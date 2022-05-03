package com.yanxing.networklibrarykt.util

import android.accounts.NetworkErrorException
import android.text.TextUtils
import com.google.gson.JsonSyntaxException
import com.yanxing.networklibrarykt.model.ResultModel
import com.yanxing.networklibrarykt.util.LogUtil.e
import org.json.JSONException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * @author 李双祥 on 2020/7/22.
 */
private const val TAG = "networklibrary"
private const val PARSE_JSON_FAIL = "数据解析失败"
private const val NETWORK_ERROR = "连接服务器异常，请检查网络或稍后再试"
private const val CONNET_SERVICE_TIME_OUT = "连接服务器超时，请稍后再试"

private const val NOT_FOUND = 404
private const val METHOD_ERROR = 405
private const val REQUEST_TIMEOUT = 408
private const val INTERNAL_SERVER_ERROR = 500
private const val SERVICE_UNAVAILABLE = 503
private const val ADDRESS_ERROR = "请求地址错误"
private const val TIME_OUT = "请求超时,请检查当前网络"
private const val SERVICE_INTERNAL_ERROR = "服务器内部错误"
private const val SERVICE_NOT_AVAILABLE = "服务器不可用"
private const val REQUEST_METHOD_ERROR = "请求方法出错"
private const val CONNECT_EXCEPTION = "连接异常"
private const val SSL_ERROR = "CA证书不信任"

/**
 * 获取异常信息
 * @param exception
 */
fun getException(exception: Throwable): String {
    return if (exception is JSONException || exception is JsonSyntaxException) {
        PARSE_JSON_FAIL
    } else if (exception is ConnectException
        || exception is NetworkErrorException
        || exception is UnknownHostException
    ) {
        NETWORK_ERROR
    } else if (exception is SocketTimeoutException) {
        CONNET_SERVICE_TIME_OUT
    } else if (exception is SSLHandshakeException) {
        SSL_ERROR
    } else if (exception is IOException) {
        //unexpected end of stream on Connection 情况
        CONNECT_EXCEPTION
    } else {
        exception.message?:""
    }
}

/**
 * http层面失败请求吗
 */
fun getMessage(code: Int?): String {
    return when (code) {
        NOT_FOUND -> ADDRESS_ERROR
        REQUEST_TIMEOUT -> TIME_OUT
        INTERNAL_SERVER_ERROR -> SERVICE_INTERNAL_ERROR
        SERVICE_UNAVAILABLE -> SERVICE_NOT_AVAILABLE
        METHOD_ERROR -> REQUEST_METHOD_ERROR
        else -> "错误码：" + code + "请检查网络重试"
    }
}

/**
 * 获取接口失败信息
 */
fun <T> getMessage(resultModel: ResultModel<T>): String {
    return resultModel.message?:(resultModel.msg?:"")
}

/**
 * 获取接口失败状态码
 */
fun <T> getCode(resultModel: ResultModel<T>): String {
    return resultModel.code?:(resultModel.status?:"")
}

/**
 * 接口成功
 * @return
 */
fun  <T> isSuccess(resultModel: ResultModel<T>): Boolean {
    return resultModel.status=="1"||resultModel.code=="1"
}

/**
 * 获取接口成功的data数据
 */
fun  <T> getSuccessData(resultModel: ResultModel<T>): T? {
    return resultModel.data
}