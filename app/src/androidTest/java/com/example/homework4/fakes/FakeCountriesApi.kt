package com.example.homework4.fakes

import com.example.homework4.data.model.CountryDto
import com.example.homework4.data.remote.CountriesApi

class FakeCountriesApi : CountriesApi {

    var countries: List<CountryDto> = emptyList()
    var searchMap: Map<String, List<CountryDto>> = emptyMap()
    var byCode: Map<String, List<CountryDto>> = emptyMap()

    override suspend fun getCountries(): List<CountryDto> = countries

    override suspend fun searchCountries(name: String): List<CountryDto> =
        searchMap[name] ?: emptyList()

    override suspend fun getCountryByCode(code: String): List<CountryDto> =
        byCode[code] ?: emptyList()
}
