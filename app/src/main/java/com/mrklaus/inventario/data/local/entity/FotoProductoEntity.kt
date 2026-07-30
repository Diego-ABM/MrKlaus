package com.mrklaus.inventario.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fotos_producto",
    foreignKeys = [
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productoId"])]
)
data class FotoProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productoId: Long,
    val rutaArchivo: String,
    val orden: Int
)
