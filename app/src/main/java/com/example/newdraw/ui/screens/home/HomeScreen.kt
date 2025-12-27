package com.example.newdraw.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import com.example.newdraw.ui.components.FlowerIcon
import com.example.newdraw.ui.components.MushroomIcon
import com.example.newdraw.ui.components.PlantIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.newdraw.data.local.MemoryEntity
import com.example.newdraw.ui.theme.InkBlack
import com.example.newdraw.ui.theme.InkPrimary
import com.example.newdraw.viewmodel.HomeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDraw: () -> Unit,
    onNavigateToEntry: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    
    // Get current year for consistent date calculation
    val currentYear = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    }
    
    // Create a map of day index to memory for quick lookup
    // Only include memories from the current year to ensure date consistency
    // Prefer starred memories when multiple exist for the same day
    val memoryMap = remember(memories, currentYear) {
        memories
            .filter { memory ->
                // Only include memories from the current year
                val calendar = java.util.Calendar.getInstance().apply {
                    timeInMillis = memory.createdAt
                }
                calendar.get(java.util.Calendar.YEAR) == currentYear
            }
            .groupBy { memory ->
                // Calculate day of year from timestamp (0-364)
                val calendar = java.util.Calendar.getInstance().apply {
                    timeInMillis = memory.createdAt
                }
                calendar.get(java.util.Calendar.DAY_OF_YEAR) - 1
            }
            .mapValues { (_, memoriesForDay) ->
                // If multiple memories for same day, prefer starred one, otherwise most recent
                memoriesForDay.sortedWith(
                    compareByDescending<MemoryEntity> { it.starred }
                        .thenByDescending { it.createdAt }
                ).first()
            }
    }
    
    var isGridView by remember { mutableStateOf(true) } // true = grid view, false = create memory

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Bottom toggle bar
            BottomToggleBar(
                isGridView = isGridView,
                onToggle = { isGridView = !isGridView },
                onNavigateToDraw = onNavigateToDraw
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Year label - styled as a button
            val currentYear = remember {
                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = InkPrimary,
                    modifier = Modifier.padding(horizontal = 80.dp)
                ) {
                    Text(
                        text = currentYear.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (memories.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No memories yet.",
                            style = MaterialTheme.typography.titleLarge,
                            color = InkBlack
                        )
                        Text(
                            text = "Tap the + button to start your memory garden!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlack.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                if (isGridView) {
                    // Main grid - shows only images for days with memories
                    YearGrid(
                        memoryMap = memoryMap,
                        onTap = { dayIndex ->
                            // Navigate to detail view
                            memoryMap[dayIndex]?.let { memory ->
                                onNavigateToEntry(memory.id)
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                } else {
                    // Create memory view - navigate to draw screen
                    LaunchedEffect(Unit) {
                        onNavigateToDraw()
                        isGridView = true // Reset to grid view after navigation
                    }
                }
            }
        }
    }
}

@Composable
fun YearGrid(
    memoryMap: Map<Int, MemoryEntity>,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 365 days in a year, arranged in a grid
    // 16 columns
    val columns = 16
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy((-2).dp), // Negative spacing for overlap
        verticalArrangement = Arrangement.spacedBy((-2).dp) // Negative spacing for overlap
    ) {
        itemsIndexed((0 until 365).toList()) { index, dayIndex ->
            val hasMemory = memoryMap.containsKey(dayIndex)
            
            if (hasMemory) {
                // Show image for days with memories
                memoryMap[dayIndex]?.let { memory ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .pointerInput(dayIndex) {
                                detectTapGestures {
                                    // Tap: navigate to detail view
                                    onTap(dayIndex)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Image - enhanced for better visibility in grid
                        // Use ColorFilter to increase contrast and make it bolder
                        val colorMatrix = remember {
                            // Much higher contrast for bold, raw aesthetic
                            // Contrast: 1.8 = 80% more contrast for very bold lines
                            val contrast = 1.8f
                            val brightness = -0.15f // Darker for more visibility
                            ColorMatrix(floatArrayOf(
                                contrast, 0f, 0f, 0f, brightness,
                                0f, contrast, 0f, 0f, brightness,
                                0f, 0f, contrast, 0f, brightness,
                                0f, 0f, 0f, 1f, 0f
                            ))
                        }
                        
                        AsyncImage(
                            model = File(memory.imagePath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop, // Crop to fill more space
                            colorFilter = ColorFilter.colorMatrix(colorMatrix),
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(1.5f) // Scale up 50% to make bigger and overlap
                        )
                    }
                }
            } else {
                // Empty day - show a small dot
                Box(
                    modifier = Modifier
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                color = InkPrimary.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun BottomToggleBar(
    isGridView: Boolean,
    onToggle: () -> Unit,
    onNavigateToDraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 72.dp), // Moved up by 2cm (56dp) + original 16dp
        contentAlignment = Alignment.Center
    ) {
        // Segmented control background - rounded with subtle shadow
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .width(220.dp)
                .height(52.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Grid view segment (left - active state shows 3 icons)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = if (isGridView) InkPrimary else Color.Transparent,
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                bottomStart = 12.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .pointerInput(Unit) {
                            detectTapGestures {
                                if (!isGridView) {
                                    onToggle()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isGridView) {
                        // Show three icons: flower, mushroom, plant
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FlowerIcon(color = Color.White, modifier = Modifier.size(14.dp))
                            MushroomIcon(color = Color.White, modifier = Modifier.size(14.dp))
                            PlantIcon(color = Color.White, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        // Inactive state - show single flower
                        FlowerIcon(color = InkPrimary, modifier = Modifier.size(18.dp))
                    }
                }
                
                // Create memory segment (right - inactive state shows single flower)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = if (!isGridView) InkPrimary else Color.Transparent,
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 12.dp,
                                bottomEnd = 12.dp
                            )
                        )
                        .pointerInput(Unit) {
                            detectTapGestures {
                                if (isGridView) {
                                    onToggle()
                                    onNavigateToDraw()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isGridView) {
                        // Active state - show single flower
                        FlowerIcon(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        // Inactive state - show single flower
                        FlowerIcon(color = InkPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
