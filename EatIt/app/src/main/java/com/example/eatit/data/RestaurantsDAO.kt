package com.example.eatit.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantsDAO {
    @Query("SELECT * FROM restaurants ORDER BY restaurant_name ASC")
    fun getRestaurants(): Flow<List<Restaurant>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(restaurant: Restaurant)
}
