package com.example.newdraw.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FlowerIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 4
        
        // Draw 5 petals
        for (i in 0 until 5) {
            val angle = (i * 72f - 90f) * Math.PI / 180f
            val petalX = centerX + radius * 1.5f * Math.cos(angle).toFloat()
            val petalY = centerY + radius * 1.5f * Math.sin(angle).toFloat()
            
            drawCircle(
                color = color,
                radius = radius * 0.6f,
                center = Offset(petalX, petalY),
                style = Stroke(width = 1.5f)
            )
        }
        
        // Draw center circle
        drawCircle(
            color = color,
            radius = radius * 0.4f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.5f)
        )
        
        // Draw stem
        drawLine(
            color = color,
            start = Offset(centerX, centerY + radius * 0.8f),
            end = Offset(centerX, size.height - 2.dp.toPx()),
            strokeWidth = 1.5f
        )
        
        // Draw two small leaves
        drawLine(
            color = color,
            start = Offset(centerX, centerY + radius * 1.2f),
            end = Offset(centerX - 3.dp.toPx(), centerY + radius * 1.5f),
            strokeWidth = 1.5f
        )
        drawLine(
            color = color,
            start = Offset(centerX, centerY + radius * 1.2f),
            end = Offset(centerX + 3.dp.toPx(), centerY + radius * 1.5f),
            strokeWidth = 1.5f
        )
    }
}

@Composable
fun MushroomIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val centerX = size.width / 2
        val bottomY = size.height - 2.dp.toPx()
        val capWidth = size.width * 0.6f
        val capHeight = size.height * 0.4f
        
        // Draw mushroom cap (semi-circle)
        val capPath = Path().apply {
            moveTo(centerX - capWidth / 2, bottomY - capHeight)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    centerX - capWidth / 2,
                    bottomY - capHeight * 2,
                    centerX + capWidth / 2,
                    bottomY - capHeight
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(centerX - capWidth / 2, bottomY - capHeight)
        }
        drawPath(capPath, color, style = Stroke(width = 1.5f))
        
        // Draw spots on cap
        drawCircle(
            color = color,
            radius = 2.dp.toPx(),
            center = Offset(centerX - capWidth / 4, bottomY - capHeight * 1.3f),
            style = Stroke(width = 1.5f)
        )
        drawCircle(
            color = color,
            radius = 1.5.dp.toPx(),
            center = Offset(centerX + capWidth / 4, bottomY - capHeight * 1.4f),
            style = Stroke(width = 1.5f)
        )
        
        // Draw stem
        drawLine(
            color = color,
            start = Offset(centerX, bottomY - capHeight),
            end = Offset(centerX, bottomY),
            strokeWidth = 1.5f
        )
    }
}

@Composable
fun PlantIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val centerX = size.width / 2
        val bottomY = size.height - 2.dp.toPx()
        
        // Draw stem
        drawLine(
            color = color,
            start = Offset(centerX, bottomY),
            end = Offset(centerX, size.height * 0.3f),
            strokeWidth = 1.5f
        )
        
        // Draw leaves
        val leafSize = 4.dp.toPx()
        // Left leaf
        drawLine(
            color = color,
            start = Offset(centerX, size.height * 0.5f),
            end = Offset(centerX - leafSize, size.height * 0.4f),
            strokeWidth = 1.5f
        )
        drawLine(
            color = color,
            start = Offset(centerX, size.height * 0.5f),
            end = Offset(centerX - leafSize * 0.7f, size.height * 0.3f),
            strokeWidth = 1.5f
        )
        // Right leaf
        drawLine(
            color = color,
            start = Offset(centerX, size.height * 0.5f),
            end = Offset(centerX + leafSize, size.height * 0.4f),
            strokeWidth = 1.5f
        )
        drawLine(
            color = color,
            start = Offset(centerX, size.height * 0.5f),
            end = Offset(centerX + leafSize * 0.7f, size.height * 0.3f),
            strokeWidth = 1.5f
        )
    }
}

