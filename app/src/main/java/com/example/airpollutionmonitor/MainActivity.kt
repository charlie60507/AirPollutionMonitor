package com.example.airpollutionmonitor

import android.os.Bundle
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
            viewModel.getPollutedInfo()
        }
        viewModel.highInfo.observe(this) {
            val listFilter = mutableListOf<Record>()
            listFilter.addAll(it)
            highPollutedAdapter.fullData = it
            highPollutedAdapter.filterData = listFilter
            highPollutedAdapter.notifyDataSetChanged()
        }
        viewModel.lowInfo.observe(this) {
            lowPollutedAdapter.data = it
            lowPollutedAdapter.notifyDataSetChanged()
        }
        viewModel.listState.observe(this) {
            handleViewState(it)
        }
    }

    private fun initOptionsMenu(menu: Menu?) {
        val searchItem = menu?.findItem(R.id.action_search)
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
                    viewModel.handleFilter(highPollutedAdapter, isSearchOpened, query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.handleFilter(highPollutedAdapter, isSearchOpened, newText)
                    return true
                }
            })
        }
    }

    private fun handleViewState(state: ListState) {
        when (state) {
            is ListState.ShowAll -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.VISIBLE
                binding.lowPollutedRecyclerView.visibility = View.VISIBLE
                binding.emptyPageTextView.visibility = View.GONE
            }
            is ListState.Found -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.VISIBLE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.GONE
            }
            is ListState.Hide -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.empty_result_text))
            }
            is ListState.NotFound -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.not_found_result_text), state.keyword)
            }
            is ListState.Refreshing -> {
                binding.recyclerViewContainer.isRefreshing = true
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.GONE
            }
            is ListState.Timeout -> {
                binding.recyclerViewContainer.isRefreshing = false
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.timeout_result_text))
            }
        }
    }

}