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
    private const val STROKE_WIDTH = 4f

    fun renderPathsToBitmap(
        paths: List<Path>,
        backgroundColor: Color = androidx.compose.ui.graphics.Color.White
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        
        // Fill background
        canvas.drawColor(backgroundColor.toArgb())
        
        // Draw all paths
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            color = com.example.newdraw.ui.theme.InkPrimary.toArgb()
        }
        
        paths.forEach { composePath ->
            val androidPath = composePath.asAndroidPath()
            canvas.drawPath(androidPath, paint)
        }
        
        return bitmap
    }
}

