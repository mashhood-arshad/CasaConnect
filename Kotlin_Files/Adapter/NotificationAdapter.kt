package com.example.casaconnect.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.casaconnect.Domain.NotificationModel
import com.example.casaconnect.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val items: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.VH>() {

    inner class VH(val binding: ItemNotificationBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.messageTxt.text = items[position].message
    }

    override fun getItemCount() = items.size
}