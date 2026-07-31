package com.mrklaus.inventario.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedido_cliente_items",
    foreignKeys = [
        ForeignKey(
            entity = PedidoClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pedidoId")]
)
data class PedidoClienteItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pedidoId: Long,
    val productoId: Long?, // Opcional si es un producto personalizado
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double
)
