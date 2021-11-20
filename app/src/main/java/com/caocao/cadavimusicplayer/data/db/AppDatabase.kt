package com.caocao.cadavimusicplayer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.caocao.cadavimusicplayer.data.model.Favorite

@Database(
    entities = [Favorite::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getDao(): DataDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "AppDatabase.db"
            ).allowMainThreadQueries().build()
    }
}