package com.example.exerlog.utils

import com.example.exerlog.ui.session.actions.FinishResult
import com.example.exerlog.ui.session.components.StatEntry

sealed class UiEvent {
    data class OpenWebsite(val url: String) : UiEvent()
    data class Navigate(val route: String, val popBackStack: Boolean = false): UiEvent()
    data class FileCreated(val fileName: String) : UiEvent()

    data class ShowImagePopup(val exerciseId: String) : UiEvent()
    data class ShowStatsPopup(val stats: List<StatEntry>) : UiEvent()


    object ToggleTimer: UiEvent()
    object ResetTimer: UiEvent()
    object IncrementTimer: UiEvent()
    object DecrementTimer : UiEvent()
    data class ShowFinishResult(val result: FinishResult) : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent() // 👈 Nuevo evento


}
