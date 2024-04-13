package com.example.airpollutionmonitor.repo

import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.service.AirPollutedNetwork
import com.example.airpollutionmonitor.ui.HighPollutedAdapter
import com.example.airpollutionmonitor.viewmodel.ListState

private const val PM25_THRESHOLD = 10

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
}
