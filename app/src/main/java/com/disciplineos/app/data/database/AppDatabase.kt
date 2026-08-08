package com.disciplineos.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        DailyEntryEntity::class,
        PlanEntity::class,
        ExerciseEntity::class,
        ScheduleEntity::class,
        CheckoffEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyEntryDao(): DailyEntryDao
    abstract fun planDao(): PlanDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun checkoffDao(): CheckoffDao
}
