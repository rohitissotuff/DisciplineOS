package com.disciplineos.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEntryDao {
    @Query("SELECT * FROM daily_entries WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<DailyEntryEntity?>

    @Query("SELECT * FROM daily_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyEntryEntity?

    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<DailyEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: DailyEntryEntity)
}

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans ORDER BY isRest ASC, name ASC")
    fun observeAll(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans ORDER BY isRest ASC, name ASC")
    suspend fun getAll(): List<PlanEntity>

    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<PlanEntity?>

    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plan: PlanEntity): Long

    @Update
    suspend fun update(plan: PlanEntity)

    @Query("DELETE FROM plans WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE planId = :planId ORDER BY sortOrder ASC, id ASC")
    fun observeForPlan(planId: Long): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE planId = :planId ORDER BY sortOrder ASC, id ASC")
    suspend fun getForPlan(planId: Long): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ExerciseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(exercises: List<ExerciseEntity>)

    @Query("DELETE FROM exercises WHERE planId = :planId")
    suspend fun deleteForPlan(planId: Long)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule")
    fun observeAll(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedule")
    suspend fun getAll(): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: ScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<ScheduleEntity>)

    @Query("DELETE FROM schedule WHERE weekday = :weekday")
    suspend fun clearWeekday(weekday: Int)
}

@Dao
interface CheckoffDao {
    @Query("SELECT * FROM checkoffs WHERE date = :date")
    fun observeForDate(date: String): Flow<List<CheckoffEntity>>

    @Query("SELECT * FROM checkoffs WHERE date = :date")
    suspend fun getForDate(date: String): List<CheckoffEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(checkoff: CheckoffEntity)

    @Transaction
    suspend fun setCompleted(date: String, exerciseId: Long, completed: Boolean) {
        upsert(CheckoffEntity(date = date, exerciseId = exerciseId, completed = completed))
    }
}
