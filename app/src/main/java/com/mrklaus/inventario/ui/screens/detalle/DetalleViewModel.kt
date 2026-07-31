package com.mrklaus.inventario.ui.screens.detalle

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.data.files.ImagenManager
import com.mrklaus.inventario.domain.model.Producto
import com.mrklaus.inventario.domain.model.Venta
import com.mrklaus.inventario.domain.model.VentaItem
import com.mrklaus.inventario.domain.repository.ProductoRepository
import com.mrklaus.inventario.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val repository: ProductoRepository,
    private val ventaRepository: VentaRepository,
    private val imagenManager: ImagenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productoId: Long = checkNotNull(savedStateHandle["productoId"])

    val uiState: StateFlow<DetalleUiState> = repository.getProductoByIdFlow(productoId)
        .map { producto: Producto? ->
            if (producto != null) DetalleUiState.Exito(producto) else DetalleUiState.NoEncontrado
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetalleUiState.Cargando
        )

    fun actualizarStock(nuevaCantidad: Int) {
        val currentState = uiState.value
        if (currentState is DetalleUiState.Exito) {
            val productoActualizado = currentState.producto.copy(cantidadStock = nuevaCantidad)
            viewModelScope.launch {
                repository.actualizarProducto(productoActualizado)
            }
        }
    }

    fun eliminarProducto(onSuccess: () -> Unit) {
        val currentState = uiState.value
        if (currentState is DetalleUiState.Exito) {
            viewModelScope.launch {
                repository.eliminarProducto(currentState.producto)
                onSuccess()
            }
        }
    }

    fun agregarFoto(uri: Uri) {
        viewModelScope.launch {
            val path = imagenManager.guardarImagen(uri)
            if (path != null) {
                repository.agregarFoto(productoId, path)
            }
        }
    }

    fun eliminarFoto(ruta: String) {
        viewModelScope.launch {
            repository.eliminarFoto(ruta)
        }
    }

    fun toggleFavorito() {
        viewModelScope.launch {
            repository.toggleFavorito(productoId)
        }
    }

    fun actualizarEstadoPedido(pedir: Boolean, nota: String?) {
        viewModelScope.launch {
            repository.actualizarEstadoPedido(productoId, pedir, nota)
        }
    }

    fun registrarVenta(cantidad: Int) {
        val currentState = uiState.value
        if (currentState is DetalleUiState.Exito) {
            val producto = currentState.producto
            if (producto.cantidadStock >= cantidad) {
                viewModelScope.launch {
                    val venta = Venta(
                        total = producto.precio * cantidad,
                        items = listOf(
                            VentaItem(
                                productoId = producto.id,
                                nombreProducto = producto.nombre,
                                cantidad = cantidad,
                                precioCompra = producto.precioCompra,
                                precioVenta = producto.precio
                            )
                        )
                    )
                    ventaRepository.registrarVenta(venta)
                    repository.actualizarStock(producto.id, producto.cantidadStock - cantidad)
                }
            }
        }
    }
}
