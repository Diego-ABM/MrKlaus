package com.mrklaus.inventario.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: Long = System.currentTimeMillis(),
    val total: Double
)
