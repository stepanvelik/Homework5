package com.example.homework4.data.repository

import com.example.homework4.data.local.FavouriteCountryEntity
import com.example.homework4.data.local.FavouritesDao
import com.example.homework4.data.model.Country
import com.example.homework4.data.model.toDomain
import com.example.homework4.data.remote.CountriesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCountriesRepository @Inject constructor(
    private val api: CountriesApi,
    private val favouritesDao: FavouritesDao
) : CountriesRepository {

    override suspend fun loadAll(): List<Country> =
        withContext(Dispatchers.IO) {
            api.getCountries().map { it.toDomain() }
        }

    override suspend fun search(name: String): List<Country> =
        withContext(Dispatchers.IO) {
            api.searchCountries(name).map { it.toDomain() }
        }

    override suspend fun getCountryByCode(code: String): Country? =
        withContext(Dispatchers.IO) {
            api.getCountryByCode(code).firstOrNull()?.toDomain()
        }

    override suspend fun getFavourites(): List<Country> =
        withContext(Dispatchers.IO) {
            favouritesDao.getAll().map { it.toCountry() }
        }

    override suspend fun addFavourite(country: Country) =
        withContext(Dispatchers.IO) {
            favouritesDao.insert(FavouriteCountryEntity.fromCountry(country))
        }

    override suspend fun removeFavourite(country: Country) =
        withContext(Dispatchers.IO) {
            favouritesDao.delete(FavouriteCountryEntity.fromCountry(country))
        }

    override suspend fun isFavourite(code: String): Boolean =
        withContext(Dispatchers.IO) {
            favouritesDao.getByCode(code) != null
        }
}
