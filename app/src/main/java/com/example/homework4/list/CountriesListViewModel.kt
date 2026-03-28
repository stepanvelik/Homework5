package com.example.homework4.list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework4.data.model.Country
import com.example.homework4.data.repository.CountriesRepository
import com.example.homework4.detail.CountryDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class CountriesListViewModel @Inject constructor(

    private val repository: CountriesRepository

) : ViewModel() {

    var state = mutableStateOf<CountriesUiState>(CountriesUiState.Loading)
        private set

    var detailState = mutableStateOf<CountryDetailUiState>(CountryDetailUiState.Loading)
        private set

    private var searchJob: Job? = null
    private var lastQuery: String = ""
    private var detailJob: Job? = null

    val favourites = mutableStateListOf<Country>()

    init {
        load()
        loadFavourites()
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            val list = repository.getFavourites()
            favourites.clear()
            favourites.addAll(list)
        }
    }

    fun load() {
        // Отменяем debounce-поиск, чтобы старый searchJob не успел перезаписать state после очистки строки.
        searchJob?.cancel()
        lastQuery = ""

        viewModelScope.launch {

            state.value = CountriesUiState.Loading

            try {

                val list = repository.loadAll()

                state.value =
                    if (list.isEmpty())
                        CountriesUiState.Empty
                    else
                        CountriesUiState.Success(list)

            } catch (e: Exception) {

                state.value =
                    CountriesUiState.Error("Ошибка загрузки: ${e.message ?: "неизвестная ошибка"}")

            }
        }
    }

    fun search(query: String) {

        lastQuery = query
        searchJob?.cancel()

        searchJob = viewModelScope.launch {

            delay(400)

            if (query.isBlank()) {
                state.value = CountriesUiState.Success(repository.loadAll())
                return@launch
            }

            state.value = CountriesUiState.Loading
            try {
                val result = repository.search(query)

                state.value =
                    if (result.isEmpty())
                        CountriesUiState.Empty
                    else
                        CountriesUiState.Success(result)

            } catch (e: Exception) {

                state.value =
                    CountriesUiState.Error("Ошибка поиска: ${e.message ?: "неизвестная ошибка"}")

            }
        }
    }

    fun refresh() {

        if (lastQuery.isBlank()) {
            load()
        } else {
            search(lastQuery)
        }

    }

    fun toggleFavourite(country: Country) {
        viewModelScope.launch {
            val isCurrentlyFavourite = favourites.any { it.code == country.code }
            if (isCurrentlyFavourite) {
                repository.removeFavourite(country)
                favourites.removeAll { it.code == country.code }
            } else {
                repository.addFavourite(country)
                favourites.add(country)
            }
        }
    }

    fun loadCountryByCode(code: String) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            detailState.value = CountryDetailUiState.Loading
            try {
                val country = repository.getCountryByCode(code)
                detailState.value =
                    if (country == null) CountryDetailUiState.Empty
                    else CountryDetailUiState.Success(country)
            } catch (e: Exception) {
                detailState.value =
                    CountryDetailUiState.Error("Не удалось загрузить страну: ${e.message ?: "неизвестная ошибка"}")
            }
        }
    }
}