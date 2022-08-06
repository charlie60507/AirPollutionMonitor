package com.example.airpollutionmonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airpollutionmonitor.data.Record
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

private const val TAG = "PollutedViewModel"

class PollutedViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val highInfo: LiveData<MutableList<Record>>
        get() = _highInfo
    val lowInfo: LiveData<MutableList<Record>>
        get() = _lowInfo
    val listState: LiveData<ListState>
        get() = _listState

    private val exceptionHandler = CoroutineExceptionHandler { _, error ->
        when (error) {
            is SocketTimeoutException -> _listState.value = ListState.Timeout
        }
    }
    private var _highInfo = MutableLiveData<MutableList<Record>>()
    private var _lowInfo = MutableLiveData<MutableList<Record>>()
    private var _listState = MutableLiveData<ListState>()

    init {
        getPollutedInfo()
    }

    fun getPollutedInfo() {
        viewModelScope.launch(exceptionHandler) {
            _listState.value = ListState.Refreshing
            val info = dataRepository.getPollutedInfo()
            _highInfo.value = info.first
            _lowInfo.value = info.second
            _listState.value = ListState.ShowAll
        }
    }

    fun handleFilter(
        adapter: HighPollutedAdapter,
        opened: Boolean,
        keyword: String
    ) {
        dataRepository.getFilterListState(adapter, opened, keyword) {
            _listState.value = it
        }
    }
}
