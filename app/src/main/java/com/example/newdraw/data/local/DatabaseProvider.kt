package com.example.newdraw.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: MemoryDatabase? = null

    fun getDatabase(context: Context): MemoryDatabase {
        return database ?: Room.databaseBuilder(
            context.applicationContext,
            MemoryDatabase::class.java,
            "memory_database"
        )
        .fallbackToDestructiveMigration() // Recreate database on version change
        .build().also {
            database = it
        }
    }
}





