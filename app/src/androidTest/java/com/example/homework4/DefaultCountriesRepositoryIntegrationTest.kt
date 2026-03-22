package com.example.homework4

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.homework4.data.local.AppDatabase
import com.example.homework4.data.model.CountryDto
import com.example.homework4.data.model.FlagsDto
import com.example.homework4.data.model.NameDto
import com.example.homework4.data.repository.DefaultCountriesRepository
import com.example.homework4.fakes.FakeCountriesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultCountriesRepositoryIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: com.example.homework4.data.local.FavouritesDao
    private lateinit var api: FakeCountriesApi
    private lateinit var repo: DefaultCountriesRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.favouritesDao()
        api = FakeCountriesApi()
        repo = DefaultCountriesRepository(api, dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addFavouriteTwice_readsSingleRowWithoutDuplicates() = runBlocking {
        val country = TestCountries.alpha
        repo.addFavourite(country)
        repo.addFavourite(country)
        val list = repo.getFavourites()
        assertEquals(1, list.size)
        assertEquals(country.code, list[0].code)
    }

    @Test
    fun writeFavouriteReadBack_preservesFields() = runBlocking {
        val country = TestCountries.beta
        repo.addFavourite(country)
        val loaded = repo.getFavourites().single()
        assertEquals(country.population, loaded.population)
        assertEquals(country.flagUrl, loaded.flagUrl)
    }

    @Test
    fun loadAll_viaRetrofitMapping_returnsDomainModels() = runBlocking {
        api.countries = listOf(
            CountryDto(
                cca2 = "ZZ",
                name = NameDto("Zed"),
                capital = listOf("Z-Cap"),
                region = "Z",
                population = 42L,
                flags = FlagsDto("https://z.png")
            )
        )
        val list = repo.loadAll()
        assertEquals(1, list.size)
        assertEquals("ZZ", list[0].code)
        assertEquals("Zed", list[0].name)
    }
}
