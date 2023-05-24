package com.code.cancer.hook

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import com.code.cancer.hook.data.HookMethod
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * 1.可视为单例
 * 2.该类与本App不在同一进程中，故无法直接更新UI
 */
class HookHelper : IXposedHookLoadPackage {

    companion object {
        const val TAG = "HookHelperLog"
        const val BROADCAST_METHOD_HOOKED = "com.ihandy.hook.HOOKED_BROADCAST"
        const val BUNDLE_KEY_TRACE_DATA = "BUNDLE_KEY_TRACE_DATA"
    }

    /**
     * traceCache： Array<String>
     * [id, 描述，类名，方法名，堆栈, 包名]
     */
    private val traceCache = ConcurrentLinkedQueue<Array<String>>()
    private var context: Context? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        Log.d(TAG, "Hook Start: ${lpparam.packageName}")

        for (method in HookMethod.values()) {
            XposedHelpers.findAndHookMethod(method.className, lpparam.classLoader, method.methodName, *method.params.toTypedArray(), CommonCallback(method, lpparam.packageName))
        }

    }

    inner class CommonCallback(private val method: HookMethod, private val packageName: String) : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            when (method) {
                HookMethod.AttachBaseContext -> {
                    val baseContext = param?.args?.getOrNull(0)
                    if (baseContext is Context) {
                        context = baseContext
                        Log.d(TAG, "Target App ($packageName) Context Got")
                    }
                }
                HookMethod.GetAndroidId -> {
                    val arg1 = param?.args?.getOrNull(1)
                    if (arg1 is String && arg1 == Settings.Secure.ANDROID_ID) {
                        sendMassageOrSaveInCache(method, packageName)
                    }
                }
                HookMethod.GetContacts -> {
                    val arg0 = param?.args?.getOrNull(0)
                    val arg1 = param?.args?.getOrNull(1)
                    if (arg0 is Uri) {
                        val arr = arrayOf("_id", "address", "type", "body", "date")
                        if (arg0 == ContactsContract.Contacts.CONTENT_URI) {
                            method.desc = "获取通讯录"
                            sendMassageOrSaveInCache(method, packageName)
                        } else if (arg0 == CallLog.Calls.CONTENT_URI) {
                            method.desc = "获取通话记录"
                            sendMassageOrSaveInCache(method, packageName)
                        } else if (arg1 != null) {
                            val strArr = arg1 as Array<String>
                            if (strArr.contentEquals(arr)) {
                                method.desc = "获取短信信息"
                                sendMassageOrSaveInCache(method, packageName)
                            }
                        }
                    }
                }
                else -> sendMassageOrSaveInCache(method, packageName)
            }
        }
    }

    private fun sendMassageOrSaveInCache(method: HookMethod, packageName: String) {
        if (context != null) {
            while (traceCache.size > 0) {
                val arr = traceCache.remove()
                val intent = Intent(BROADCAST_METHOD_HOOKED)
                intent.putExtra(BUNDLE_KEY_TRACE_DATA, arr)
                context?.sendBroadcast(intent)
            }
            val intent = Intent(BROADCAST_METHOD_HOOKED)
            val trace = getTrace()
            if (trace.isNotEmpty()) {
                val strArr = arrayOf(method.name, method.desc, method.className, method.methodName, trace, packageName)
                intent.putExtra(BUNDLE_KEY_TRACE_DATA, strArr)
                context?.sendBroadcast(intent)
                log(strArr)
            }
        } else {
            val trace = getTrace()
            if (trace.isNotEmpty()) {
                val strArr = arrayOf(method.name, method.desc, method.className, method.methodName, trace, packageName)
                traceCache.add(strArr)
                log(strArr)
            }
        }
    }

    private fun log(strArr: Array<String>) {
        Log.d(TAG, "${strArr[5]}: ${strArr[1]}\n${strArr[4]}")
    }

    private fun getTrace(): String {
        val stackTrace = Thread.currentThread().stackTrace
        val stringBuilder = StringBuilder()
        val strings = ArrayList<String>()
        for (element in stackTrace) {
            val className = element.className
            if (checkTrace(className)) {
                strings.add(" - ${element.className}.${element.methodName}(${element.fileName}.${element.lineNumber})")
            }
        }
        for (i in 0 until strings.size) {
            val s = strings[i]
            if (i == strings.size - 1) {
                stringBuilder.append(s)
            } else {
                stringBuilder.append(s + "\n")
            }
        }
        return stringBuilder.toString()
    }

    private fun checkTrace(className: String): Boolean {
        val checkList = listOf(
            "com.ihandy.hook",
            "android.os",
            "LSPHooker_",
            "XposedBridge",
            "com.android.internal.os",
            "Thread.getStackTrace",
            "dalvik.system",
            "android.app.ActivityThread",
            "java.lang.Thread"
        )
        for (s in checkList) {
            if (className.contains(s)) return false
        }
        return true
    }


}
