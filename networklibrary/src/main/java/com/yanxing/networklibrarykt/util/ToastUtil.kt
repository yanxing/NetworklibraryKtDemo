package com.yanxing.networklibrarykt.util

import android.content.Context
import android.widget.Toast

/**
 * @author 李双祥 on 2020/7/31.
 */
object ToastUtil {

    fun showToast(context:Context,msg:String){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
    }
}