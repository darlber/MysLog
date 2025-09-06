package com.example.exerlog.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.exerlog.ui.home.HomeEvent
import com.example.exerlog.utils.Event

@Composable
fun HomeBottomBar(
    onEvent: (Event) -> Unit,
) {
    BottomAppBar(
        modifier = Modifier,
        containerColor = Color.Transparent,
        actions = {
            Row {
                FloatingActionButton(
                    onClick = { onEvent(HomeEvent.CheckUpdates) },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Update")
                }
                Spacer(modifier = Modifier.width(12.dp))
                FloatingActionButton(
                    onClick = { onEvent(HomeEvent.OpenSettings) },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(HomeEvent.NewSession) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        }
    )
}

@Preview
@Composable
fun HomeBottomBarPreview() {
    HomeBottomBar(
        onEvent = { }
    )
}
