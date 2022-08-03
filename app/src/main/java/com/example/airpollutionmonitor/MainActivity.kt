package com.example.airpollutionmonitor

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

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
            val listFilter = mutableListOf<Record>()
            listFilter.addAll(it)
            highPollutedAdapter.data = it
            highPollutedAdapter.dataFilter = listFilter
            highPollutedAdapter.notifyDataSetChanged()
        }
        viewModel.lowInfo.observe(this) {
            lowPollutedAdapter.data = it
            lowPollutedAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.items, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        var isSearchOpened = false
        searchItem?.apply {
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    Log.d(TAG, "onMenuItemActionExpand")
                    showEmptyPage(true)
                    isSearchOpened = true
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    Log.d(TAG, "onMenuItemActionCollapse")
                    showEmptyPage(false)
                    isSearchOpened = false
                    return true
                }
            })
        }
        (searchItem?.actionView as SearchView).apply {
            queryHint = baseContext.getString(R.string.search_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return handleFilter(isSearchOpened, query)
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return handleFilter(isSearchOpened, newText)
                }
            })
        }
        return true
    }

    private fun handleFilter(opened: Boolean, s: String?): Boolean {
        highPollutedAdapter.filter.filter(s)
        if (!opened || (s != null && s.isNotEmpty())) {
            showEmptyPage(false)
        } else {
            showEmptyPage(true)
        }
        return true
    }

    private fun showEmptyPage(show: Boolean) {
        if (show) {
            binding.lowPollutedRecyclerView.visibility = View.GONE
            binding.highPollutedRecyclerView.visibility = View.GONE
            binding.emptyPage.visibility = View.VISIBLE
        } else {
            binding.lowPollutedRecyclerView.visibility = View.VISIBLE
            binding.highPollutedRecyclerView.visibility = View.VISIBLE
            binding.emptyPage.visibility = View.GONE
        }
    }

}