package com.yanxing.networklibrarykt.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.yanxing.networklibrarykt.R

/**
 * @author 李双祥 on 2020/9/10.
 */
class LoadDialog :DialogFragment(){

    companion object{
        val TAG = "com.yanxing.networklibrarykt.dialog.LoadDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_style)
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.load_dialog,container)
        arguments?.apply {
            val tip=getString("tip")
            view.findViewById<TextView>(R.id.progress_text).text=tip
        }
        dialog?.setCanceledOnTouchOutside(false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        val layoutParams = window?.attributes
        layoutParams?.dimAmount = 0.47f
        window?.attributes = layoutParams
    }

    override fun show(transaction: FragmentTransaction,tag: String?): Int {
        transaction.add(this, tag)
        return transaction.commitAllowingStateLoss()
    }
}