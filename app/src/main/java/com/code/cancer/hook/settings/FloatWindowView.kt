package com.code.cancer.hook.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.code.cancer.hook.HomeActivity
import com.code.cancer.hook.common.Event
import com.code.cancer.hook.common.EventBus
import com.code.cancer.hook.common.RecyclerViewAdapter
import com.code.cancer.hook.data.HookInfo
import com.code.cancer.hook.databinding.WindowAlterLayoutBinding

@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
class FloatWindowView(context: Context, private val windowManager: WindowManager, private val params: WindowManager.LayoutParams) : ConstraintLayout(context) {

    private val showingList = ArrayList<WindowItemView.Data>()
    private val allInfo = ArrayList<HookInfo>()

    private val binding = WindowAlterLayoutBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.run {
            rvHook.adapter = InfoAdapter()
            icClose.setOnClickListener { windowManager.removeView(this@FloatWindowView) }
            icHome.setOnClickListener {
                context.startActivity(Intent(context, HomeActivity::class.java))
            }
            icClean.setOnClickListener {
                EventBus.post(Event.Clean)
            }
            icMove.setOnTouchListener(object : OnTouchListener{
                private var x = 0
                private var y = 0
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    Log.d("DebugTest", "onTouch: ${event.action}")
                    val rawX = event.rawX.toInt()
                    val rawY = event.rawY.toInt()
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = rawX
                            y = rawY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val movedX = rawX - x
                            val movedY = rawY - y
                            x = rawX
                            y = rawY
                            params.x += movedX
                            params.y += movedY
                            Log.d("DebugTest", "onTouch: ($x, $y), wl: (${params.x}, ${params.y})")
                            windowManager.updateViewLayout(this@FloatWindowView, params)
                        }
                    }
                    return false
                }
            })
        }
    }

    fun addInfo(info: HookInfo) {
        allInfo.add(info)
        var find = false
        for ((index, value) in showingList.withIndex()) {
            if (value.info.id == info.id) {
                find = true
                value.count += 1
                binding.rvHook.adapter?.notifyItemChanged(index, value.count)
                break
            }
        }
        if (!find) {
            showingList.add(WindowItemView.Data(info, 1))
            val updateIndex = showingList.size - 1
            binding.rvHook.adapter?.notifyItemInserted(updateIndex)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        showingList.clear()
        allInfo.clear()
        binding.rvHook.adapter?.notifyDataSetChanged()
    }

    private inner class InfoAdapter : RecyclerViewAdapter<WindowItemView>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<WindowItemView> {
            val windowItemView = WindowItemView(parent.context)
            return Holder(windowItemView)
        }

        override fun onBindViewHolder(holder: Holder<WindowItemView>, position: Int) {
            holder.view.bind(showingList[position])
        }

        override fun onBindViewHolder(holder: Holder<WindowItemView>, position: Int, payloads: MutableList<Any>) {
            val value = payloads.getOrNull(0)
            if (value is Int) {
                holder.view.updateCount(showingList[position].count)
            } else {
                super.onBindViewHolder(holder, position, payloads)
            }
        }

        override fun getItemCount(): Int = showingList.size
    }

}