package com.mrklaus.inventario.ui.screens.agregar

import android.net.Uri
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
class AgregarViewModel @Inject constructor(
    private val repository: ProductoRepository,
    private val imagenManager: ImagenManager
) : ViewModel() {

    private val _fotosSeleccionadas = MutableStateFlow<List<Uri>>(emptyList())
    val fotosSeleccionadas = _fotosSeleccionadas.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion = _descripcion.asStateFlow()

    private val _precio = MutableStateFlow("")
    val precio = _precio.asStateFlow()

    private val _mascota = MutableStateFlow(Mascota.PERRO)
    val mascota = _mascota.asStateFlow()

    private val _categoria = MutableStateFlow(Categoria.LIMPIEZA_ASEO)
    val categoria = _categoria.asStateFlow()

    private val _stock = MutableStateFlow("0")
    val stock = _stock.asStateFlow()

    fun onNombreChange(value: String) { _nombre.value = value }
    fun onDescripcionChange(value: String) { _descripcion.value = value }
    fun onPrecioChange(value: String) { _precio.value = value }
    fun onMascotaChange(value: Mascota) { _mascota.value = value }
    fun onCategoriaChange(value: Categoria) { _categoria.value = value }
    fun onStockChange(value: String) { _stock.value = value }

    fun addFoto(uri: Uri) {
        if (_fotosSeleccionadas.value.size < 5) {
            _fotosSeleccionadas.value = _fotosSeleccionadas.value + uri
        }
    }

    fun removeFoto(uri: Uri) {
        _fotosSeleccionadas.value = _fotosSeleccionadas.value - uri
    }

    fun guardarProducto(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Guardar imágenes primero
            val rutas = _fotosSeleccionadas.value.mapNotNull { uri ->
                imagenManager.guardarImagen(uri)
            }

            val nuevoProducto = Producto(
                id = 0,
                nombre = _nombre.value,
                descripcion = _descripcion.value,
                precio = _precio.value.toDoubleOrNull() ?: 0.0,
                cantidadStock = _stock.value.toIntOrNull() ?: 0,
                stockMinimo = 3,
                mascota = _mascota.value,
                categoria = _categoria.value,
                variante = null,
                nota = null,
                isFavorite = false,
                fechaVencimiento = null,
                pesoKg = null,
                tipoArena = null,
                fotos = emptyList(), // Se agregarán vía tabla fotos_producto
                fechaActualizacion = System.currentTimeMillis()
            )
            
            val productoId = repository.insertarProducto(nuevoProducto)
            
            // Vincular fotos guardadas
            rutas.forEach { ruta ->
                repository.agregarFoto(productoId, ruta)
            }

            onSuccess()
        }
    }
}
