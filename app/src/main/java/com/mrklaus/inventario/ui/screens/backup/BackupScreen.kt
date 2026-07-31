package com.mrklaus.inventario.ui.screens.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogs: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let { viewModel.exportBackup(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importBackup(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is BackupEvent.RestartApp -> {
                    // Handled in uiState observer to show snackbar first
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Respaldo y Seguridad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Text(
                "Gestión de Base de Datos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "Exporta tus datos para tener una copia física o impórtalos para restaurar tu inventario.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(Spacing.medium))

            Button(
                onClick = { exportLauncher.launch("mrklaus_backup_${System.currentTimeMillis()}.db") },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(Spacing.medium)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(Modifier.width(Spacing.small))
                Text("Exportar Base de Datos")
            }

            OutlinedButton(
                onClick = { importLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(Spacing.medium)
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null)
                Spacer(Modifier.width(Spacing.small))
                Text("Importar Base de Datos")
            }

            HorizontalDivider(Modifier.padding(vertical = Spacing.medium))

            Text(
                "Respaldo en la Nube",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { /* Placeholder para Google Login */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)), // Google Blue
                contentPadding = PaddingValues(Spacing.medium)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(Modifier.width(Spacing.small))
                Text("Iniciar Sesión con Google (Próximamente)")
            }

            TextButton(
                onClick = onNavigateToLogs,
                modifier = Modifier.padding(top = Spacing.medium)
            ) {
                Text("Ver Registros del Sistema (Logs)")
            }

            if (uiState is BackupUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = Spacing.medium))
            }
        }
    }

    // Handle results
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is BackupUiState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                if (state.message.contains("restaurados")) {
                    // Solo si es importación exitosa, reiniciamos
                    kotlinx.coroutines.delay(2000)
                    exitProcess(0)
                }
                viewModel.clearState()
            }
            is BackupUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearState()
            }
            else -> {}
        }
    }
}
