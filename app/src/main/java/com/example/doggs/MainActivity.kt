package com.example.doggs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val dogViewModel: DogViewModel = viewModel()

            Scaffold { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "main",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("main") { DoggsApp(navController, dogViewModel) }
                    composable("addDog") { AddDogScreen(navController, dogViewModel) }
                    composable("settings") { SettingsScreen(navController) }
                    composable("profile") { ProfileScreen(navController) }
                    composable("dogDetail/{dogName}") { backStackEntry ->
                        val dogName = backStackEntry.arguments?.getString("dogName") ?: ""
                        val dog = dogViewModel.dogs.find { it.name == dogName }
                        dog?.let { DogDetailScreen(navController, it, dogViewModel) }
                    }
                }
            }
        }
    }
}