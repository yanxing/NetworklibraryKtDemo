package com.dianmei.baselibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * 基类
 * @author 李双祥 on 2022/4/18.
 */
abstract  class BaseActivity<T : ViewBinding> :AppCompatActivity(){

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=initBinding()
        setContentView(binding.root)
        afterInstanceView()
    }

    /**
     * 子类绑定ViewBinding，kotlin-android-extensions已废弃
     */
    abstract fun initBinding(): T

    /**
     * 实例化控件之后的操作
     */
    protected abstract fun afterInstanceView()
}