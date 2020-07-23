package com.yanxing.networklibrarykt.intercepter

import android.text.TextUtils
import com.yanxing.networklibrarykt.util.ErrorCodeUtil
import com.yanxing.networklibrarykt.util.LogUtil
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer

/**
 * @author 李双祥 on 2020/7/22.
 */
class ParameterInterceptor() : Interceptor {

    private val TAG = "networklibrary"
    private var headers: Map<String, String>? = null

    constructor(headers: Map<String, String>) : this() {
        this.headers = headers
    }

    override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
        val oldRequest = chain.request()
        val builder = oldRequest.newBuilder()
        headers?.let {
            for (entry in it.entries) {
                if (!TextUtils.isEmpty(entry.value)) {
                    builder.addHeader(entry.key, entry.value)
                }
            }
        }

        return if (!LogUtil.isDebug) {
            //不打印，不拼接参数，不重新构造
            chain.proceed(builder.build())
        } else {
            //get请求的参数
            val parameters = oldRequest.url().queryParameterNames()
            val getParams = StringBuilder()
            for (param in parameters) {
                getParams.append(param).append("=").append(oldRequest.url().queryParameter(param))
                    .append("  ")
            }


            //post请求的参数
            val postParams = StringBuilder()
            val requestBody = oldRequest.body()
            requestBody?.let {
                when (requestBody) {
                    is FormBody -> {
                        for (i in 0 until requestBody.size()) {
                            postParams
                                .append(requestBody.encodedName(i))
                                .append("=")
                                .append(requestBody.encodedValue(i))
                                .append("  ")
                        }
                    }
                    is MultipartBody -> {

                    }
                    else -> {
                        //json
                        val buffer = Buffer()
                        requestBody.writeTo(buffer)
                        postParams.append(buffer.readUtf8())
                    }
                }
            }


            //header参数
            val headerParams = StringBuilder()
            val newRequest = builder.build()
            for (name in newRequest.headers().names()) {
                headerParams.append(name).append("=")
                    .append(newRequest.headers().get(name))
                    .append("  ")
            }

            val b = System.currentTimeMillis()
            //此句异常，将不执行后续打印耗时代码
            val response = chain.proceed(newRequest)
            val a = System.currentTimeMillis()
            var message = ""
            if (!response.isSuccessful) {
                message = ErrorCodeUtil.getMessage(response.code())
            }
            val content = response.body()?.string()
            val headerParamStr =
                if (TextUtils.isEmpty(headerParams)) "" else "  header参数$headerParams"
            LogUtil.d(TAG,newRequest.url().url().toString() + "  请求参数:" + getParams.toString() + postParams.toString() + headerParamStr
                        + "\n请求耗时：" + (a - b) + "ms，" + "请求结果\n" + content + "\n")

            val body = ResponseBody.create(
                if (newRequest.body() == null) null else newRequest.body()?.contentType(), content
            )
            //重新构造body
            val server = response.newBuilder().body(body).build()
            server.newBuilder().message(message).build()
        }
    }
}