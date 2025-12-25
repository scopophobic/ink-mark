package com.example.newdraw.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.newdraw.data.local.MemoryEntity
import com.example.newdraw.ui.theme.InkBlack
import com.example.newdraw.ui.theme.InkPrimary
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    memory: MemoryEntity,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(memory.createdAt))

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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    model = File(memory.imagePath),
                    contentDescription = memory.title,
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
                    text = dateString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBlack.copy(alpha = 0.7f)
                )

                // Title
                if (memory.title.isNotBlank()) {
                    Text(
                        text = memory.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = InkBlack
                    )
                }

                // Memory text
                if (memory.note.isNotBlank()) {
                    Text(
                        text = memory.note,
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkBlack
                    )
                }
            }
        }
    }
}

