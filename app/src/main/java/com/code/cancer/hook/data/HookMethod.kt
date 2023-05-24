package com.code.cancer.hook.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

enum class HookMethod(
    var desc: String,
    val className: String,
    val methodName: String,
    val params: List<Class<*>>,
) {
    GetDeviceId(
        "获取DeviceId",
        "android.telephony.TelephonyManager",
        "getDeviceId",
        emptyList()
    ),
    GetIMSI(
        "获取IMSI",
        "android.telephony.TelephonyManager",
        "getSubscriberId",
        emptyList()
    ),
    GetIMEI(
        "获取IMEI",
        "android.telephony.TelephonyManager",
        "getImei",
        emptyList()
    ),
    GetOaid(
        "获取oaid",
        "com.bun.miitmdid.provider.DefaultProvider",
        "getOAID",
        emptyList()
    ),
    GetInstalledPackages(
        "获取应用列表",
        "android.app.ApplicationPackageManager",
        "getInstalledPackagesAsUser",
        listOf(Int::class.java, Int::class.java)
    ),
    GetNetworkInterface(
        "获取MAC地址信息",
        "java.net.NetworkInterface",
        "getHardwareAddress",
        emptyList()
    ),
    GetMacAddress(
        "获取MAC地址",
        "android.net.wifi.WifiInfo",
        "getMacAddress",
        emptyList()
    ),
    GetAndroidId(
        "获取AndroidID",
        "android.provider.Settings.Secure",
        "getString",
        listOf(ContentResolver::class.java, String::class.java)
    ),
    GetContacts(
        "获取通讯录信息/短信/通话记录",
        "android.content.ContentResolver",
        "query",
        listOf(Uri::class.java, Array<String>::class.java, String::class.java, Array<String>::class.java, String::class.java)
    ),
    AttachBaseContext(
        "Application attach.",
        "android.app.Application",
        "attach",
        listOf(Context::class.java)
    )

}
