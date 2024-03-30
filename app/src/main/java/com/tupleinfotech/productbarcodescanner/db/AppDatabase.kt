package com.jmsc.postab.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AddHost::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun addHostDao(): AddHostDao
}