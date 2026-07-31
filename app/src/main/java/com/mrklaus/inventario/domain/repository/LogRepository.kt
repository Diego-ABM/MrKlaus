package com.mrklaus.inventario.domain.repository

import com.mrklaus.inventario.data.local.entity.LogEntryEntity
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    suspend fun log(nivel: String, tag: String, mensaje: String, excepcion: Throwable? = null)
    fun getLogs(): Flow<List<LogEntryEntity>>
    suspend fun limpiarLogs()
}
