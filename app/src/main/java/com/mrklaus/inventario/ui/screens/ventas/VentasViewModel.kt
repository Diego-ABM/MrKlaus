package com.mrklaus.inventario.ui.screens.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.domain.repository.ProductoRepository
import com.mrklaus.inventario.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VentasViewModel @Inject constructor(
    private val repository: VentaRepository,
    productoRepository: ProductoRepository
) : ViewModel() {

    private val _mesSeleccionado = MutableStateFlow(Calendar.getInstance())
    val mesSeleccionado = _mesSeleccionado.asStateFlow()

    private val rangoFechas = _mesSeleccionado.map { cal ->
        val inicio = cal.clone() as Calendar
        inicio.set(Calendar.DAY_OF_MONTH, 1)
        inicio.set(Calendar.HOUR_OF_DAY, 0)
        inicio.set(Calendar.MINUTE, 0)
        inicio.set(Calendar.SECOND, 0)
        
        val fin = inicio.clone() as Calendar
        fin.add(Calendar.MONTH, 1)
        fin.add(Calendar.SECOND, -1)
        
        inicio.timeInMillis to fin.timeInMillis
    }

    val ventas = rangoFechas.flatMapLatest { (inicio, fin) ->
        repository.getVentasPorRango(inicio, fin)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rotacion = rangoFechas.flatMapLatest { (inicio, fin) ->
        repository.getRotacionProductosEnRango(inicio, fin)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gananciaNeta = rangoFechas.flatMapLatest { (inicio, fin) ->
        repository.getGananciaNetaEnRango(inicio, fin)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val gananciaBruta = rangoFechas.flatMapLatest { (inicio, fin) ->
        repository.getVentasTotalesEnRango(inicio, fin)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val capitalInvertido = productoRepository.getCapitalInvertido()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun cambiarMes(delta: Int) {
        val nuevaFecha = _mesSeleccionado.value.clone() as Calendar
        nuevaFecha.add(Calendar.MONTH, delta)
        _mesSeleccionado.value = nuevaFecha
    }
}
