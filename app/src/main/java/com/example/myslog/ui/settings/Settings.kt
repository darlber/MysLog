package com.example.myslog.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myslog.utils.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val mContext = LocalContext.current
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let { viewModel.onEvent(SettingsEvent.ExportDatabase(mContext, it)) }
        }
    )
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.onEvent(SettingsEvent.ImportDatabase(mContext, it)) }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.FileCreated -> exportLauncher.launch(event.fileName)
                is UiEvent.Navigate -> navController.navigate(event.route)
                else -> Unit
            }
        }
    }

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            // 🔲 Nuevo checkbox para mantener la pantalla encendida
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = keepScreenOn,
                    onCheckedChange = { checked ->
                        viewModel.onEvent(SettingsEvent.ToggleKeepScreenOn(checked))
                    }
                )
                Text("Mantener pantalla encendida")
            }

            Spacer(Modifier.height(16.dp))

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
