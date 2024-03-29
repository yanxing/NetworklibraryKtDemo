package com.yanxing.demo

import com.yanxing.networklibrarykt.model.ResultModel
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author 李双祥 on 2020/4/24.
 */
interface ServiceAPI {

    /**
     * 手机号密码登录
     */
    @GET("weather/current/{cityName}")
    suspend fun getWeather(@Path("cityName") cityName: String): ResultModel<Weather>
}