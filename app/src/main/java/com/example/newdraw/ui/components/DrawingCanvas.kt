package com.example.newdraw.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.newdraw.ui.theme.InkPrimary

@Composable
fun DrawingCanvas(
    paths: List<Path>,
    onPathStart: (Path) -> Unit,
    onPathUpdate: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val strokeWidth = 4f

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val path = Path().apply {
                            moveTo(offset.x, offset.y)
                        }
                        onPathStart(path)
                    },
                    onDrag = { change, _ ->
                        onPathUpdate(change.position.x, change.position.y)
                    }
                )
            }
    ) {
        paths.forEach { path ->
            drawPath(
                path = path,
                color = InkPrimary,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

