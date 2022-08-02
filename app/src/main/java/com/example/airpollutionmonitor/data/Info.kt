package com.example.airpollutionmonitor.data


import com.google.gson.annotations.SerializedName

data class Info(
    @SerializedName("label")
    val label: String
)