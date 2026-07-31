package com.mrklaus.inventario.data.repository

import com.mrklaus.inventario.data.local.dao.LogDao
import com.mrklaus.inventario.data.local.entity.LogEntryEntity
import com.mrklaus.inventario.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val logDao: LogDao
) : LogRepository {
    override suspend fun log(nivel: String, tag: String, mensaje: String, excepcion: Throwable?) {
        logDao.insertarLog(
            LogEntryEntity(
                nivel = nivel,
                tag = tag,
                mensaje = mensaje,
                excepcion = excepcion?.stackTraceToString()
            )
        )
    }

    override fun getLogs(): Flow<List<LogEntryEntity>> = logDao.getAllLogs()

    override suspend fun limpiarLogs() {
        val tresMesesAtras = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 90)
        logDao.limpiarLogsAntiguos(tresMesesAtras)
    }
}
