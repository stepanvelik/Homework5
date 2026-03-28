package com.example.homework4.data.remote

import com.example.homework4.data.model.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CountriesApi {

    @GET("all")
    suspend fun getCountries(): List<CountryDto>

    @GET("name/{name}")
    suspend fun searchCountries(
        @Path("name") name: String
    ): List<CountryDto>

    @GET("alpha/{code}")
    suspend fun getCountryByCode(
        @Path("code") code: String
    ): List<CountryDto>

}