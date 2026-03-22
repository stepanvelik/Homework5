package com.example.homework4.fakes

import com.example.homework4.data.model.Country
import com.example.homework4.data.repository.CountriesRepository

class FakeCountriesRepository : CountriesRepository {

    var loadAllData: List<Country> = emptyList()
    var loadAllError: Throwable? = null
    val searchCalls = mutableListOf<String>()

    private val favourites = mutableListOf<Country>()

    var getCountryByCodeHandler: (String) -> Country? = { null }

    override suspend fun loadAll(): List<Country> {
        loadAllError?.let { throw it }
        return loadAllData
    }

    override suspend fun search(name: String): List<Country> {
        searchCalls.add(name)
        return loadAllData.filter { it.name.contains(name, ignoreCase = true) }
    }

    override suspend fun getCountryByCode(code: String): Country? =
        getCountryByCodeHandler(code)

    override suspend fun getFavourites(): List<Country> = favourites.toList()

    override suspend fun addFavourite(country: Country) {
        if (favourites.none { it.code == country.code }) {
            favourites.add(country)
        }
    }

    override suspend fun removeFavourite(country: Country) {
        favourites.removeAll { it.code == country.code }
    }

    override suspend fun isFavourite(code: String): Boolean =
        favourites.any { it.code == code }
}
