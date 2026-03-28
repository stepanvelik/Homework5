package com.example.homework4.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavouriteCountryEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favouritesDao(): FavouritesDao
}