package com.example.newdraw.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Insert
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Query("SELECT * FROM memory_table ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("""
        SELECT * FROM memory_table 
        WHERE title LIKE '%' || :query || '%' 
        OR note LIKE '%' || :query || '%' 
        OR tags LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchMemories(query: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memory_table WHERE id = :id")
    suspend fun getMemoryById(id: Int): MemoryEntity?
    
    @Query("UPDATE memory_table SET starred = :starred WHERE id = :id")
    suspend fun updateStarred(id: Int, starred: Boolean)
}





