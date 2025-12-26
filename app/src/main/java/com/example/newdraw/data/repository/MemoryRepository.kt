package com.example.newdraw.data.repository

import com.example.newdraw.data.local.MemoryDao
import com.example.newdraw.data.local.MemoryEntity
import kotlinx.coroutines.flow.Flow

class MemoryRepository(
    private val memoryDao: MemoryDao
) {
    fun getAllMemories(): Flow<List<MemoryEntity>> = memoryDao.getAllMemories()

    fun searchMemories(query: String): Flow<List<MemoryEntity>> {
        return if (query.isBlank()) {
            memoryDao.getAllMemories()
        } else {
            memoryDao.searchMemories(query)
        }
    }

    suspend fun insertMemory(memory: MemoryEntity): Long {
        return memoryDao.insertMemory(memory)
    }

    suspend fun getMemoryById(id: Int): MemoryEntity? {
        return memoryDao.getMemoryById(id)
    }
}





