package com.rz.tbcheck.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rz.tbcheck.data.ListHistoryItem
import com.rz.tbcheck.databinding.ItemHistoryBinding

class ListHistoryAdapter(private val listHistory: ArrayList<ListHistoryItem>) :
    RecyclerView.Adapter<ListHistoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ListHistoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val history = listHistory[position]

        holder.binding.apply {

            val percent = if (history.accuracy?.substring(3) == ".")
                history.accuracy.substring(0, 2)
            else
                history.accuracy!!.substring(0, 3)

            (history.status + " (" + percent + "%)").also { tvStatus.text = it }

            tvDate.text = history.createdAt!!.substring(0, 10)

            Glide.with(holder.itemView.context)
                .load(history.image)
                .override(100)
                .into(ivImg)

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(listHistory[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int = listHistory.size

}