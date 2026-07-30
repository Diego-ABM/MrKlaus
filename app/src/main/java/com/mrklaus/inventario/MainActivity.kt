package com.mrklaus.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.mrklaus.inventario.ui.navigation.NavGraph
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
                NavGraph(navController = navController)
            }
        }
    }
}
