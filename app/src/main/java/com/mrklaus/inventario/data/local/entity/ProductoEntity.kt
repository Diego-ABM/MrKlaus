package com.mrklaus.inventario.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidadStock: Int,
    val stockMinimo: Int,
    val mascota: Mascota,
    val categoria: Categoria,
    val variante: String?,
    val nota: String?,
    val isFavorite: Boolean = false,
    val fechaVencimiento: Long? = null,
    val pesoKg: Double? = null,
    val tipoArena: String? = null,
    val pedirAlProveedor: Boolean = false,
    val notaPedido: String? = null,
    val fechaActualizacion: Long = System.currentTimeMillis()
)
