package com.example.airpollutionmonitor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airpollutionmonitor.App
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.repo.DataRepository
import com.example.airpollutionmonitor.utils.NetworkUtil
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
