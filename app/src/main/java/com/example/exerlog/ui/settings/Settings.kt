package com.example.exerlog.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.exerlog.utils.UiEvent

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val mContext = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                viewModel.onEvent(SettingsEvent.ExportDatabase(mContext, it))
            }
        }
    )
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.onEvent(SettingsEvent.ImportDatabase(mContext, it))
            }
        }
    )

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.FileCreated -> {
                    exportLauncher.launch(event.fileName)
                }
                is UiEvent.Navigate -> navController.navigate(event.route)
                else -> Unit
            }
        }
    }

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Settings")
            FilledTonalButton(onClick = {
                viewModel.onEvent(SettingsEvent.CreateFile)
            }) {
                Text("Export Database")
            }
            FilledTonalButton(onClick = {
                importLauncher.launch("application/json")
            }) {
                Text("Import Database")
            }
            FilledTonalButton(onClick = {
                viewModel.onEvent(SettingsEvent.ClearDatabase)
            }) {
                Text("Delete Database")
            }
        }
    }
}
@Preview
@Composable
fun SettingsDialogPreview() {
    SettingsScreen(navController = NavHostController(LocalContext.current))
}
