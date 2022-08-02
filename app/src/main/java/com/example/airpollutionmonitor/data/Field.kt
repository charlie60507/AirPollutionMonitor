package com.example.airpollutionmonitor.data


import com.google.gson.annotations.SerializedName

data class Field(
    @SerializedName("id")
    val id: String,
    @SerializedName("info")
    val info: Info,
    @SerializedName("type")
    val type: String
)