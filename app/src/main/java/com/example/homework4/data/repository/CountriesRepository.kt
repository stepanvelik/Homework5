package com.example.homework4.data.repository

import com.example.homework4.data.model.Country

interface CountriesRepository {
    suspend fun loadAll(): List<Country>
    suspend fun search(name: String): List<Country>
    suspend fun getCountryByCode(code: String): Country?
    suspend fun getFavourites(): List<Country>
    suspend fun addFavourite(country: Country)
    suspend fun removeFavourite(country: Country)
    suspend fun isFavourite(code: String): Boolean
}
