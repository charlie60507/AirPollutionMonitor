package com.example.airpollutionmonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.databinding.HighPollutionListItemBinding

class HighPollutedAdapter : RecyclerView.Adapter<HighPollutedAdapter.ViewHolder>() {
    var data = listOf<Record>()

    class ViewHolder(private val binding: HighPollutionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int, record: Record) {
            val isStateFine =
                record.status == binding.root.context.resources.getText(R.string.good_status)

            binding.numberTextView.text = (pos + 1).toString()
            binding.locationTextView.text = record.county
            binding.siteTextView.text = record.sitename
            binding.pm25ValueTextView.text = record.pm25
            binding.statusTextView.text =
                if (isStateFine)
                    binding.root.context.resources.getText(R.string.good_status_hint)
                else
                    record.status

            // TODO: Fix when btn is shown, but other views still be aligned end
            binding.imgBtn.visibility = if (isStateFine) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HighPollutionListItemBinding.inflate(
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
