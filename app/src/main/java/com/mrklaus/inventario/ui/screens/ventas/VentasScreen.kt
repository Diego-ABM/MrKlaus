package com.mrklaus.inventario.ui.screens.ventas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    viewModel: VentasViewModel = hiltViewModel()
) {
    val ventas by viewModel.ventas.collectAsState()
    val rotacion by viewModel.rotacion.collectAsState()
    val ganancias by viewModel.ganancias.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Historial", "Rotación", "Ganancias")
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ventas y Reportes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
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
                                    Divider(Modifier.padding(vertical = 4.dp))
                                    Text("Total: ${venta.total} COP", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    LazyColumn(contentPadding = PaddingValues(Spacing.medium)) {
                        items(rotacion) { item ->
                            ListItem(
                                headlineContent = { Text(item.nombre) },
                                trailingContent = { Text("${item.totalVendido} vendidos", fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
                2 -> {
                    Column(modifier = Modifier.fillMaxSize().padding(Spacing.large), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Text("Ganancias Totales Acumuladas", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(Spacing.medium))
                        Text(
                            text = "$ganancias COP",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
