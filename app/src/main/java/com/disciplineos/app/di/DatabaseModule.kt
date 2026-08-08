package com.disciplineos.app.di

import android.content.Context
import androidx.room.Room
import com.disciplineos.app.data.database.AppDatabase
import com.disciplineos.app.data.database.CheckoffDao
import com.disciplineos.app.data.database.DailyEntryDao
import com.disciplineos.app.data.database.ExerciseDao
import com.disciplineos.app.data.database.PlanDao
import com.disciplineos.app.data.database.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "discipline_os.db",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideDailyEntryDao(database: AppDatabase): DailyEntryDao = database.dailyEntryDao()

    @Provides
    fun providePlanDao(database: AppDatabase): PlanDao = database.planDao()

    @Provides
    fun provideExerciseDao(database: AppDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    fun provideCheckoffDao(database: AppDatabase): CheckoffDao = database.checkoffDao()
}
