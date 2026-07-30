package com.mrklaus.inventario.data.local.dao

import androidx.room.*
import com.mrklaus.inventario.data.local.entity.VentaEntity
import com.mrklaus.inventario.data.local.entity.VentaItemEntity
import com.mrklaus.inventario.domain.model.ProductoRotacion
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Insert
    suspend fun insertarVenta(venta: VentaEntity): Long

    @Insert
    suspend fun insertarItems(items: List<VentaItemEntity>)

    @Transaction
    suspend fun registrarVenta(venta: VentaEntity, items: List<VentaItemEntity>) {
        val ventaId = insertarVenta(venta)
        val itemsConId = items.map { it.copy(ventaId = ventaId) }
        insertarItems(itemsConId)
    }

    @Query("SELECT * FROM ventas WHERE fecha BETWEEN :inicio AND :fin ORDER BY fecha DESC")
    fun getVentasPorRango(inicio: Long, fin: Long): Flow<List<VentaEntity>>

    @Query("SELECT * FROM venta_items WHERE ventaId = :ventaId")
    suspend fun getItemsDeVenta(ventaId: Long): List<VentaItemEntity>

    // Reportes: Productos más vendidos en un rango
    @Query(
        """
        SELECT nombreProducto as nombre, SUM(cantidad) as totalVendido 
        FROM venta_items 
        INNER JOIN ventas ON venta_items.ventaId = ventas.id
        WHERE ventas.fecha BETWEEN :inicio AND :fin
        GROUP BY nombreProducto 
        ORDER BY totalVendido DESC
        """
    )
    fun getRotacionProductosEnRango(inicio: Long, fin: Long): Flow<List<ProductoRotacion>>

    // Reportes: Ganancia Neta (Venta - Compra) en un rango
    @Query(
        """
        SELECT SUM(cantidad * (precioVenta - precioCompra)) 
        FROM venta_items 
        INNER JOIN ventas ON venta_items.ventaId = ventas.id
        WHERE ventas.fecha BETWEEN :inicio AND :fin
        """
    )
    fun getGananciaNetaEnRango(inicio: Long, fin: Long): Flow<Double?>

    // Reportes: Total Ventas en un rango
    @Query("SELECT SUM(total) FROM ventas WHERE fecha BETWEEN :inicio AND :fin")
    fun getVentasTotalesEnRango(inicio: Long, fin: Long): Flow<Double?>
}
