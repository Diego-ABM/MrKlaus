package com.mrklaus.inventario.ui.screens.pedidos_clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.data.local.dao.PedidoClienteConItems
import com.mrklaus.inventario.data.local.entity.PedidoClienteEntity
import com.mrklaus.inventario.data.local.entity.PedidoClienteItemEntity
import com.mrklaus.inventario.domain.model.Producto
import com.mrklaus.inventario.domain.repository.PedidoRepository
import com.mrklaus.inventario.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedidosClienteViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    val pedidos = pedidoRepository.getAllPedidos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _busquedaProducto = MutableStateFlow("")
    val busquedaProducto = _busquedaProducto.asStateFlow()

    val productosSugeridos = _busquedaProducto.flatMapLatest { query ->
        if (query.length < 2) flowOf(emptyList())
        else productoRepository.getAllProductos().map { list ->
            list.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBusquedaChange(query: String) {
        _busquedaProducto.value = query
    }

    fun crearPedido(nombre: String, contacto: String?, items: List<PedidoClienteItemEntity>) {
        viewModelScope.launch {
            // Obtener precio de compra actual para cada item
            val itemsConCosto = items.map { item ->
                if (item.productoId != null) {
                    val prod = productoRepository.getProductoById(item.productoId)
                    item.copy(precioCompra = prod?.precioCompra ?: 0.0)
                } else {
                    item // Producto personalizado, precioCompra=0 por defecto
                }
            }
            val total = itemsConCosto.sumOf { it.precioUnitario * it.cantidad }
            val pedido = PedidoClienteEntity(
                nombreCliente = nombre,
                contacto = contacto,
                total = total,
                fechaEntregaEstimada = null
            )
            pedidoRepository.crearPedido(pedido, itemsConCosto)
        }
    }

    fun marcarEntregado(pedido: PedidoClienteEntity, entregado: Boolean) {
        viewModelScope.launch {
            pedidoRepository.completarEntrega(pedido.id, entregado)
        }
    }

    fun marcarPagado(pedido: PedidoClienteEntity, pagado: Boolean) {
        viewModelScope.launch {
            pedidoRepository.actualizarPedido(pedido.copy(pagado = pagado))
        }
    }

    fun eliminarPedido(pedido: PedidoClienteEntity) {
        viewModelScope.launch {
            pedidoRepository.eliminarPedido(pedido)
        }
    }
}
