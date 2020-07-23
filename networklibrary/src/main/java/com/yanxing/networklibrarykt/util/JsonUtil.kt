package com.yanxing.networklibrarykt.util

import com.google.gson.*
import com.google.gson.annotations.Expose

/**
 * @author 李双祥 on 2020/7/23.
 */

/**
 * 过滤解析带有注解@Expose属性
 */
fun createGson(): Gson {
    return GsonBuilder()
        .addDeserializationExclusionStrategy(object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                return false
            }

            override fun shouldSkipField(f: FieldAttributes): Boolean {
                val expose = f.getAnnotation(Expose::class.java)
                expose?.apply {
                    if (deserialize) return true
                }
                return false
            }

        }).setLenient().create()
}