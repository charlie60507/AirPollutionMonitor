package com.example.airpollutionmonitor.service

import com.example.airpollutionmonitor.data.PollutedInfo
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

private const val URL =
    "https://data.epa.gov.tw"
private const val URL_PATH =
    "/api/v2/aqx_p_432?limit=1000&api_key=cebebe84-e17d-4022-a28f-81097fda5896&sort=ImportDate%20desc&format=json"
private const val OKHTTP_TIMEOUT = 30L

interface AirPollutedService {
    @GET(URL_PATH)
    suspend fun getPollutedInfo(): PollutedInfo
}

object AirPollutedNetwork {
    private val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
        .build()
    val apiService: AirPollutedService =
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient).build()
            .create(AirPollutedService::class.java)
}