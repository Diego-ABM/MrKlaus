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

    // Reportes: Productos más vendidos en un rango (Unificado Ventas + Pedidos)
    @Query(
        """
        SELECT nombre, SUM(cant) as totalVendido FROM (
            SELECT nombreProducto as nombre, cantidad as cant 
            FROM venta_items 
            INNER JOIN ventas ON venta_items.ventaId = ventas.id
            WHERE ventas.fecha BETWEEN :inicio AND :fin
            UNION ALL
            SELECT nombreProducto as nombre, cantidad as cant
            FROM pedido_cliente_items
            INNER JOIN pedidos_clientes ON pedido_cliente_items.pedidoId = pedidos_clientes.id
            WHERE pedidos_clientes.fechaPedido BETWEEN :inicio AND :fin
            AND pedidos_clientes.entregado = 1 AND pedidos_clientes.pagado = 1
        ) GROUP BY nombre ORDER BY totalVendido DESC
        """
    )
    fun getRotacionProductosEnRango(inicio: Long, fin: Long): Flow<List<ProductoRotacion>>

    // Reportes: Ganancia Neta (Venta - Compra) en un rango (Unificado)
    @Query(
        """
        SELECT SUM(utilidad) FROM (
            SELECT (cantidad * (precioVenta - precioCompra)) as utilidad
            FROM venta_items 
            INNER JOIN ventas ON venta_items.ventaId = ventas.id
            WHERE ventas.fecha BETWEEN :inicio AND :fin
            UNION ALL
            SELECT (cantidad * (precioUnitario - precioCompra)) as utilidad
            FROM pedido_cliente_items
            INNER JOIN pedidos_clientes ON pedido_cliente_items.pedidoId = pedidos_clientes.id
            WHERE pedidos_clientes.fechaPedido BETWEEN :inicio AND :fin
            AND pedidos_clientes.entregado = 1 AND pedidos_clientes.pagado = 1
        )
        """
    )
    fun getGananciaNetaEnRango(inicio: Long, fin: Long): Flow<Double?>

    // Reportes: Total Ventas en un rango (Unificado)
    @Query(
        """
        SELECT SUM(total_monto) FROM (
            SELECT total as total_monto FROM ventas WHERE fecha BETWEEN :inicio AND :fin
            UNION ALL
            SELECT total as total_monto FROM pedidos_clientes 
            WHERE fechaPedido BETWEEN :inicio AND :fin
            AND entregado = 1 AND pagado = 1
        )
        """
    )
    fun getVentasTotalesEnRango(inicio: Long, fin: Long): Flow<Double?>
}
