package com.mrklaus.inventario.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class LogEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val nivel: String, // ERROR, INFO, WARN
    val tag: String,
    val mensaje: String,
    val excepcion: String? = null
)
