package com.mrklaus.inventario.ui.screens.alertas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.ui.components.ProductoCard
import com.mrklaus.inventario.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AlertasViewModel = hiltViewModel(),
) {
    val agotados by viewModel.agotados.collectAsState()
    val proximosAVencer by viewModel.proximosAVencer.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Vencimientos", "Agotados")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas de Inventario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            
            val productos = if (selectedTab == 0) proximosAVencer else agotados
            
            if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No hay alertas en esta sección")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    items(productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { onNavigateToDetalle(producto.id) },
                            onToggleFavorite = { viewModel.toggleFavorito(producto.id) }
                        )
                    }
                }
            }
        }
    }
}
