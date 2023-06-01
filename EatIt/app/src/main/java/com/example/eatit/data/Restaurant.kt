package com.example.eatit.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "restaurants")
data class Restaurant (
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "restaurant_name")
    var restaurantName: String,

    @ColumnInfo(name = "restaurant_address")
    var restaurantAddress: String,

    @ColumnInfo(name = "restaurant_description")
    var restaurantDescription: String,

    @ColumnInfo(name = "travel_photo")
    var restaurantPhoto: String
)