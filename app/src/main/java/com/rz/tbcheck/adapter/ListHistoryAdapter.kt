package com.rz.tbcheck.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rz.tbcheck.data.ApiResponse
import com.rz.tbcheck.databinding.ItemHistoryBinding

class ListHistoryAdapter(private val listHistory: ArrayList<ApiResponse.ListHistoryItem>) :
    RecyclerView.Adapter<ListHistoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ApiResponse.ListHistoryItem)
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
            (history.status + "(" + history.accuracy + ")").also { tvStatus.text = it }
            tvDate.text = history.date

            Glide.with(holder.itemView.context)
                .load(history.photoUrl)
                .override(100)
                .into(ivImg)

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(listHistory[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int = listHistory.size

}