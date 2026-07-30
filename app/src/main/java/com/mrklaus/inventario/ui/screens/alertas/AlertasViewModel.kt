package com.mrklaus.inventario.ui.screens.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.domain.model.Producto
import com.mrklaus.inventario.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AlertasViewModel @Inject constructor(
    private val repository: ProductoRepository
) : ViewModel() {

    val agotados = repository.getAgotados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proximosAVencer = repository.getProximosAVencer()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
