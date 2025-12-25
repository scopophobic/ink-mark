package com.example.newdraw.utils

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb

object BitmapUtils {
    private const val CANVAS_SIZE = 512

    fun renderPathsToBitmap(
        paths: List<com.example.newdraw.data.DrawingPath>,
        canvasWidth: Float = CANVAS_SIZE.toFloat(),
        canvasHeight: Float = CANVAS_SIZE.toFloat(),
        backgroundColor: Color = androidx.compose.ui.graphics.Color.Transparent
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888)
        // Erase the bitmap to make it transparent
        bitmap.eraseColor(android.graphics.Color.TRANSPARENT)
        val canvas = AndroidCanvas(bitmap)
        
        // Only draw background if it's not transparent
        if (backgroundColor != androidx.compose.ui.graphics.Color.Transparent) {
            canvas.drawColor(backgroundColor.toArgb())
        }
        
        // Calculate scale factor to fit the drawing into 512x512
        val scaleX = CANVAS_SIZE.toFloat() / canvasWidth
        val scaleY = CANVAS_SIZE.toFloat() / canvasHeight
        val scale = minOf(scaleX, scaleY) // Use minimum to maintain aspect ratio
        
        // Apply scale transformation
        canvas.save()
        canvas.scale(scale, scale)
        
        // Draw each path with its own stroke width
        paths.forEach { drawingPath ->
            val scaledStrokeWidth = drawingPath.strokeWidth * scale
            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                this.strokeWidth = scaledStrokeWidth
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                color = com.example.newdraw.ui.theme.InkPrimary.toArgb()
            }
            val androidPath = drawingPath.path.asAndroidPath()
            canvas.drawPath(androidPath, paint)
        }
        
        canvas.restore()
        
        return bitmap
    }
}

