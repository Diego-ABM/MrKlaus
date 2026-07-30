package com.mrklaus.inventario.ui.screens.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val repository: ProductoRepository
) : ViewModel() {

    val uiState: StateFlow<ListaUiState> = repository.getFavoritos()
        .map { productos ->
            if (productos.isEmpty()) {
                ListaUiState.Vacio
            } else {
                ListaUiState.Exito(productos)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListaUiState.Cargando
        )

    fun toggleFavorito(productoId: Long) {
        viewModelScope.launch {
            repository.toggleFavorito(productoId)
        }
    }
}
