package com.example.airpollutionmonitor.data


import com.google.gson.annotations.SerializedName

data class PollutedInfo(
    @SerializedName("__extras")
    val extras: Extras,
    @SerializedName("fields")
    val fields: List<Field>,
    @SerializedName("include_total")
    val includeTotal: Boolean,
    @SerializedName("limit")
    val limit: String,
    @SerializedName("_links")
    val links: Links,
    @SerializedName("offset")
    val offset: String,
    @SerializedName("records")
    val records: List<Record>,
    @SerializedName("resource_format")
    val resourceFormat: String,
    @SerializedName("resource_id")
    val resourceId: String,
    @SerializedName("total")
    val total: String
)