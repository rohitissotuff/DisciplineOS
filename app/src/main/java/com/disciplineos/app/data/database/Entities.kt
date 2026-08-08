package com.disciplineos.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isRest: Boolean = false,
)

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("planId")],
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val name: String,
    val detail: String = "",
    val sortOrder: Int = 0,
)

@Entity(tableName = "schedule")
data class ScheduleEntity(
    /** 1 = Monday … 7 = Sunday */
    @PrimaryKey val weekday: Int,
    val planId: Long,
)

@Entity(
    tableName = "checkoffs",
    primaryKeys = ["date", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("exerciseId"), Index("date")],
)
data class CheckoffEntity(
    val date: String,
    val exerciseId: Long,
    val completed: Boolean = false,
)

@Entity(tableName = "daily_entries")
data class DailyEntryEntity(
    @PrimaryKey val date: String,
    val sleepHours: Float = 0f,
    val proteinGrams: Float = 0f,
    val waterLiters: Float = 0f,
    val disciplineCheck: Boolean = false,
    val workoutPoints: Int = 0,
    val score: Int = 0,
)
