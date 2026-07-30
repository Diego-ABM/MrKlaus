package com.mrklaus.inventario.domain.repository

import com.mrklaus.inventario.domain.model.ProductoRotacion
import com.mrklaus.inventario.domain.model.Venta
import kotlinx.coroutines.flow.Flow

interface VentaRepository {
    suspend fun registrarVenta(venta: Venta)
    fun getVentasPorRango(inicio: Long, fin: Long): Flow<List<Venta>>
    fun getRotacionProductosEnRango(inicio: Long, fin: Long): Flow<List<ProductoRotacion>>
    fun getGananciaNetaEnRango(inicio: Long, fin: Long): Flow<Double>
    fun getVentasTotalesEnRango(inicio: Long, fin: Long): Flow<Double>
}
