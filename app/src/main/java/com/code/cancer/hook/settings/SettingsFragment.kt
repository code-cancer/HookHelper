package com.code.cancer.hook.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import androidx.core.content.ContextCompat.getSystemService
import com.code.cancer.hook.HookApplication
import com.code.cancer.hook.common.BaseFragment
import com.code.cancer.hook.common.Event
import com.code.cancer.hook.common.EventBus
import com.code.cancer.hook.common.Toasts
import com.code.cancer.hook.databinding.FragmentSettingsBinding


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private var floatView: FloatWindowView? = null

    override fun initViews(binding: FragmentSettingsBinding) {
        binding.tvShowFloatWindow.setOnClickListener {
            val canDrawOverlays = Settings.canDrawOverlays(context)
            if (canDrawOverlays) {
                if (floatView?.parent == null) {
                    showWindow()
                } else {
                    hideWindow()
                }
            } else {
                Toasts.show("请授予悬浮窗权限～")
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:${HookApplication.context.packageName}")
                startActivity(intent)
            }
        }
        EventBus.observe<Event.OnHooked>(this) {
            floatView?.addInfo(it.hookInfo)
        }
        EventBus.observe<Event.Clean>(this) {
            floatView?.clear()
        }
    }

    private fun hideWindow() {
        val windowManager = getSystemService(HookApplication.context, WindowManager::class.java)
        windowManager?.removeView(floatView)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showWindow() {
        val windowManager = getSystemService(HookApplication.context, WindowManager::class.java)
        val layoutParams = WindowManager.LayoutParams().apply {
            width = WRAP_CONTENT
            height = WRAP_CONTENT
            flags = FLAG_NOT_TOUCH_MODAL or FLAG_NOT_FOCUSABLE
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) TYPE_APPLICATION_OVERLAY else TYPE_PHONE
            format = PixelFormat.RGBA_8888
        }
        if(context != null && windowManager != null) {
            if(floatView == null) {
                val floatWindowView = FloatWindowView(requireContext(), windowManager, layoutParams)
                floatView = floatWindowView
                windowManager.addView(floatView, layoutParams)
            } else if(floatView?.parent == null) {
                val params = floatView?.layoutParams
                windowManager.addView(floatView, params)
            }
        }

    }

}