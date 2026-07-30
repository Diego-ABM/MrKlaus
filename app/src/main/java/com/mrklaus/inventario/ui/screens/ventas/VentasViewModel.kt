package com.mrklaus.inventario.ui.screens.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VentasViewModel @Inject constructor(
    private val repository: VentaRepository
) : ViewModel() {

    val ventas = repository.getTodasLasVentas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rotacion = repository.getRotacionProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ganancias = repository.getGananciasTotales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}
