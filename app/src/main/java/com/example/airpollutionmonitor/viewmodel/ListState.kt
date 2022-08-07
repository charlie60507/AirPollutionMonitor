package com.example.airpollutionmonitor.viewmodel

sealed class ListState {
    object ShowAll : ListState()
    object Hide : ListState()
    object Found : ListState()
    class NotFound(var keyword: String?) : ListState()
    object Refreshing : ListState()
    object Timeout : ListState()
    object NoNetwork : ListState()
}