package com.mrklaus.inventario.ui.screens.pedidos_clientes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.data.local.entity.PedidoClienteItemEntity
import com.mrklaus.inventario.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosClienteScreen(
    onNavigateBack: () -> Unit,
    viewModel: PedidosClienteViewModel = hiltViewModel()
) {
    val pedidos by viewModel.pedidos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos de Clientes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Pedido")
            }
        }
    ) { padding ->
        if (pedidos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay pedidos registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                items(pedidos) { item ->
                    val pedido = item.pedido
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.medium)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(pedido.nombreCliente, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    pedido.contacto?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Badge(
                                        containerColor = if (pedido.entregado) Color(0xFF4CAF50) else Color(0xFFFFA000)
                                    ) {
                                        Text(if (pedido.entregado) "Entregado" else "Pendiente Entrega", color = Color.White)
                                    }
                                    if (!pedido.pagado) {
                                        Badge(containerColor = Color.Red) {
                                            Text("PAGO PENDIENTE", color = Color.White)
                                        }
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(Spacing.small))
                            
                            item.items.forEach { line ->
                                Text("${line.cantidad}x ${line.nombreProducto} - ${line.precioUnitario} c/u", style = MaterialTheme.typography.bodyMedium)
                            }
                            
                            HorizontalDivider(Modifier.padding(vertical = Spacing.small))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total: ${pedido.total} COP", fontWeight = FontWeight.Bold)
                                Row {
                                    IconButton(onClick = { viewModel.eliminarPedido(pedido) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                    IconButton(onClick = { viewModel.marcarPagado(pedido, !pedido.pagado) }) {
                                        Icon(
                                            imageVector = if (pedido.pagado) Icons.Default.Payments else Icons.Default.MoneyOff,
                                            contentDescription = "Pago",
                                            tint = if (pedido.pagado) Color(0xFF4CAF50) else Color.Red
                                        )
                                    }
                                    IconButton(onClick = { viewModel.marcarEntregado(pedido, !pedido.entregado) }) {
                                        Icon(
                                            imageVector = if (pedido.entregado) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                                            contentDescription = "Entrega",
                                            tint = if (pedido.entregado) Color(0xFF4CAF50) else Color(0xFFFFA000)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        NuevoPedidoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, contacto, items ->
                viewModel.crearPedido(nombre, contacto, items)
                showAddDialog = false
            },
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPedidoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?, List<PedidoClienteItemEntity>) -> Unit,
    viewModel: PedidosClienteViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }
    val itemsPedido = remember { mutableStateListOf<PedidoClienteItemEntity>() }
    
    val query by viewModel.busquedaProducto.collectAsState()
    val sugerencias by viewModel.productosSugeridos.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Pedido") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Cliente") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contacto,
                    onValueChange = { contacto = it },
                    label = { Text("Contacto / Tel") },
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.small)
                )
                
                HorizontalDivider(Modifier.padding(vertical = Spacing.medium))
                
                Text("Productos", style = MaterialTheme.typography.titleSmall)
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onBusquedaChange(it) },
                    label = { Text("Buscar producto...") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                // Agregar como producto personalizado
                                itemsPedido.add(
                                    PedidoClienteItemEntity(
                                        pedidoId = 0,
                                        productoId = null,
                                        nombreProducto = query,
                                        cantidad = 1,
                                        precioUnitario = 0.0
                                    )
                                )
                                viewModel.onBusquedaChange("")
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Agregar personalizado")
                            }
                        }
                    }
                )
                
                if (sugerencias.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        sugerencias.take(3).forEach { prod ->
                            ListItem(
                                headlineContent = { Text(prod.nombre) },
                                modifier = Modifier.clickable {
                                    itemsPedido.add(
                                        PedidoClienteItemEntity(
                                            pedidoId = 0,
                                            productoId = prod.id,
                                            nombreProducto = prod.nombre,
                                            cantidad = 1,
                                            precioUnitario = prod.precio
                                        )
                                    )
                                    viewModel.onBusquedaChange("")
                                }
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(Spacing.small))
                
                itemsPedido.forEachIndexed { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.nombreProducto, modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            if (item.cantidad > 1) {
                                itemsPedido[index] = item.copy(cantidad = item.cantidad - 1)
                            } else {
                                itemsPedido.removeAt(index)
                            }
                        }) { Icon(Icons.Default.Remove, contentDescription = null) }
                        Text("${item.cantidad}")
                        IconButton(onClick = {
                            itemsPedido[index] = item.copy(cantidad = item.cantidad + 1)
                        }) { Icon(Icons.Default.Add, contentDescription = null) }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre, contacto, itemsPedido.toList()) },
                enabled = nombre.isNotBlank() && itemsPedido.isNotEmpty()
            ) { Text("Crear") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
