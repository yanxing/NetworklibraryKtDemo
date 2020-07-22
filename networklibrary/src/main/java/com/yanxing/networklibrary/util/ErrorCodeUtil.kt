package com.yanxing.networklibrary.util

import android.accounts.NetworkErrorException
import android.text.TextUtils
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * @author 李双祥 on 2020/7/22.
 */
object ErrorCodeUtil {

    private const val SUCCESS_CODE = 1
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
     *
     * @param e
     */
    fun getException(e: Throwable): String? {
        if (!TextUtils.isEmpty(e.message)) {
            LogUtil.e(TAG, e.message!!)
        }
        return if (e is JSONException || e is JsonSyntaxException) {
            PARSE_JSON_FAIL
        } else if (e is ConnectException
            || e is NetworkErrorException
            || e is UnknownHostException
        ) {
            NETWORK_ERROR
        } else if (e is SocketTimeoutException) {
            CONNET_SERVICE_TIME_OUT
        } else if (e is SSLHandshakeException) {
            SSL_ERROR
        } else if (e is IOException) {
            //unexpected end of stream on Connection 情况
            CONNECT_EXCEPTION
        } else {
            e.message
        }
    }

    /**
     * 获取服务器错误代码代表的错误信息
     *
     * @param code
     * @return
     */
    fun getMessage(code: Int): String {
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
     * 这里status为1或者success时，代表成功
     *
     * @param status
     * @return
     */
    fun isSuccess(status: String): Boolean {
        return SUCCESS_CODE.toString() == status || "success" == status
    }
}