package com.yanxing.networklibrarykt.model

/**
 * @author 李双祥 on 2020/7/23.
 */
class ResultModel<T>(
    /**
     * 可能用的状态码1
     */
    var status: String?,
    /**
     * 可能用的状态码2
     */
    var code: String?,
    var message: String?,
    var msg: String?,
    var data: T? = null
)