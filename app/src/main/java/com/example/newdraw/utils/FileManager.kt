package com.example.newdraw.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object FileManager {
    private const val DRAWINGS_FOLDER = "drawings"
    private const val FILE_PREFIX = "drawing_"
    private const val FILE_EXTENSION = ".png"

    fun getDrawingsDirectory(context: Context): File {
        val dir = File(context.filesDir, DRAWINGS_FOLDER)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun generateFileName(): String {
        val timestamp = System.currentTimeMillis()
        return "$FILE_PREFIX$timestamp$FILE_EXTENSION"
    }

    fun getDrawingFile(context: Context, fileName: String): File {
        return File(getDrawingsDirectory(context), fileName)
    }

    suspend fun saveBitmapToFile(context: Context, bitmap: android.graphics.Bitmap): String {
        val fileName = generateFileName()
        val file = getDrawingFile(context, fileName)
        
        FileOutputStream(file).use { out ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
        }
        
        return file.absolutePath
    }
}





