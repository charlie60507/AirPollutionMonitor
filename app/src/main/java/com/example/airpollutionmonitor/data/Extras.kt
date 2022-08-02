package com.example.airpollutionmonitor.data


import com.google.gson.annotations.SerializedName

data class Extras(
    @SerializedName("api_key")
    val apiKey: String
)