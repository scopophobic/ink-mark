package com.example.newdraw.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.newdraw.data.DrawingPath
import com.example.newdraw.ui.theme.InkPrimary

@Composable
fun DrawingCanvas(
    paths: List<DrawingPath>,
    currentStrokeWidth: Float,
    onPathStart: (Path) -> Unit,
    onPathUpdate: (Float, Float) -> Unit,
    onPathEnd: () -> Unit = {},
    onCanvasSizeChange: (Float, Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var updateCounter by remember { mutableStateOf(0) }
    var lastUpdateTime by remember { mutableStateOf(0L) }
    var canvasSizeNotified by remember { mutableStateOf(false) }
    var dragStartPosition by remember { mutableStateOf<androidx.compose.ui.geometry.Offset?>(null) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartPosition = offset
                        val path = Path().apply {
                            moveTo(offset.x, offset.y)
                        }
                        currentPath = path
                        updateCounter++
                        onPathStart(path)
                    },
                    onDrag = { change, _ ->
                        val now = System.currentTimeMillis()
                        // Update every frame for smooth real-time drawing
                        if (now - lastUpdateTime >= 0) {
                            currentPath?.lineTo(change.position.x, change.position.y)
                            onPathUpdate(change.position.x, change.position.y)
                            // Force recomposition by updating counter - this triggers Canvas redraw
                            updateCounter = (updateCounter + 1) % 10000
                            lastUpdateTime = now
                        }
                    },
                    onDragEnd = {
                        // Check if it's a dot (very short drag or no movement)
                        dragStartPosition?.let { start ->
                            currentPath?.let { path ->
                                val bounds = path.getBounds()
                                val distance = kotlin.math.sqrt(
                                    (bounds.center.x - start.x) * (bounds.center.x - start.x) + 
                                    (bounds.center.y - start.y) * (bounds.center.y - start.y)
                                )
                                
                                // If distance is very small (< 3px), it's a dot - convert to filled circle
                                if (distance < 3f) {
                                    val radius = currentStrokeWidth / 2
                                    val dotPath = Path().apply {
                                        addOval(
                                            androidx.compose.ui.geometry.Rect(
                                                start.x - radius,
                                                start.y - radius,
                                                start.x + radius,
                                                start.y + radius
                                            )
                                        )
                                    }
                                    // Update the path in ViewModel to be a dot
                                    currentPath = dotPath
                                    // Force update
                                    updateCounter++
                                }
                            }
                        }
                        dragStartPosition = null
                        currentPath = null
                        updateCounter++
                        onPathEnd()
                    }
                )
            }
    ) {
        // Notify canvas size only once
        if (!canvasSizeNotified) {
            onCanvasSizeChange(size.width, size.height)
            canvasSizeNotified = true
        }
        
        // Draw all completed paths with their individual stroke widths
        paths.forEach { drawingPath ->
            val bounds = drawingPath.path.getBounds()
            // Check if it's a dot (oval/circle)
            if (bounds.width > 0 && bounds.height > 0 && 
                kotlin.math.abs(bounds.width - bounds.height) < 1f &&
                bounds.width < drawingPath.strokeWidth * 2) {
                // It's a dot, draw as filled circle
                drawCircle(
                    color = InkPrimary,
                    radius = drawingPath.strokeWidth / 2,
                    center = androidx.compose.ui.geometry.Offset(
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
        
        // Draw current path in real-time with current stroke width
        // Reading updateCounter ensures Canvas recomposes when it changes
        val counter = updateCounter
        currentPath?.let { path ->
            val bounds = path.getBounds()
            // Check if it's a dot
            if (bounds.width > 0 && bounds.height > 0 && 
                kotlin.math.abs(bounds.width - bounds.height) < 1f &&
                bounds.width < currentStrokeWidth * 2) {
                // It's a dot, draw as filled circle
                drawCircle(
                    color = InkPrimary,
                    radius = currentStrokeWidth / 2,
                    center = androidx.compose.ui.geometry.Offset(
                        bounds.center.x,
                        bounds.center.y
                    )
                )
            } else {
                // It's a stroke, draw as path
                drawPath(
                    path = path,
                    color = InkPrimary,
                    style = Stroke(
                        width = currentStrokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
    
    // Force recomposition when updateCounter changes
    LaunchedEffect(updateCounter) {
        // This ensures the Canvas recomposes
    }
}

