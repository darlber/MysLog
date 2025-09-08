package com.example.myslog.ui.home

import com.example.myslog.ui.SessionWrapper
import com.example.myslog.utils.Event


sealed class HomeEvent : Event {
    data class SessionClicked(val sessionWrapper: SessionWrapper) : HomeEvent()
    data class DeleteSessionRequested(val sessionWrapper: SessionWrapper) : HomeEvent()
    data class ConfirmDeleteSession(val sessionId: Long) : HomeEvent()


    object NewSession : HomeEvent()
    object OpenSettings : HomeEvent()
    object CheckUpdates : HomeEvent()
}