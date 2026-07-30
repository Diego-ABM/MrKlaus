package com.mrklaus.inventario.domain.model

data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidadStock: Int,
    val stockMinimo: Int,
    val mascota: Mascota,
    val categoria: Categoria,
    val variante: String?,
    val nota: String?,
    val isFavorite: Boolean,
    val fechaVencimiento: Long?,
    val pesoKg: Double?,
    val tipoArena: String?,
    val fotos: List<String>,
    val fechaActualizacion: Long
)
