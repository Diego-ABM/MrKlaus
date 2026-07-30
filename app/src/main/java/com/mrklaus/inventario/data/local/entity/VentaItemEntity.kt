package com.mrklaus.inventario.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "venta_items",
    foreignKeys = [
        ForeignKey(
            entity = VentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["ventaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ventaId")]
)
data class VentaItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ventaId: Long,
    val productoId: Long,
    val nombreProducto: String, // Copia al momento de venta
    val cantidad: Int,
    val precioVenta: Double // Precio al momento de venta
)
