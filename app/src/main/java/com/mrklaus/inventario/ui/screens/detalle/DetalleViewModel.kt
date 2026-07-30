package com.mrklaus.inventario.ui.screens.detalle

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.data.files.ImagenManager
import com.mrklaus.inventario.domain.model.Producto
import com.mrklaus.inventario.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val repository: ProductoRepository,
    private val imagenManager: ImagenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productoId: Long = checkNotNull(savedStateHandle["productoId"])

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState = _uiState.asStateFlow()

    init {
        cargarProducto()
    }

    private fun cargarProducto() {
        viewModelScope.launch {
            val producto = repository.getProductoById(productoId)
            if (producto != null) {
                _uiState.value = DetalleUiState.Exito(producto)
            } else {
                _uiState.value = DetalleUiState.NoEncontrado
            }
        }
    }

    fun actualizarStock(nuevaCantidad: Int) {
        val currentState = _uiState.value
        if (currentState is DetalleUiState.Exito) {
            val productoActualizado = currentState.producto.copy(cantidadStock = nuevaCantidad)
            viewModelScope.launch {
                repository.actualizarProducto(productoActualizado)
                _uiState.value = DetalleUiState.Exito(productoActualizado)
            }
        }
    }

    fun eliminarProducto(onSuccess: () -> Unit) {
        val currentState = _uiState.value
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
                cargarProducto()
            }
        }
    }

    fun eliminarFoto(ruta: String) {
        viewModelScope.launch {
            repository.eliminarFoto(ruta)
            cargarProducto()
        }
    }

    fun toggleFavorito() {
        viewModelScope.launch {
            repository.toggleFavorito(productoId)
            cargarProducto()
        }
    }
}
