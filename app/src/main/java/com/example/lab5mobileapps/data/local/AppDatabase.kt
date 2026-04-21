package com.example.lab5mobileapps.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lab5mobileapps.R
import com.example.lab5mobileapps.domain.model.Place
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Place::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "places_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.placeDao())
                }
            }
        }

        suspend fun populateDatabase(placeDao: PlaceDao) {
            val initialPlaces = listOf(
                Place(
                    name = "Театральна площа",
                    description = "Центральна площа міста...",
                    category = "Площа",
                    isFavourite = false,
                    rating = 4.8,
                    imageRes = R.drawable.image2
                ),
                Place(
                    name = "Театральна площа",
                    description = "Центральна площа міста...",
                    category = "Площа",
                    isFavourite = false,
                    rating = 4.8,
                    imageRes = R.drawable.image3
                ),
                Place(
                    name = "Театральна площа",
                    description = "Центральна площа міста...",
                    category = "Площа",
                    isFavourite = false,
                    rating = 4.8,
                    imageRes = R.drawable.image1
                ),
                Place(
                    name = "Театральна площа",
                    description = "Центральна площа міста...",
                    category = "Площа",
                    isFavourite = false,
                    rating = 4.8,
                    imageRes = R.drawable.image3
                ),
                Place(
                    name = "Театральна площа",
                    description = "Центральна площа міста...",
                    category = "Площа",
                    isFavourite = false,
                    rating = 4.8,
                    imageRes = R.drawable.image2
                ),
            )
            placeDao.insertAll(initialPlaces)
        }
    }
}