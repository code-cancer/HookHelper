package com.code.cancer.hook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.code.cancer.hook.HookHelper.Companion.BROADCAST_METHOD_HOOKED
import com.code.cancer.hook.common.Event
import com.code.cancer.hook.common.EventBus
import com.code.cancer.hook.data.HookInfo
import com.code.cancer.hook.databinding.ActivityHomeBinding
import com.code.cancer.hook.log.LogFragment
import com.code.cancer.hook.settings.SettingsFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var receiver: HookReceiver
    private val logFragment = LogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun register() {
        receiver = HookReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BROADCAST_METHOD_HOOKED)
        registerReceiver(receiver, intentFilter)
    }

    private fun unRegister() {
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        unRegister()
        super.onDestroy()
    }

    private fun initView() = binding.run {
        viewPager.adapter = PageAdapter()
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNav.menu.getItem(position).isChecked = true
            }
        })
        bottomNav.setOnItemSelectedListener {
            viewPager.currentItem = it.order
            true
        }
        register()
    }


    private inner class PageAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> logFragment
                else -> SettingsFragment()
            }
        }
    }

    private inner class HookReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action == BROADCAST_METHOD_HOOKED) {
                val strings = intent.getStringArrayExtra(HookHelper.BUNDLE_KEY_TRACE_DATA) ?: emptyArray()
                if (strings.size == 6) {
                    EventBus.post(Event.OnHooked(HookInfo(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5])))
                }
            }
        }
    }

    //创建菜单栏
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName.equals("MenuBuilder")) {
            try {
                val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
                method.isAccessible = true;
                method.invoke(menu, true);
            } catch (e: java.lang.Exception) {
                e.printStackTrace();
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clean -> EventBus.post(Event.Clean)
        }
        return super.onOptionsItemSelected(item)
    }

}