package com.mrklaus.inventario.ui.screens.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrklaus.inventario.domain.repository.ProductoRepository
import com.mrklaus.inventario.domain.repository.VentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VentasViewModel @Inject constructor(
    private val repository: VentaRepository,
    private val productoRepository: ProductoRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _mesSeleccionado = MutableStateFlow(Calendar.getInstance())
    val mesSeleccionado = _mesSeleccionado.asStateFlow()

    private val _modoAnual = MutableStateFlow(false)
    val modoAnual = _modoAnual.asStateFlow()

    private val rangoFechas = combine(_mesSeleccionado, _modoAnual) { cal, anual ->
        val inicio = cal.clone() as Calendar
        if (anual) {
            inicio.set(Calendar.MONTH, 0)
        }
        inicio.set(Calendar.DAY_OF_MONTH, 1)
        inicio.set(Calendar.HOUR_OF_DAY, 0)
        inicio.set(Calendar.MINUTE, 0)
        inicio.set(Calendar.SECOND, 0)
        
        val fin = inicio.clone() as Calendar
        if (anual) {
            fin.add(Calendar.YEAR, 1)
        } else {
            fin.add(Calendar.MONTH, 1)
        }
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

    val inversionPendiente = productoRepository.getInversionPendiente()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun cambiarMes(delta: Int) {
        val nuevaFecha = _mesSeleccionado.value.clone() as Calendar
        if (_modoAnual.value) {
            nuevaFecha.add(Calendar.YEAR, delta)
        } else {
            nuevaFecha.add(Calendar.MONTH, delta)
        }
        _mesSeleccionado.value = nuevaFecha
    }

    fun toggleModoAnual() {
        _modoAnual.value = !_modoAnual.value
    }

    fun exportarReporte(uri: android.net.Uri) {
        viewModelScope.launch {
            val sb = StringBuilder()
            sb.append("--- REPORTE MR. KLAUS ---\n")
            sb.append("Periodo: ${if (_modoAnual.value) "Anual " + _mesSeleccionado.value.get(Calendar.YEAR) else "Mensual " + _mesSeleccionado.value.get(Calendar.MONTH)}\n\n")
            sb.append("Resumen Financiero:\n")
            sb.append("- Ganancia Bruta: ${gananciaBruta.value} COP\n")
            sb.append("- Ganancia Neta: ${gananciaNeta.value} COP\n")
            sb.append("- Capital en Stock: ${capitalInvertido.value} COP\n")
            sb.append("- Inversión Pendiente (Próximos Pedidos): ${inversionPendiente.value} COP\n\n")
            
            sb.append("Top Productos (Rotación):\n")
            rotacion.value.forEach { 
                sb.append("- ${it.nombre}: ${it.totalVendido} unidades\n")
            }
            
            try {
                context.contentResolver.openOutputStream(uri)?.use { 
                    it.write(sb.toString().toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
