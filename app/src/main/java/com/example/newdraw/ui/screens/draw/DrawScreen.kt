package com.example.newdraw.ui.screens.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newdraw.ui.components.DrawingCanvas
import com.example.newdraw.ui.theme.InkPrimary
import com.example.newdraw.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    viewModel: EntryViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paths by viewModel.drawingPaths

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Draw") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back", color = InkPrimary)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.undoLastPath() }) {
                        Text("Undo", color = InkPrimary)
                    }
                    TextButton(onClick = { viewModel.clearAllPaths() }) {
                        Text("Clear", color = InkPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .background(color = androidx.compose.ui.graphics.Color.White)
            ) {
                DrawingCanvas(
                    paths = paths,
                    onPathStart = { path -> viewModel.startPath(path) },
                    onPathUpdate = { x, y -> viewModel.updateCurrentPath(x, y) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = paths.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InkPrimary
                )
            ) {
                Text("Next")
            }
        }
    }
}


