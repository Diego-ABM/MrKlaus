package com.mrklaus.inventario.ui.screens.editar

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.data.files.ImagenManager
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota
import com.mrklaus.inventario.domain.model.Producto
import com.mrklaus.inventario.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarProductoViewModel @Inject constructor(
    private val repository: ProductoRepository,
    private val imagenManager: ImagenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productoId: Long = checkNotNull(savedStateHandle["productoId"])

    private val _productoActual = MutableStateFlow<Producto?>(null)
    val productoActual = _productoActual.asStateFlow()

    private val _fotosSeleccionadas = MutableStateFlow<List<String>>(emptyList())
    val fotosSeleccionadas = _fotosSeleccionadas.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion = _descripcion.asStateFlow()

    private val _precio = MutableStateFlow("")
    val precio = _precio.asStateFlow()

    private val _precioCompra = MutableStateFlow("")
    val precioCompra = _precioCompra.asStateFlow()

    private val _mascota = MutableStateFlow(Mascota.PERRO)
    val mascota = _mascota.asStateFlow()

    private val _categoria = MutableStateFlow(Categoria.LIMPIEZA_ASEO)
    val categoria = _categoria.asStateFlow()

    private val _stock = MutableStateFlow("0")
    val stock = _stock.asStateFlow()

    private val _pesoKg = MutableStateFlow("")
    val pesoKg = _pesoKg.asStateFlow()

    private val _fechaVencimiento = MutableStateFlow<Long?>(null)
    val fechaVencimiento = _fechaVencimiento.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getProductoById(productoId)?.let { producto ->
                _productoActual.value = producto
                _nombre.value = producto.nombre
                _descripcion.value = producto.descripcion
                _precio.value = producto.precio.toString()
                _precioCompra.value = producto.precioCompra.toString()
                _mascota.value = producto.mascota
                _categoria.value = producto.categoria
                _stock.value = producto.cantidadStock.toString()
                _pesoKg.value = producto.pesoKg?.toString() ?: ""
                _fechaVencimiento.value = producto.fechaVencimiento
                _fotosSeleccionadas.value = producto.fotos
            }
        }
    }

    fun onNombreChange(value: String) { _nombre.value = value }
    fun onDescripcionChange(value: String) { _descripcion.value = value }
    fun onPrecioChange(value: String) { _precio.value = value }
    fun onPrecioCompraChange(value: String) { _precioCompra.value = value }
    fun onMascotaChange(value: Mascota) { _mascota.value = value }
    fun onCategoriaChange(value: Categoria) { _categoria.value = value }
    fun onStockChange(value: String) { _stock.value = value }
    fun onPesoKgChange(value: String) { _pesoKg.value = value }
    fun onFechaVencimientoChange(value: Long?) { _fechaVencimiento.value = value }

    fun addFoto(uri: Uri) {
        viewModelScope.launch {
            imagenManager.guardarImagen(uri)?.let { path ->
                _fotosSeleccionadas.value = _fotosSeleccionadas.value + path
                repository.agregarFoto(productoId, path)
            }
        }
    }

    fun removeFoto(path: String) {
        viewModelScope.launch {
            repository.eliminarFoto(path)
            _fotosSeleccionadas.value = _fotosSeleccionadas.value - path
        }
    }

    fun actualizarProducto(onSuccess: () -> Unit) {
        val current = _productoActual.value ?: return
        viewModelScope.launch {
            val productoActualizado = current.copy(
                nombre = _nombre.value,
                descripcion = _descripcion.value,
                precio = _precio.value.toDoubleOrNull() ?: 0.0,
                precioCompra = _precioCompra.value.toDoubleOrNull() ?: 0.0,
                cantidadStock = _stock.value.toIntOrNull() ?: 0,
                pesoKg = _pesoKg.value.toDoubleOrNull(),
                mascota = _mascota.value,
                categoria = _categoria.value,
                fechaVencimiento = _fechaVencimiento.value,
                fechaActualizacion = System.currentTimeMillis()
            )
            
            repository.actualizarProducto(productoActualizado)
            onSuccess()
        }
    }
}
