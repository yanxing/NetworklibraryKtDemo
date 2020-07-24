package com.yanxing.networklibrarykt.util

import android.util.Log

/**
 * @author 李双祥 on 2020/7/22.
 */
object LogUtil {

    var isDebug=false

    fun v(tag:String,msg:String){
        if (isDebug){
            print(1, tag, msg)
        }
    }

    fun d(tag:String,msg:String){
        if (isDebug){
            print(2, tag, msg)
        }
    }

    fun i(tag:String,msg:String){
        if (isDebug){
            print(3, tag, msg)
        }
    }

    fun w(tag:String,msg:String){
        if (isDebug){
            print(4, tag, msg)
        }
    }

    fun e(tag:String,msg:String){
        if (isDebug){
            print(5, tag, msg)
        }
    }

    private fun print(key:Int,tag:String,msg:String){
        var msgTemp=msg;
        //添加换行
        if (!msgTemp.contains(",\n")) {
            msgTemp = msgTemp.replace(",".toRegex(), ",\n   ")
        }
        if (!msgTemp.contains("{\n")) {
            msgTemp = msgTemp.replace("\\{".toRegex(), "{\n   ")
        }
        if (msgTemp.contains("},")) {
            msgTemp = msgTemp.replace("\\},".toRegex(), "\n},")
        }
        //防止中文过多，改为字符数，避免换行少字符
        val maxLogSize = 2001
        for (i in 0..msgTemp.length/maxLogSize){
            val start=i*maxLogSize
            var end=(i+1)*maxLogSize
            end=Math.min(end,msgTemp.length)
            when(key){
                1->{
                    Log.v(tag,msgTemp.substring(start,end))
                }
                2->{
                    Log.d(tag,msgTemp.substring(start,end))
                }
                3->{
                    Log.i(tag,msgTemp.substring(start,end))
                }
                4->{
                    Log.w(tag,msgTemp.substring(start,end))
                }
                5->{
                    Log.e(tag,msgTemp.substring(start,end))
                }
            }
        }
    }

}