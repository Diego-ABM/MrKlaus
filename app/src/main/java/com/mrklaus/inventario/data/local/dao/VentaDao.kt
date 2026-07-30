package com.mrklaus.inventario.data.local.dao

import androidx.room.*
import com.mrklaus.inventario.data.local.entity.VentaEntity
import com.mrklaus.inventario.data.local.entity.VentaItemEntity
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

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun getTodasLasVentas(): Flow<List<VentaEntity>>

    @Query("SELECT * FROM venta_items WHERE ventaId = :ventaId")
    suspend fun getItemsDeVenta(ventaId: Long): List<VentaItemEntity>

    // Reportes: Productos más vendidos
    @Query("""
        SELECT nombreProducto as nombre, SUM(cantidad) as totalVendido 
        FROM venta_items 
        GROUP BY nombreProducto 
        ORDER BY totalVendido DESC
    """)
    fun getRotacionProductos(): Flow<List<ProductoRotacion>>

    // Reportes: Ganancias totales
    @Query("SELECT SUM(total) FROM ventas")
    fun getGananciasTotales(): Flow<Double?>
}

data class ProductoRotacion(
    val nombre: String,
    val totalVendido: Int
)
