package com.example.airpollutionmonitor

sealed class ListState {
    object ShowAll : ListState()
    object Hide : ListState()
    object Found : ListState()
    class NotFound(var keyword: String?) : ListState()
    object Refreshing : ListState()
}