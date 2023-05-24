package com.code.cancer.hook

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import kotlinx.coroutines.MainScope

class HookApplication : Application() {

    companion object {

        val appScope = MainScope()

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: HookApplication
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}