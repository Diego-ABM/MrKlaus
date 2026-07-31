package com.mrklaus.inventario.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos_clientes")
data class PedidoClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreCliente: String,
    val contacto: String?,
    val fechaPedido: Long = System.currentTimeMillis(),
    val fechaEntregaEstimada: Long?,
    val entregado: Boolean = false,
    val pagado: Boolean = false,
    val stockDescontado: Boolean = false,
    val nota: String? = null,
    val total: Double = 0.0
)
