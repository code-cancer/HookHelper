package com.code.cancer.hook.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewAdapter<V : View> : RecyclerView.Adapter<RecyclerViewAdapter.Holder<V>>() {

    class Holder<V : View>(val view: V) : RecyclerView.ViewHolder(view)

}