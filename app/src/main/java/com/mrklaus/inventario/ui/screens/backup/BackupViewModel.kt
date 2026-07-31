package com.mrklaus.inventario.ui.screens.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.data.backup.DatabaseBackupManager
import com.mrklaus.inventario.util.LogManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupManager: DatabaseBackupManager,
    private val logManager: LogManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<BackupEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            val tempFile = File(context.cacheDir, "backup_temp.db")
            val result = backupManager.exportDatabase(tempFile)
            
            if (result.isSuccess) {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        tempFile.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                    _uiState.value = BackupUiState.Success("Base de datos exportada con éxito")
                    logManager.info("Backup", "Base de datos exportada a: $uri")
                } catch (e: Exception) {
                    _uiState.value = BackupUiState.Error("Error al escribir el archivo: ${e.message}")
                    logManager.error("Backup", "Error al exportar", e)
                }
            } else {
                val error = result.exceptionOrNull()
                _uiState.value = BackupUiState.Error("Error al exportar: ${error?.message}")
                logManager.error("Backup", "Error al exportar base de datos", error)
            }
            tempFile.delete()
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading
            val tempFile = File(context.cacheDir, "import_temp.db")
            
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                val result = backupManager.importDatabase(tempFile)
                if (result.isSuccess) {
                    logManager.info("Backup", "Base de datos importada exitosamente desde: $uri")
                    _uiState.value = BackupUiState.Success("Datos restaurados. La aplicación se reiniciará.")
                    _eventFlow.emit(BackupEvent.RestartApp)
                } else {
                    val error = result.exceptionOrNull()
                    _uiState.value = BackupUiState.Error("Error al importar: ${error?.message}")
                    logManager.error("Backup", "Error al importar base de datos", error)
                }
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error("Error al leer el archivo: ${e.message}")
                logManager.error("Backup", "Error crítico durante importación", e)
            } finally {
                tempFile.delete()
            }
        }
    }

    fun clearState() {
        _uiState.value = BackupUiState.Idle
    }
}

sealed class BackupUiState {
    object Idle : BackupUiState()
    object Loading : BackupUiState()
    data class Success(val message: String) : BackupUiState()
    data class Error(val message: String) : BackupUiState()
}

sealed class BackupEvent {
    object RestartApp : BackupEvent()
}
