package com.mrklaus.inventario.data.local.dao

import androidx.room.*
import com.mrklaus.inventario.data.local.entity.LogEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert
    suspend fun insertarLog(log: LogEntryEntity)

    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LogEntryEntity>>

    @Query("DELETE FROM logs WHERE timestamp < :limite")
    suspend fun limpiarLogsAntiguos(limite: Long)
}
