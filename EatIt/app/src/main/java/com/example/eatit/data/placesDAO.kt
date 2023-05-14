package com.example.eatit.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesDAO {
    @Query("SELECT * FROM places ORDER BY place_name ASC")
    fun getPlaces(): Flow<List<Place>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: Place)
}
