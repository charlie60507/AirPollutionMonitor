package com.example.airpollutionmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airpollutionmonitor.App
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.repo.DataRepository
import com.example.airpollutionmonitor.utils.NetworkUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PollutedViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val highInfo: StateFlow<MutableList<Record>>
        get() = _highInfo.asStateFlow()
    val lowInfo: StateFlow<MutableList<Record>>
        get() = _lowInfo.asStateFlow()
    val listState: StateFlow<ListState>
        get() = _listState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, error ->
        when (error) {
            is SocketTimeoutException -> _listState.value = ListState.Timeout
        }
    }
    private var _highInfo = MutableStateFlow(mutableListOf<Record>())
    private var _lowInfo = MutableStateFlow(mutableListOf<Record>())
    private var _listState = MutableStateFlow<ListState>(ListState.Refreshing)

    init {
        getPollutedInfo()
    }

    fun getPollutedInfo() {
        viewModelScope.launch(exceptionHandler) {
            _listState.value = ListState.Refreshing
            if (NetworkUtil.isConnect(App.appContext)) {
                val info = dataRepository.getPollutedInfo()
                _highInfo.value = info.first
                _lowInfo.value = info.second
                _listState.value = ListState.ShowAll
            } else {
                _listState.value = ListState.NoNetwork
            }
        }
    }

    fun handleFilter(itemCount: Int, keyword: String) {
        viewModelScope.launch(exceptionHandler) {
            _listState.value =
                if (keyword.isNotEmpty()) {
                    if (itemCount == 0) {
                        ListState.NotFound(keyword)
                    } else {
                        ListState.Found
                    }
                } else {
                    ListState.Hide
                }
        }
    }
}
