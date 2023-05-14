package com.example.eatit.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Place::class], version = 1, exportSchema = true)
abstract class PlacesDatabase : RoomDatabase() {

    abstract fun itemDAO(): PlacesDAO

    companion object {
        @Volatile
        private var INSTANCE: PlacesDatabase ?= null

        fun getDatabase(context: Context): PlacesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlacesDatabase::class.java,
                    "items_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }

}