package com.cibertec.t2_salcedo


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.cibertec.t2_salcedo.ui.theme.T2_SalcedoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mantén esto si lo deseas para el modo edge-to-edge
        setContent {
            T2_SalcedoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PersonApp() // Llama a tu composable principal de la aplicación de personas
                }
            }
        }
    }
}

@Composable
fun PersonApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "person_list") {
        composable("person_list") {
            PersonListScreen(navController = navController)
        }
        composable(
            "person_entry?personId={personId}",
            arguments = listOf(navArgument("personId") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getInt("personId")
            PersonEntryScreen(navController = navController, personId = personId)
        }
    }
}
