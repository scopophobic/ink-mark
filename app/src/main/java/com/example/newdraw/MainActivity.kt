package com.example.newdraw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newdraw.data.local.DatabaseProvider
import com.example.newdraw.data.repository.MemoryRepository
import com.example.newdraw.ui.screens.draw.DrawScreen
import com.example.newdraw.ui.screens.entry.EntryScreen
import com.example.newdraw.ui.screens.home.HomeScreen
import com.example.newdraw.ui.theme.NewdrawTheme
import com.example.newdraw.viewmodel.EntryViewModel
import com.example.newdraw.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = DatabaseProvider.getDatabase(this)
        val repository = MemoryRepository(database.memoryDao())
        
        setContent {
            NewdrawTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Shared EntryViewModel for draw and entry screens
                    val entryViewModel: EntryViewModel = viewModel {
                        EntryViewModel(repository, this@MainActivity)
                    }
                    
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            val viewModel: HomeViewModel = viewModel {
                                HomeViewModel(repository)
                            }
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToDraw = {
                                    entryViewModel.reset()
                                    navController.navigate("draw")
                                },
                                onNavigateToEntry = { memoryId ->
                                    // For now, just navigate to draw
                                    // Entry detail view can be added later
                                    entryViewModel.reset()
                                    navController.navigate("draw")
                                }
                            )
                        }
                        
                        composable("draw") {
                            DrawScreen(
                                viewModel = entryViewModel,
                                onNext = {
                                    navController.navigate("entry")
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        
                        composable("entry") {
                            EntryScreen(
                                viewModel = entryViewModel,
                                onSave = {
                                    navController.popBackStack("home", inclusive = false)
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
