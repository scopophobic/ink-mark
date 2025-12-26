package com.example.newdraw.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MemoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MemoryDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
}





