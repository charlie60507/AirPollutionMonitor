package com.example.airpollutionmonitor

import android.util.Log
import com.example.airpollutionmonitor.data.Record
import com.example.airpollutionmonitor.service.AirPollutedNetwork

private const val PM25_THRESHOLD = 8

object DataRepository {
    val apiService = AirPollutedNetwork.apiService
    suspend fun getPollutedInfo(): Pair<List<Record>, List<Record>> {
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
