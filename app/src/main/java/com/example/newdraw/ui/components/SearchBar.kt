package com.example.newdraw.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newdraw.ui.theme.InkPrimary
import com.example.newdraw.ui.theme.InkBlack
import com.example.newdraw.ui.theme.InkSurface

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { 
            Text(
                "Search memories...", 
                color = InkBlack.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            ) 
        },
        shape = RoundedCornerShape(8.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = InkBlack),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InkPrimary,
            unfocusedBorderColor = InkPrimary.copy(alpha = 0.5f),
            focusedTextColor = InkBlack,
            unfocusedTextColor = InkBlack,
            unfocusedPlaceholderColor = InkBlack.copy(alpha = 0.6f),
            focusedPlaceholderColor = InkBlack.copy(alpha = 0.6f),
            focusedLabelColor = InkBlack,
            unfocusedLabelColor = InkBlack.copy(alpha = 0.7f),
            cursorColor = InkPrimary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}




