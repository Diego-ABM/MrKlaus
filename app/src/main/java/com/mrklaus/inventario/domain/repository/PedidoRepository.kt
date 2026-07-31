package com.mrklaus.inventario.domain.repository

import com.mrklaus.inventario.data.local.dao.PedidoClienteConItems
import com.mrklaus.inventario.data.local.entity.PedidoClienteEntity
import com.mrklaus.inventario.data.local.entity.PedidoClienteItemEntity
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {
    fun getAllPedidos(): Flow<List<PedidoClienteConItems>>
    suspend fun crearPedido(pedido: PedidoClienteEntity, items: List<PedidoClienteItemEntity>)
    suspend fun actualizarPedido(pedido: PedidoClienteEntity)
    suspend fun completarEntrega(pedidoId: Long, entregado: Boolean)
    suspend fun eliminarPedido(pedido: PedidoClienteEntity)
}
