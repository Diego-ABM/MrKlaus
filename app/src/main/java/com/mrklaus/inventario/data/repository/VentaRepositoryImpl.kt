package com.mrklaus.inventario.data.repository

import com.mrklaus.inventario.data.local.dao.VentaDao
import com.mrklaus.inventario.data.local.entity.VentaEntity
import com.mrklaus.inventario.data.local.entity.VentaItemEntity
import com.mrklaus.inventario.domain.model.ProductoRotacion
import com.mrklaus.inventario.domain.model.Venta
import com.mrklaus.inventario.domain.model.VentaItem
import com.mrklaus.inventario.domain.repository.VentaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VentaRepositoryImpl @Inject constructor(
    private val ventaDao: VentaDao
) : VentaRepository {

    override suspend fun registrarVenta(venta: Venta) {
        val entity = VentaEntity(total = venta.total, fecha = venta.fecha)
        val itemEntities = venta.items.map { 
            VentaItemEntity(
                ventaId = 0, // Será asignado por el DAO
                productoId = it.productoId,
                nombreProducto = it.nombreProducto,
                cantidad = it.cantidad,
                precioVenta = it.precioVenta
            )
        }
        ventaDao.registrarVenta(entity, itemEntities)
    }

    override fun getTodasLasVentas(): Flow<List<Venta>> {
        return ventaDao.getTodasLasVentas().map { list ->
            list.map { entity ->
                val items = ventaDao.getItemsDeVenta(entity.id).map { it.toDomain() }
                Venta(
                    id = entity.id,
                    fecha = entity.fecha,
                    total = entity.total,
                    items = items
                )
            }
        }
    }

    override fun getRotacionProductos(): Flow<List<ProductoRotacion>> {
        return ventaDao.getRotacionProductos().map { list ->
            list.map { ProductoRotacion(it.nombre, it.totalVendido) }
        }
    }

    override fun getGananciasTotales(): Flow<Double> {
        return ventaDao.getGananciasTotales().map { it ?: 0.0 }
    }

    private fun VentaItemEntity.toDomain() = VentaItem(
        id = id,
        ventaId = ventaId,
        productoId = productoId,
        nombreProducto = nombreProducto,
        cantidad = cantidad,
        precioVenta = precioVenta
    )
}
