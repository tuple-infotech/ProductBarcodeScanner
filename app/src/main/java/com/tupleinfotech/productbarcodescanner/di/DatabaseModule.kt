package com.tupleinfotech.productbarcodescanner.di

import android.content.Context
import androidx.room.Room
import com.jmsc.postab.db.AddHostDao
import com.jmsc.postab.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent ::class)
@Module
class DatabaseModule {
    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): AddHostDao {
        return appDatabase.addHostDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "cts_db"
        ).fallbackToDestructiveMigration().build()
    }
}