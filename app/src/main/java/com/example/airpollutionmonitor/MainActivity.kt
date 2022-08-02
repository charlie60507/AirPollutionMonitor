package com.example.airpollutionmonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airpollutionmonitor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var viewModel = PollutedViewModel(DataRepository)
    private val highPollutedAdapter = HighPollutedAdapter()
    private val lowPollutedAdapter = LowPollutedAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.highPollutedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = highPollutedAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        binding.lowPollutedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = lowPollutedAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getPollutedInfo()
        viewModel.highInfo.observe(this) {
            highPollutedAdapter.data = it
            highPollutedAdapter.notifyDataSetChanged()
        }
        viewModel.lowInfo.observe(this) {
            lowPollutedAdapter.data = it
            lowPollutedAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

}