package com.mrklaus.inventario.domain.model

data class Venta(
    val id: Long = 0,
    val fecha: Long = System.currentTimeMillis(),
    val total: Double,
    val items: List<VentaItem>
)

data class VentaItem(
    val id: Long = 0,
    val ventaId: Long = 0,
    val productoId: Long,
    val nombreProducto: String,
    val cantidad: Int,
    val precioVenta: Double
)

data class ProductoRotacion(
    val nombre: String,
    val totalVendido: Int
)
