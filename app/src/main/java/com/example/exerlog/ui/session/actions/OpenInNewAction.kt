package com.example.exerlog.ui.session.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun OpenInNewAction(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.ZoomIn,
            contentDescription = "Open exercise guide."
        )
    }
}