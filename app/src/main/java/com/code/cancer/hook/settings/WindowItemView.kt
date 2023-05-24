package com.code.cancer.hook.settings

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.code.cancer.hook.data.HookInfo
import com.code.cancer.hook.databinding.ItemHookInfoBinding

class WindowItemView(context: Context) : LinearLayout(context) {

    class Data(
        val info: HookInfo,
        var count: Int
    )

    private val binding = ItemHookInfoBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    fun updateCount(count: Int) {
        binding.tvCount.text = "${count}次"
    }

    fun bind(data: Data) {
        binding.tvCount.text = "${data.count}次"
        binding.tvDesc.text = data.info.desc
    }

}