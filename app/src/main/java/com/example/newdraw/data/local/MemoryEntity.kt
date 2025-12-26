package com.example.newdraw.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_table")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val note: String,
    val tags: String,
    val imagePath: String,
    val createdAt: Long,
    val starred: Boolean = false
)





