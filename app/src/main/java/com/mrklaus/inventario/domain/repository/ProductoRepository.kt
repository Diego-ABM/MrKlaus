package com.mrklaus.inventario.domain.repository

import com.mrklaus.inventario.domain.model.Mascota
import com.mrklaus.inventario.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    fun getAllProductos(): Flow<List<Producto>>
    fun getProductosPorMascota(mascota: Mascota): Flow<List<Producto>>
    suspend fun getProductoById(id: Long): Producto?
    suspend fun insertarProducto(producto: Producto): Long
    suspend fun actualizarProducto(producto: Producto)
    suspend fun eliminarProducto(producto: Producto)
    suspend fun agregarFoto(productoId: Long, ruta: String)
    suspend fun eliminarFoto(ruta: String)
    fun getFavoritos(): Flow<List<Producto>>
    suspend fun toggleFavorito(productoId: Long)
}
