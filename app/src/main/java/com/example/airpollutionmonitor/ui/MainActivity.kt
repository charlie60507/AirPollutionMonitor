package com.example.airpollutionmonitor.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airpollutionmonitor.R
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.databinding.ActivityMainBinding
import com.example.airpollutionmonitor.repo.DataRepository
import com.example.airpollutionmonitor.viewmodel.ListState
import com.example.airpollutionmonitor.viewmodel.PollutedViewModel
import com.example.airpollutionmonitor.viewmodel.ViewModelFactory

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pollutedViewModel: PollutedViewModel
    private var searchItem: MenuItem? = null
    private val highPollutedAdapter = HighPollutedAdapter()
    private val lowPollutedAdapter = LowPollutedAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        pollutedViewModel =
            ViewModelProvider(this, ViewModelFactory(DataRepository))[PollutedViewModel::class.java]
        setContentView(binding.root)
        initViews()
        initObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.items, menu)
        initOptionsMenu(menu)
        return true
    }

    private fun initViews() {
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

    private fun initObservers() {
        binding.recyclerViewContainer.setOnRefreshListener {
            pollutedViewModel.getPollutedInfo()
        }
        pollutedViewModel.highInfo.observe(this) {
            val listFilter = mutableListOf<Record>()
            listFilter.addAll(it)
            highPollutedAdapter.fullData = it
            highPollutedAdapter.filterData = listFilter
            highPollutedAdapter.notifyDataSetChanged()
        }
        pollutedViewModel.lowInfo.observe(this) {
            lowPollutedAdapter.data = it
            lowPollutedAdapter.notifyDataSetChanged()
        }
        pollutedViewModel.listState.observe(this) {
            handleViewState(it)
        }
    }

    private fun initOptionsMenu(menu: Menu?) {
        searchItem = menu?.findItem(R.id.action_search)
        var isSearchOpened = false
        searchItem?.apply {
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    handleViewState(ListState.Hide)
                    isSearchOpened = true
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    handleViewState(ListState.ShowAll)
                    isSearchOpened = false
                    return true
                }
            })
        }
        (searchItem?.actionView as SearchView).apply {
            queryHint = baseContext.getString(R.string.search_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    pollutedViewModel.handleFilter(highPollutedAdapter, isSearchOpened, query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    pollutedViewModel.handleFilter(highPollutedAdapter, isSearchOpened, newText)
                    return true
                }
            })
        }
    }

    private fun handleViewState(state: ListState) {
        Log.d(TAG, "handleViewState=${state}")
        when (state) {
            is ListState.ShowAll -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.VISIBLE
                binding.lowPollutedRecyclerView.visibility = View.VISIBLE
                binding.emptyPageTextView.visibility = View.GONE
                searchItem?.isVisible = true
            }
            is ListState.Found -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.VISIBLE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.GONE
                searchItem?.isVisible = true
            }
            is ListState.Hide -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.empty_result_text))
                searchItem?.isVisible = true
            }
            is ListState.NotFound -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(
                        resources.getString(R.string.not_found_result_text),
                        state.keyword
                    )
            }
            is ListState.Refreshing -> {
                binding.recyclerViewContainer.isRefreshing = true
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.GONE
                searchItem?.isVisible = false
            }
            is ListState.Timeout -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.timeout_result_text))
                searchItem?.isVisible = false
            }
            is ListState.NoNetwork -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.no_internet_text))
                searchItem?.isVisible = false
            }
        }
    }

}