package com.aaditya.smartfit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aaditya.smartfit.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): ActivityEntity?

    @Query(
        """
        SELECT * FROM activities
        WHERE timestampMillis BETWEEN :startMillis AND :endMillis
        ORDER BY timestampMillis DESC
        """
    )
    fun observeByDateRange(startMillis: Long, endMillis: Long): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ActivityEntity>)

    @Update
    suspend fun update(item: ActivityEntity)

    @Delete
    suspend fun delete(item: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun count(): Int
}

