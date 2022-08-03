package com.example.airpollutionmonitor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airpollutionmonitor.data.Record
import kotlinx.coroutines.launch

class PollutedViewModel(private val dataRepository: DataRepository) : ViewModel() {
    companion object {
        private const val TAG = "PollutedViewModel"
    }

    val highInfo: LiveData<MutableList<Record>>
        get() = _highInfo
    val lowInfo: LiveData<MutableList<Record>>
        get() = _lowInfo

    private var _highInfo = MutableLiveData<MutableList<Record>>()
    private var _lowInfo = MutableLiveData<MutableList<Record>>()

    fun getPollutedInfo() {
        viewModelScope.launch {
            val info = dataRepository.getPollutedInfo()
            _highInfo.value = info.first
            _lowInfo.value = info.second
            Log.d(TAG, "${_highInfo.value}")
        }
    }
}
