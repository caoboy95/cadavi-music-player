package com.caocao.cadavimusicplayer.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EmptyRecyclerView : RecyclerView {

    private var emptyView: View? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private fun checkIfEmpty() {
        emptyView?.let { emptyView ->
            adapter?.run {
                val isHavingItem = itemCount > 0
                emptyView.visibility = if (isHavingItem) GONE else VISIBLE
                visibility = if (isHavingItem) VISIBLE else GONE
            }
        }
    }

    fun stopObserver() {
        adapter?.unregisterAdapterDataObserver(observer)
    }

    override fun setAdapter(adapter: Adapter<ViewHolder>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        checkIfEmpty()
    }

    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
        checkIfEmpty()
    }
}