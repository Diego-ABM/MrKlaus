package com.mrklaus.inventario.ui.screens.ventas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    onNavigateBack: () -> Unit,
    viewModel: VentasViewModel = hiltViewModel(),
) {
    val ventas by viewModel.ventas.collectAsState()
    val rotacion by viewModel.rotacion.collectAsState()
    val gananciaNeta by viewModel.gananciaNeta.collectAsState()
    val gananciaBruta by viewModel.gananciaBruta.collectAsState()
    val capitalInvertido by viewModel.capitalInvertido.collectAsState()
    val mesSeleccionado by viewModel.mesSeleccionado.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Historial", "Rotación", "Resumen")
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ventas y Reportes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Selector de Mes
            Card(
                modifier = Modifier.fillMaxWidth().padding(Spacing.medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { viewModel.cambiarMes(-1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                    }
                    Text(
                        text = monthFormat.format(mesSeleccionado.time).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { viewModel.cambiarMes(1) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
                    }
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> {
                    if (ventas.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay ventas registradas en este mes")
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(Spacing.medium)) {
                            items(ventas) { venta ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.extraSmall),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(Spacing.medium)) {
                                        Text("Venta #${venta.id} - ${dateFormat.format(Date(venta.fecha))}", style = MaterialTheme.typography.titleSmall)
                                        venta.items.forEach { item ->
                                            Text("${item.cantidad}x ${item.nombreProducto} - ${item.precioVenta} c/u", style = MaterialTheme.typography.bodySmall)
                                        }
                                        HorizontalDivider(Modifier.padding(vertical = 4.dp))
                                        Text("Total: ${venta.total} COP", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    if (rotacion.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Sin datos de rotación este mes")
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(Spacing.medium)) {
                            items(rotacion) { item ->
                                ListItem(
                                    headlineContent = { Text(item.nombre) },
                                    trailingContent = { Text("${item.totalVendido} vendidos", fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(Spacing.medium).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        ReportCard(
                            title = "Ganancia Bruta (Ventas)",
                            value = gananciaBruta,
                            color = MaterialTheme.colorScheme.primary
                        )
                        ReportCard(
                            title = "Ganancia Neta (Utilidad)",
                            value = gananciaNeta,
                            color = Color(0xFF4CAF50)
                        )
                        HorizontalDivider()
                        ReportCard(
                            title = "Capital Invertido (Stock Actual)",
                            value = capitalInvertido,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(title: String, value: Double, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.medium), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$value COP",
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
