package com.mrklaus.inventario.ui.screens.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedidosViewModel @Inject constructor(
    private val repository: ProductoRepository
) : ViewModel() {

    val paraPedido = repository.getParaPedido()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorito(productoId: Long) {
        viewModelScope.launch {
            repository.toggleFavorito(productoId)
        }
    }
}
