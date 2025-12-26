package com.example.newdraw.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.newdraw.data.local.MemoryEntity
import com.example.newdraw.ui.theme.InkBlack
import com.example.newdraw.ui.theme.InkPrimary
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    memory: MemoryEntity,
    viewModel: com.example.newdraw.viewmodel.HomeViewModel,
    onBack: () -> Unit,
    onNavigateToMemory: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentMemory by remember(memory) { mutableStateOf(memory) }
    
    // Update currentMemory when memory prop changes
    LaunchedEffect(memory) {
        currentMemory = memory
    }
    
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(currentMemory.createdAt))
    var dragOffset by remember { mutableStateOf(0f) }
    
    // Get all memories for swipe navigation
    val allMemories by viewModel.memories.collectAsStateWithLifecycle()
    val currentIndex = remember(allMemories, currentMemory) {
        allMemories.indexOfFirst { it.id == currentMemory.id }.takeIf { it >= 0 } ?: 0
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Memory", color = InkBlack) },
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
                    IconButton(
                        onClick = {
                            lifecycleOwner.lifecycleScope.launch {
                                viewModel.toggleStarred(currentMemory.id, !currentMemory.starred)
                                currentMemory = currentMemory.copy(starred = !currentMemory.starred)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (currentMemory.starred) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (currentMemory.starred) "Unstar" else "Star",
                            tint = if (currentMemory.starred) InkPrimary else InkBlack.copy(alpha = 0.5f)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(currentIndex) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(dragOffset) > 100f) {
                                if (dragOffset > 0 && currentIndex > 0) {
                                    // Swipe right - previous memory
                                    val prevMemory = allMemories[currentIndex - 1]
                                    onNavigateToMemory(prevMemory.id)
                                } else if (dragOffset < 0 && currentIndex < allMemories.size - 1) {
                                    // Swipe left - next memory
                                    val nextMemory = allMemories[currentIndex + 1]
                                    onNavigateToMemory(nextMemory.id)
                                }
                            }
                            dragOffset = 0f
                        }
                    ) { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Image with subtle background for transparent PNGs
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                ) {
                    AsyncImage(
                        model = File(currentMemory.imagePath),
                        contentDescription = currentMemory.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date
                    Text(
                        text = dateFormat.format(Date(currentMemory.createdAt)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkBlack.copy(alpha = 0.7f)
                    )

                    // Title
                    if (currentMemory.title.isNotBlank()) {
                        Text(
                            text = currentMemory.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = InkBlack
                        )
                    }

                    // Memory text
                    if (currentMemory.note.isNotBlank()) {
                        Text(
                            text = currentMemory.note,
                            style = MaterialTheme.typography.bodyLarge,
                            color = InkBlack
                        )
                    }
                }
            }
        }
    }
}

