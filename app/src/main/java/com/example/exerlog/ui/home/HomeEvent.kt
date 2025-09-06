package com.example.exerlog.ui.home

import com.example.exerlog.ui.SessionWrapper
import com.example.exerlog.utils.Event


sealed class HomeEvent : Event {
    data class SessionClicked(val sessionWrapper: SessionWrapper) : HomeEvent()
    data class DeleteSessionRequested(val sessionWrapper: SessionWrapper) : HomeEvent()
    data class ConfirmDeleteSession(val sessionId: Long) : HomeEvent()


    object NewSession : HomeEvent()
    object OpenSettings : HomeEvent()
    object CheckUpdates : HomeEvent()
}