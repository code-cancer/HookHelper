package com.code.cancer.hook.common

import android.widget.Toast
import androidx.annotation.StringRes
import com.code.cancer.hook.HookApplication

object Toasts {

    fun show(str: String) {
        Toast.makeText(HookApplication.context, str, Toast.LENGTH_SHORT).show()
    }

    fun show(@StringRes strId: Int) {
        val context = HookApplication.context
        val string = context.getString(strId)
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

}