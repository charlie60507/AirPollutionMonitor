package com.example.airpollutionmonitor.repo

import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.service.AirPollutedNetwork
import com.example.airpollutionmonitor.ui.HighPollutedAdapter
import com.example.airpollutionmonitor.viewmodel.ListState

private const val PM25_THRESHOLD = 8

object DataRepository {
    private val apiService = AirPollutedNetwork.apiService
    suspend fun getPollutedInfo(): Pair<MutableList<Record>, MutableList<Record>> {
        val records = apiService.getPollutedInfo().records
        val lowList = mutableListOf<Record>()
        val highList = mutableListOf<Record>()
        for (record in records) {
            val pm25Int = record.pm25.toIntOrNull()
            if (pm25Int == null) {
                lowList.add(record)
                continue
            }
            if (pm25Int > PM25_THRESHOLD) {
                highList.add(record)
            } else {
                lowList.add(record)
            }
        }
        return Pair(highList, lowList)
    }

    fun getFilterListState(
        adapter: HighPollutedAdapter,
        opened: Boolean,
        keyword: String,
        callback: (ListState) -> Unit
    ) {
        adapter.filter.filter(keyword) {
            callback(
                if (!opened || keyword.isNotEmpty()) {
                    when (adapter.itemCount) {
                        0 -> ListState.NotFound(keyword)
                        adapter.fullData.size -> ListState.ShowAll
                        else -> ListState.Found
                    }
                } else {
                    ListState.Hide
                }
            )
        }
    }
}
