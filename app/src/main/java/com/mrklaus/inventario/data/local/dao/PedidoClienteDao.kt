package com.mrklaus.inventario.data.local.dao

import androidx.room.*
import com.mrklaus.inventario.data.local.entity.PedidoClienteEntity
import com.mrklaus.inventario.data.local.entity.PedidoClienteItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoClienteDao {
    @Insert
    suspend fun insertarPedido(pedido: PedidoClienteEntity): Long

    @Insert
    suspend fun insertarItems(items: List<PedidoClienteItemEntity>)

    @Transaction
    suspend fun crearPedido(pedido: PedidoClienteEntity, items: List<PedidoClienteItemEntity>) {
        val id = insertarPedido(pedido)
        val itemsConId = items.map { it.copy(pedidoId = id) }
        insertarItems(itemsConId)
    }

    @Update
    suspend fun actualizarPedido(pedido: PedidoClienteEntity)

    @Query("SELECT * FROM pedidos_clientes ORDER BY fechaPedido DESC")
    fun getAllPedidos(): Flow<List<PedidoClienteConItems>>

    @Query("SELECT * FROM pedidos_clientes WHERE id = :id")
    suspend fun getPedidoById(id: Long): PedidoClienteConItems?

    @Delete
    suspend fun eliminarPedido(pedido: PedidoClienteEntity)
}

data class PedidoClienteConItems(
    @Embedded val pedido: PedidoClienteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pedidoId"
    )
    val items: List<PedidoClienteItemEntity>
)
