package com.mrklaus.inventario.ui.screens.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota
import com.mrklaus.inventario.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaViewModel @Inject constructor(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _mascotaFiltro = MutableStateFlow<Mascota?>(null)
    val mascotaFiltro = _mascotaFiltro.asStateFlow()

    private val _categoriaFiltro = MutableStateFlow<Categoria?>(null)
    val categoriaFiltro = _categoriaFiltro.asStateFlow()

    private val _queryBusqueda = MutableStateFlow("")
    val queryBusqueda = _queryBusqueda.asStateFlow()

    val uiState: StateFlow<ListaUiState> = combine(
        repository.getAllProductos(),
        _mascotaFiltro,
        _categoriaFiltro,
        _queryBusqueda
    ) { productos, mascota, categoria, query ->
        val filtrados = productos.filter { producto ->
            (mascota == null || producto.mascota == mascota) &&
            (categoria == null || producto.categoria == categoria) &&
            (query.isEmpty() || producto.nombre.contains(query, ignoreCase = true))
        }

        if (filtrados.isEmpty()) {
            if (productos.isEmpty()) ListaUiState.Cargando else ListaUiState.Vacio
        } else {
            ListaUiState.Exito(filtrados)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListaUiState.Cargando
    )

    fun filtrarPorMascota(mascota: Mascota?) {
        _mascotaFiltro.value = mascota
    }

    fun filtrarPorCategoria(categoria: Categoria?) {
        _categoriaFiltro.value = categoria
    }

    fun buscar(query: String) {
        _queryBusqueda.value = query
    }

    fun toggleFavorito(productoId: Long) {
        viewModelScope.launch {
            repository.toggleFavorito(productoId)
        }
    }
}
