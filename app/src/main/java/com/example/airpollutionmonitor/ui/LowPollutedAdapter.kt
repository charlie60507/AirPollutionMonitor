package com.example.airpollutionmonitor.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.databinding.LowPollutionListItemBinding


class LowPollutedAdapter : RecyclerView.Adapter<LowPollutedAdapter.ViewHolder>() {
    var data = listOf<Record>()

    class ViewHolder(private val binding: LowPollutionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int, record: Record) {
            binding.numberTextView.text = (pos + 1).toString()
            binding.siteTextView.text = record.sitename
            binding.countyTextView.text = record.county
            binding.pm25ValueTextView.text = record.pm25
            binding.statusTextView.text =record.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LowPollutionListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, data[position])
    }

    override fun getItemCount(): Int = data.size

}
