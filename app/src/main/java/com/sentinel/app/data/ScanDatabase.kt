package com.sentinel.app.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [ScanRecord::class], version = 1, exportSchema = false)
abstract class ScanDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao
    companion object {
        @Volatile private var INSTANCE: ScanDatabase? = null
        fun getInstance(context: Context): ScanDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScanDatabase::class.java,
                    "sentinel_db"
                ).build().also { INSTANCE = it }
            }
    }
}
