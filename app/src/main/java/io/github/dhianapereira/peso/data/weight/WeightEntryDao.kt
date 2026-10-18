package io.github.dhianapereira.peso.data.weight

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY dateEpochDay ASC, createdAt ASC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY dateEpochDay ASC, createdAt ASC")
    suspend fun getAll(): List<WeightEntryEntity>

    @Query("SELECT * FROM weight_entries WHERE id = :id")
    suspend fun getById(id: Long): WeightEntryEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: WeightEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<WeightEntryEntity>)

    @Update
    suspend fun update(entry: WeightEntryEntity)

    @Delete
    suspend fun delete(entry: WeightEntryEntity)

    @Query("DELETE FROM weight_entries")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(entries: List<WeightEntryEntity>) {
        clear()
        insertAll(entries)
    }
}
