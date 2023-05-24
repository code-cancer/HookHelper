package com.code.cancer.hook.log

import android.annotation.SuppressLint
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.code.cancer.hook.common.BaseFragment
import com.code.cancer.hook.common.Event
import com.code.cancer.hook.common.EventBus
import com.code.cancer.hook.common.RecyclerBindingAdapter
import com.code.cancer.hook.data.HookInfo
import com.code.cancer.hook.databinding.FragmentLogBinding
import com.code.cancer.hook.databinding.ItemMethodTraceBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LogFragment : BaseFragment<FragmentLogBinding>() {

    private val showInfo = ArrayList<ItemData>()

    @SuppressLint("NotifyDataSetChanged")
    override fun initViews(binding: FragmentLogBinding): Unit = binding.run {
        tracesList.adapter = traceAdapter
        EventBus.observe<Event.OnHooked>(viewLifecycleOwner) {
            val timeMillis = System.currentTimeMillis()
            val date = Date(timeMillis)
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
            val format = dateFormat.format(date)
            showInfo.add(ItemData(format, it.hookInfo))
            tracesList.adapter?.notifyItemInserted(showInfo.size - 1)
        }
        EventBus.observe<Event.Clean>(viewLifecycleOwner) {
            showInfo.clear()
            tracesList.adapter?.notifyDataSetChanged()
        }
    }

    private val traceAdapter = object : RecyclerBindingAdapter<ItemMethodTraceBinding>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<ItemMethodTraceBinding> {
            val view = ItemMethodTraceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view.tvTrace.movementMethod = ScrollingMovementMethod.getInstance()
            view.tvTrace.setHorizontallyScrolling(true)
            view.tvTrace.isFocusable = true
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder<ItemMethodTraceBinding>, position: Int) {
            val itemData = showInfo[position]
            holder.binding.run {
                tvTime.text = itemData.time
                tvMethod.text = itemData.info.desc
                tvPackageName.text = itemData.info.packageName
                tvTrace.text = itemData.info.trace
                tvTrace.isVisible = itemData.opened
                if(itemData.opened) {
                    ivIcon.rotation = 90f
                } else {
                    ivIcon.rotation = 0f
                }
                containerInfo.setOnClickListener {
                    itemData.opened = !itemData.opened
                    tvTrace.isVisible = itemData.opened
                    if(itemData.opened) {
                        ivIcon.rotation = 90f
                    } else {
                        ivIcon.rotation = 0f
                    }
                }
            }
        }

        override fun getItemCount(): Int = showInfo.size
    }

    private class ItemData(
        val time: String,
        val info: HookInfo,
        var opened: Boolean = false
    )

}