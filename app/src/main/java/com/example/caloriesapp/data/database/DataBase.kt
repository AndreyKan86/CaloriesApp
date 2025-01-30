package com.example.caloriesapp.data.database


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.caloriesapp.data.dao.UserDao
import com.example.caloriesapp.data.model.SavedProduct

@Database(
    entities = [SavedProduct::class], // Указываем, какие таблицы есть в базе
    version = 2 // Версия базы данных
)
abstract class AppDatabase : RoomDatabase() {

    // Метод для доступа к DAO
    abstract fun userDao(): UserDao

    // Singleton для базы данных
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calories_database" // Имя базы данных
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}