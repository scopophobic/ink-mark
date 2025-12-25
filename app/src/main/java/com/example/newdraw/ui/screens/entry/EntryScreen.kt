package com.example.newdraw.ui.screens.entry

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newdraw.ui.theme.InkPrimary
import com.example.newdraw.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    viewModel: EntryViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val title by viewModel.title
    val note by viewModel.note
    val tags by viewModel.tags

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Entry Details") },
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
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkPrimary.copy(alpha = 0.5f),
                    focusedTextColor = InkPrimary,
                    unfocusedTextColor = InkPrimary,
                    cursorColor = InkPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { viewModel.note.value = it },
                label = { Text("Memory") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkPrimary.copy(alpha = 0.5f),
                    focusedTextColor = InkPrimary,
                    unfocusedTextColor = InkPrimary,
                    cursorColor = InkPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = { viewModel.tags.value = it },
                label = { Text("Tags (comma-separated)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkPrimary.copy(alpha = 0.5f),
                    focusedTextColor = InkPrimary,
                    unfocusedTextColor = InkPrimary,
                    cursorColor = InkPrimary
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Title is required"
                    } else {
                        isSaving = true
                        errorMessage = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InkPrimary
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Save Entry")
                }
            }
        }
        
        LaunchedEffect(isSaving) {
            if (isSaving) {
                val success = viewModel.saveEntry()
                isSaving = false
                if (success) {
                    viewModel.reset()
                    onSave()
                } else {
                    errorMessage = "Failed to save entry"
                }
            }
        }
    }
}
