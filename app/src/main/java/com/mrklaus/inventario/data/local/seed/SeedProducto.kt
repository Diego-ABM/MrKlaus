package com.mrklaus.inventario.data.local.seed

import kotlinx.serialization.Serializable

@Serializable
data class SeedProducto(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidadStock: Int,
    val stockMinimo: Int,
    val mascota: String,
    val categoria: String,
    val variante: String? = null,
    val nota: String? = null
)
