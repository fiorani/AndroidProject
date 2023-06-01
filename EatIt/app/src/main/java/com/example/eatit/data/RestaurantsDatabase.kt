package com.example.eatit.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Restaurant::class], version = 1, exportSchema = true)
abstract class RestaurantsDatabase : RoomDatabase() {

    abstract fun itemDAO(): RestaurantsDAO

    companion object {
        @Volatile
        private var INSTANCE: RestaurantsDatabase ?= null

        fun getDatabase(context: Context): RestaurantsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantsDatabase::class.java,
                    "items_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }

}