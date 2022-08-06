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
            highPollutedAdapter.fullData = it
            highPollutedAdapter.filterData = listFilter
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
                    showResult(ListState.Hide)
                    isSearchOpened = true
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    showResult(ListState.ShowAll)
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

    private fun handleFilter(opened: Boolean, keyword: String?): Boolean {
        highPollutedAdapter.filter.filter(keyword) {
            if (!opened || (keyword != null && keyword.isNotEmpty())) {
                when (highPollutedAdapter.itemCount) {
                    0 -> showResult(ListState.NotFound, keyword)
                    highPollutedAdapter.fullData.size -> showResult(ListState.ShowAll)
                    else -> showResult(ListState.Found)
                }
            } else {
                showResult(ListState.Hide, keyword)
            }
        }
        return true
    }

    private fun showResult(state: ListState, keyWord: String? = null) {
        when (state) {
            ListState.ShowAll -> {
                binding.highPollutedRecyclerView.visibility = View.VISIBLE
                binding.lowPollutedRecyclerView.visibility = View.VISIBLE
                binding.emptyPageTextView.visibility = View.GONE
            }
            ListState.Found -> {
                binding.highPollutedRecyclerView.visibility = View.VISIBLE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.GONE
            }
            ListState.Hide -> {
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.empty_result_text))
            }
            ListState.NotFound -> {
                binding.highPollutedRecyclerView.visibility = View.GONE
                binding.lowPollutedRecyclerView.visibility = View.GONE
                binding.emptyPageTextView.visibility = View.VISIBLE
                binding.emptyPageTextView.text =
                    String.format(resources.getString(R.string.not_found_result), keyWord)
            }
        }
    }

    sealed class ListState {
        object ShowAll : ListState()
        object Hide : ListState()
        object Found : ListState()
        object NotFound : ListState()
    }

}