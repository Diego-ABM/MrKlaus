package com.mrklaus.inventario.domain.repository

import com.mrklaus.inventario.domain.model.ProductoRotacion
import com.mrklaus.inventario.domain.model.Venta
import kotlinx.coroutines.flow.Flow

interface VentaRepository {
    suspend fun registrarVenta(venta: Venta)
    fun getTodasLasVentas(): Flow<List<Venta>>
    fun getRotacionProductos(): Flow<List<ProductoRotacion>>
    fun getGananciasTotales(): Flow<Double>
}
