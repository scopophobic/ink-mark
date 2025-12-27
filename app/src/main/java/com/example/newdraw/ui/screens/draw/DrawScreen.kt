package com.example.newdraw.ui.screens.draw

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newdraw.ui.components.DrawingCanvas
import com.example.newdraw.ui.theme.InkPrimary
import com.example.newdraw.ui.theme.InkBlack
import com.example.newdraw.viewmodel.EntryViewModel
import java.text.SimpleDateFormat
import java.util.*

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
    var isMemoryFocused by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }
    
    val paths by viewModel.drawingPaths
    val note by viewModel.note
    val strokeWidth by viewModel.strokeWidth
    
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    
    // Get current date for display
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val currentDate = remember { dateFormat.format(Date()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    // Day and date - clean text
                    Text(
                        text = currentDate,
                        color = InkBlack,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
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
                    if (!isSaved) {
                        TextButton(onClick = { viewModel.undoLastPath() }) {
                            Text("Undo", color = InkPrimary)
                        }
                        TextButton(onClick = { viewModel.clearAllPaths() }) {
                            Text("Clear", color = InkPrimary)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stroke width control - slider only, no text
            Slider(
                value = strokeWidth,
                onValueChange = { if (!isSaved) viewModel.strokeWidth.value = it },
                valueRange = 5f..30f, // Wider range from small to big
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                enabled = !isSaved,
                colors = SliderDefaults.colors(
                    thumbColor = InkPrimary,
                    activeTrackColor = InkPrimary,
                    inactiveTrackColor = InkPrimary.copy(alpha = 0.3f)
                )
            )

            // Drawing canvas - animated shrink/expand
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isMemoryFocused) {
                            Modifier.height(80.dp) // Small height when focused
                        } else {
                            Modifier.aspectRatio(1f) // Full size when not focused
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isMemoryFocused,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300)) using
                        SizeTransform(clip = false)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { focused ->
                    if (focused) {
                        // Small thumbnail preview when memory is focused
                        Box(
                            modifier = Modifier
                                .size(80.dp) // Small 2x2 cube size
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(enabled = !isSaved) {
                                    isMemoryFocused = false
                                    focusManager.clearFocus() // Clear focus from text field
                                }
                        ) {
                        // Simple preview canvas - just shows the drawing scaled down
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (paths.isNotEmpty() && viewModel.canvasWidth > 0 && viewModel.canvasHeight > 0) {
                                val scaleX = size.width / viewModel.canvasWidth
                                val scaleY = size.height / viewModel.canvasHeight
                                val scale = minOf(scaleX, scaleY)
                                
                                // Center the drawing
                                val scaledWidth = viewModel.canvasWidth * scale
                                val scaledHeight = viewModel.canvasHeight * scale
                                val offsetX = (size.width - scaledWidth) / 2
                                val offsetY = (size.height - scaledHeight) / 2
                                
                                translate(offsetX, offsetY) {
                                    scale(scale) {
                                        // Draw all paths
                                        paths.forEach { drawingPath ->
                                            val bounds = drawingPath.path.getBounds()
                                            // Check if it's a dot
                                            if (bounds.width > 0 && bounds.height > 0 && 
                                                kotlin.math.abs(bounds.width - bounds.height) < 1f &&
                                                bounds.width < drawingPath.strokeWidth * 2) {
                                                // It's a dot, draw as filled circle
                                                drawCircle(
                                                    color = InkPrimary,
                                                    radius = drawingPath.strokeWidth / 2,
                                                    center = Offset(
                                                        bounds.center.x,
                                                        bounds.center.y
                                                    )
                                                )
                                            } else {
                                                // It's a stroke, draw as path
                                                drawPath(
                                                    path = drawingPath.path,
                                                    color = InkPrimary,
                                                    style = Stroke(
                                                        width = drawingPath.strokeWidth,
                                                        cap = StrokeCap.Round,
                                                        join = StrokeJoin.Round
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    } else {
                        // Full size canvas
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
                                onPathStart = { path -> if (!isSaved) viewModel.startPath(path) },
                                onPathUpdate = { x, y -> if (!isSaved) viewModel.updateCurrentPath(x, y) },
                                onPathEnd = { if (!isSaved) viewModel.triggerRecomposition() },
                                onCanvasSizeChange = { width, height ->
                                    viewModel.canvasWidth = width
                                    viewModel.canvasHeight = height
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Memory input - no box, blends with background, primary color only
            BasicTextField(
                value = note,
                onValueChange = { if (!isSaved) viewModel.note.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isMemoryFocused = focusState.isFocused
                    },
                enabled = !isSaved,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = InkPrimary,
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (note.isEmpty()) {
                            Text(
                                text = "Add memory",
                                color = InkPrimary.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                },
                minLines = 3,
                maxLines = 10
            )

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Save button
            if (!isSaved) {
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
        }
        
        LaunchedEffect(isSaving) {
            if (isSaving) {
                val success = viewModel.saveEntry()
                isSaving = false
                if (success) {
                    isSaved = true
                    isMemoryFocused = false
                    viewModel.reset()
                    onSave()
                } else {
                    errorMessage = "Failed to save entry"
                }
            }
        }
    }
}
