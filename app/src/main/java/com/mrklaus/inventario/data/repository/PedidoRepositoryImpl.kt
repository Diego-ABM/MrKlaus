package com.mrklaus.inventario.data.repository

import com.mrklaus.inventario.data.local.dao.PedidoClienteConItems
import com.mrklaus.inventario.data.local.dao.PedidoClienteDao
import com.mrklaus.inventario.data.local.dao.ProductoDao
import com.mrklaus.inventario.data.local.entity.PedidoClienteEntity
import com.mrklaus.inventario.data.local.entity.PedidoClienteItemEntity
import com.mrklaus.inventario.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PedidoRepositoryImpl @Inject constructor(
    private val pedidoDao: PedidoClienteDao,
    private val productoDao: ProductoDao
) : PedidoRepository {
    override fun getAllPedidos(): Flow<List<PedidoClienteConItems>> = pedidoDao.getAllPedidos()

    override suspend fun crearPedido(pedido: PedidoClienteEntity, items: List<PedidoClienteItemEntity>) {
        pedidoDao.crearPedido(pedido, items)
    }

    override suspend fun actualizarPedido(pedido: PedidoClienteEntity) {
        pedidoDao.actualizarPedido(pedido)
    }

    override suspend fun completarEntrega(pedidoId: Long, entregado: Boolean) {
        val pedidoConItems = pedidoDao.getPedidoById(pedidoId) ?: return
        val pedido = pedidoConItems.pedido
        
        // Solo descontamos stock si se marca como entregado y no se ha descontado antes
        if (entregado && !pedido.stockDescontado) {
            pedidoConItems.items.forEach { item ->
                item.productoId?.let { pid ->
                    val prod = productoDao.getPorId(pid)?.producto
                    if (prod != null) {
                        productoDao.actualizarStock(pid, prod.cantidadStock - item.cantidad)
                    }
                }
            }
            pedidoDao.actualizarPedido(pedido.copy(entregado = true, stockDescontado = true))
        } else if (!entregado) {
            // Si se desmarca como entregado, podrías elegir revertir el stock o no.
            // Para simplicidad, solo marcamos entregado=false
            pedidoDao.actualizarPedido(pedido.copy(entregado = false))
        }
    }

    override suspend fun eliminarPedido(pedido: PedidoClienteEntity) {
        pedidoDao.eliminarPedido(pedido)
    }
}
