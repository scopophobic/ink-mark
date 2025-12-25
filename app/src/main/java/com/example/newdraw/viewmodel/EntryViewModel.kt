package com.example.newdraw.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newdraw.data.DrawingPath
import com.example.newdraw.data.local.MemoryEntity
import com.example.newdraw.data.repository.MemoryRepository
import com.example.newdraw.utils.BitmapUtils
import com.example.newdraw.utils.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryViewModel(
    private val repository: MemoryRepository,
    private val context: Context
) : ViewModel() {
    val drawingPaths = mutableStateOf<List<DrawingPath>>(emptyList())
    
    val title = mutableStateOf("")
    val note = mutableStateOf("")
    val strokeWidth = mutableStateOf(4f)

    private var currentPathIndex: Int = -1
    private var currentPath: Path? = null
    var canvasWidth: Float = 512f
    var canvasHeight: Float = 512f
    
    fun startPath(path: Path) {
        currentPath = path
        val drawingPath = DrawingPath(path, strokeWidth.value)
        drawingPaths.value = drawingPaths.value + drawingPath
        currentPathIndex = drawingPaths.value.size - 1
    }
    
    fun updateCurrentPath(x: Float, y: Float) {
        // Update the current path directly for real-time drawing
        currentPath?.lineTo(x, y)
        // Also update the stored path
        if (currentPathIndex >= 0 && currentPathIndex < drawingPaths.value.size) {
            drawingPaths.value[currentPathIndex].path.lineTo(x, y)
        }
    }
    
    fun triggerRecomposition() {
        // Force recomposition by creating new list reference
        drawingPaths.value = drawingPaths.value.toList()
        currentPath = null
    }

    fun undoLastPath() {
        if (drawingPaths.value.isNotEmpty()) {
            drawingPaths.value = drawingPaths.value.dropLast(1)
        }
    }

    fun clearAllPaths() {
        drawingPaths.value = emptyList()
    }

    suspend fun saveDrawing(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapUtils.renderPathsToBitmap(
                    drawingPaths.value,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    backgroundColor = androidx.compose.ui.graphics.Color.Transparent
                )
                FileManager.saveBitmapToFile(context, bitmap)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveEntry(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val imagePath = saveDrawing() ?: return@withContext false
                
                val memory = MemoryEntity(
                    title = title.value.ifBlank { "Untitled" },
                    note = note.value,
                    tags = "", // Tags removed
                    imagePath = imagePath,
                    createdAt = System.currentTimeMillis()
                )
                
                repository.insertMemory(memory) > 0
            } catch (e: Exception) {
                false
            }
        }
    }

    fun reset() {
        title.value = ""
        note.value = ""
        drawingPaths.value = emptyList()
        currentPathIndex = -1
        canvasWidth = 512f
        canvasHeight = 512f
    }
}

