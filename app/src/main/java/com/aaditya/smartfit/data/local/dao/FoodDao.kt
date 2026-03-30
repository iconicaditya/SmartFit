package com.aaditya.smartfit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aaditya.smartfit.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY consumedAtMillis DESC")
    fun observeAll(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): FoodEntity?

    @Query(
        """
        SELECT * FROM foods
        WHERE consumedAtMillis BETWEEN :startMillis AND :endMillis
        ORDER BY consumedAtMillis DESC
        """
    )
    fun observeByDateRange(startMillis: Long, endMillis: Long): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FoodEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodEntity>)

    @Update
    suspend fun update(item: FoodEntity)

    @Delete
    suspend fun delete(item: FoodEntity)

    @Query("DELETE FROM foods WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun count(): Int
}

