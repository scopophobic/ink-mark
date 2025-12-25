package com.example.newdraw.ui.screens.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newdraw.ui.components.DrawingCanvas
import com.example.newdraw.ui.theme.InkPrimary
import com.example.newdraw.ui.theme.InkBlack
import com.example.newdraw.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawScreen(
    viewModel: EntryViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val paths by viewModel.drawingPaths
    val note by viewModel.note
    val strokeWidth by viewModel.strokeWidth

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Draw", color = InkBlack) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    titleContentColor = InkBlack
                ),
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stroke width control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Stroke:", color = InkBlack)
                Slider(
                    value = strokeWidth,
                    onValueChange = { viewModel.strokeWidth.value = it },
                    valueRange = 2f..20f,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = InkPrimary,
                        activeTrackColor = InkPrimary,
                        inactiveTrackColor = InkPrimary.copy(alpha = 0.3f)
                    )
                )
                Text(
                    text = "${strokeWidth.toInt()}px",
                    color = InkBlack,
                    modifier = Modifier.widthIn(min = 40.dp)
                )
            }

            // Square drawing box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(color = Color.White)
                    .border(2.dp, InkPrimary)
            ) {
                DrawingCanvas(
                    paths = paths,
                    currentStrokeWidth = strokeWidth,
                    onPathStart = { path -> viewModel.startPath(path) },
                    onPathUpdate = { x, y -> viewModel.updateCurrentPath(x, y) },
                    onPathEnd = { viewModel.triggerRecomposition() },
                    onCanvasSizeChange = { width, height ->
                        viewModel.canvasWidth = width
                        viewModel.canvasHeight = height
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Memory input below drawing box
            OutlinedTextField(
                value = note,
                onValueChange = { viewModel.note.value = it },
                label = { Text("Add a memory...", color = InkBlack) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkPrimary.copy(alpha = 0.5f),
                    focusedTextColor = InkBlack,
                    unfocusedTextColor = InkBlack,
                    unfocusedPlaceholderColor = InkBlack.copy(alpha = 0.6f),
                    focusedPlaceholderColor = InkBlack.copy(alpha = 0.6f),
                    focusedLabelColor = InkBlack,
                    unfocusedLabelColor = InkBlack.copy(alpha = 0.7f),
                    cursorColor = InkPrimary
                )
            )

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Save button
            Button(
                onClick = {
                    if (paths.isEmpty()) {
                        errorMessage = "Please draw something"
                    } else {
                        isSaving = true
                        errorMessage = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && paths.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InkPrimary
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White
                    )
                } else {
                    Text("Save", color = Color.White)
                }
            }
        }
        
        LaunchedEffect(isSaving) {
            if (isSaving) {
                val success = viewModel.saveEntry()
                isSaving = false
                if (success) {
                    viewModel.reset()
                    onSave()
                } else {
                    errorMessage = "Failed to save entry"
                }
            }
        }
    }
}
