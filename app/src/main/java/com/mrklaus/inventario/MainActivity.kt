package com.mrklaus.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrklaus.inventario.ui.navigation.NavGraph
import com.mrklaus.inventario.ui.navigation.Screen
import com.mrklaus.inventario.ui.theme.MrKlausTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MrKlausTheme {
                val navController = rememberNavController()
                val items = listOf(
                    NavigationItem("Inventario", Screen.Lista, Icons.Default.List),
                    NavigationItem("Favoritos", Screen.Favoritos, Icons.Default.Favorite),
                    NavigationItem("Alertas", Screen.Alertas, Icons.Default.Notifications),
                    NavigationItem("Pedidos", Screen.Pedidos, Icons.Default.ShoppingCart),
                    NavigationItem("Ventas", Screen.Ventas, Icons.Default.Assessment)
                )

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        
                        // Solo mostrar bottom bar en pantallas principales (o siempre si prefieres)
                        val showBottomBar = items.any { it.screen.route == currentDestination?.route }

                        if (showBottomBar) {
                            NavigationBar {
                                items.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.title) },
                                        label = { Text(item.title) },
                                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                                        onClick = {
                                            navController.navigate(item.screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
