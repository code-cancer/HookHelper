package com.code.cancer.hook.common

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class RecyclerBindingAdapter<VB : ViewBinding> : RecyclerView.Adapter<RecyclerBindingAdapter.Holder<VB>>() {

    class Holder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

}

