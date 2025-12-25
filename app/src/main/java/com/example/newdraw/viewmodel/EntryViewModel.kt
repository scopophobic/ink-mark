package com.example.newdraw.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val drawingPaths = mutableStateOf<List<Path>>(emptyList())
    
    val title = mutableStateOf("")
    val note = mutableStateOf("")
    val tags = mutableStateOf("")

    private var currentPath: Path? = null
    
    fun startPath(path: Path) {
        currentPath = path
        drawingPaths.value = drawingPaths.value + path
    }
    
    fun updateCurrentPath(x: Float, y: Float) {
        currentPath?.lineTo(x, y)
        // Force recomposition by updating the list reference
        drawingPaths.value = drawingPaths.value.toList()
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
                    backgroundColor = androidx.compose.ui.graphics.Color.White
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
                    title = title.value,
                    note = note.value,
                    tags = tags.value,
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
        tags.value = ""
        drawingPaths.value = emptyList()
    }
}

