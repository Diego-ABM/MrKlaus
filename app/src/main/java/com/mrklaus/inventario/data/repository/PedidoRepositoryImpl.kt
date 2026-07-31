package com.mrklaus.inventario.data.repository

import com.mrklaus.inventario.data.local.dao.PedidoClienteConItems
import com.mrklaus.inventario.data.local.dao.PedidoClienteDao
import com.mrklaus.inventario.data.local.entity.PedidoClienteEntity
import com.mrklaus.inventario.data.local.entity.PedidoClienteItemEntity
import com.mrklaus.inventario.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PedidoRepositoryImpl @Inject constructor(
    private val pedidoDao: PedidoClienteDao
) : PedidoRepository {
    override fun getAllPedidos(): Flow<List<PedidoClienteConItems>> = pedidoDao.getAllPedidos()

    override suspend fun crearPedido(pedido: PedidoClienteEntity, items: List<PedidoClienteItemEntity>) {
        pedidoDao.crearPedido(pedido, items)
    }

    override suspend fun actualizarPedido(pedido: PedidoClienteEntity) {
        pedidoDao.actualizarPedido(pedido)
    }

    override suspend fun eliminarPedido(pedido: PedidoClienteEntity) {
        pedidoDao.eliminarPedido(pedido)
    }
}
