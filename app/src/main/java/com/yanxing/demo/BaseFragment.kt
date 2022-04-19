package com.dianmei.baselibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * 基类
 * @author 李双祥 on 2022/4/18.
 */
abstract class BaseFragment<T : ViewBinding>(var layoutId: Int) : Fragment() {

    protected var binding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutId, container, false)
    }

    /**
     * 子类绑定ViewBinding，kotlin-android-extensions已废弃
     */
    abstract fun initBinding(view: View): T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = initBinding(view)
        afterInstanceView()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * 实例化控件之后的操作
     */
    protected abstract fun afterInstanceView()

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}